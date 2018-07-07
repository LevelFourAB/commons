package se.l4.commons.serialization.internal.reflection;

import java.beans.ConstructorProperties;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.ResolvedConstructor;
import com.google.common.base.Defaults;
import com.google.common.base.MoreObjects;
import com.google.common.primitives.Primitives;

import se.l4.commons.serialization.Expose;
import se.l4.commons.serialization.Factory;
import se.l4.commons.serialization.SerializationException;
import se.l4.commons.serialization.SerializerCollection;
import se.l4.commons.types.InstanceException;

/**
 * Factory that can be used to create an instance of a certain object.
 *
 * @author Andreas Holstenson
 *
 */
public class FactoryDefinition<T>
{
	private final SerializerCollection collection;
	private final Constructor<?> raw;
	final Argument[] arguments;

	private final boolean hasSerializedFields;
	private final boolean isInjectable;

	public FactoryDefinition(
		SerializerCollection collection,
		Constructor<?> raw,
		Argument[] arguments,
		boolean hasSerializedFields,
		boolean isInjectable
	)
	{
		this.collection = collection;
		this.raw = raw;
		this.arguments = arguments;
		this.hasSerializedFields = hasSerializedFields;
		this.isInjectable = isInjectable;
	}

	public static <T> FactoryDefinition<T> resolve(
		SerializerCollection collection,
		se.l4.commons.serialization.spi.Type parentType,
		Map<String, FieldDefinition> fields,
		Map<String, FieldDefinition> nonRenamed,
		ResolvedConstructor constructor
	)
	{
		List<Argument> args = new ArrayList<>();

		Constructor<?> raw = constructor.getRawMember();

		ConstructorProperties cp = raw.getAnnotation(ConstructorProperties.class);
		String[] names = cp == null ? null : cp.value();

		Annotation[][] annotations = raw.getParameterAnnotations();

		// Figure out if we are injectable
		boolean isInjectable = constructor.getArgumentCount() == 0;
		for(Annotation a : constructor.getRawMember().getAnnotations())
		{
			if(a.annotationType() == Factory.class)
			{
				isInjectable = true;
			}
			else if(a.annotationType().getSimpleName().equals("Inject"))
			{
				isInjectable = true;
			}
		}

		// Get if any fields are going to be serialized
		boolean hasSerializedFields = false;
		for(int i=0, n=constructor.getArgumentCount(); i<n; i++)
		{
			Expose expose = findExpose(annotations[i]);
			if(expose != null)
			{
				// An expose annotation is present - this definition has serializable fields
				hasSerializedFields = true;
				break;
			}
			else
			{
				// Check in the ConstructorProperties array if we have serialized fields
				if(names != null && i < names.length)
				{
					String name = names[i];
					FieldDefinition def = nonRenamed.get(name);
					if(def != null)
					{
						hasSerializedFields = true;
						break;
					}
				}
			}
		}

		if(! isInjectable && ! hasSerializedFields)
		{
			// Neither injectable nor any serialized fields - skip this definition
			return null;
		}

		for(int i=0, n=constructor.getArgumentCount(); i<n; i++)
		{
			ResolvedType type = constructor.getArgumentType(i);

			Expose expose = findExpose(annotations[i]);
			if(expose != null)
			{
				// Try to serialize
				if("".equals(expose.value()))
				{
					throw new SerializationException("The annotation @" +
						Expose.class.getSimpleName() +
						" when used in a constructor must have a name (for " +
						raw.getDeclaringClass() + ")");
				}

				FieldDefinition def = fields.get(expose.value());
				if(def == null)
				{
					throw new SerializationException(expose + " was used on a " +
							"constructor but there was no such field declared" +
							" (for " + raw.getDeclaringClass() + ")");
				}
				else if(Primitives.wrap(def.getType()) != Primitives.wrap(type.getErasedType()))
				{
					throw new SerializationException(expose + " was used on a " +
						"constructor but the type of the argument was different " +
						"from the field. The field was resolved to " +
						def.getType() + " but the argument was of type " +
						type.getErasedType() +
						" (for " + raw.getDeclaringClass() + ")");
				}

				args.add(new SerializedArgument(def.getType(), expose.value()));
			}
			else
			{
				if(names != null && i < names.length)
				{
					String name = names[i];
					FieldDefinition def = nonRenamed.get(name);
					if(def != null)
					{
						args.add(new SerializedArgument(def.getType(), def.getName()));
						continue;
					}
				}

				Type javaType = constructor.getRawMember().getParameters()[i].getParameterizedType();

				try
				{
					args.add(new InjectedArgument(collection, javaType, annotations[i]));
				}
				catch(Exception e)
				{
					throw new SerializationException("Error in constructor for " + raw + " while processing argument " + i + "; " + e.getMessage(), e);
				}
			}
		}

		Argument[] arguments = args.toArray(new Argument[args.size()]);
		return new FactoryDefinition<>(
			collection,
			raw,
			arguments,
			hasSerializedFields,
			isInjectable
		);
	}

	private static Expose findExpose(Annotation[] annotations)
	{
		for(Annotation a : annotations)
		{
			if(a instanceof Expose)
			{
				return (Expose) a;
			}
		}

		return null;
	}

	/**
	 * Get if this factory has any serialized fields.
	 *
	 * @return
	 */
	public boolean hasSerializedFields()
	{
		return hasSerializedFields;
	}

	/**
	 * Get if this factory is marked as injectable.
	 *
	 * @return
	 */
	public boolean isInjectable()
	{
		return isInjectable;
	}

	/**
	 * Get the number of fields this factory covers.
	 *
	 * @return
	 */
	public int getFieldCount()
	{
		int result = 0;
		for(Argument a : arguments)
		{
			if(a instanceof FactoryDefinition.SerializedArgument)
			{
				result++;
			}
		}
		return result;
	}

	/**
	 * Get a score for this factory based on the given data. The higher the
	 * score the more arguments were found.
	 *
	 * @param data
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public int getScore(Map<String, Object> data)
	{
		if(! hasSerializedFields)
		{
			return 0;
		}

		int score = 0;

		for(Argument arg : arguments)
		{
			if(arg instanceof FactoryDefinition.SerializedArgument)
			{
				if(data.containsKey(((SerializedArgument) arg).name))
				{
					score++;
				}
			}
		}

		return score;
	}

	/**
	 * Create a new instance using the given deserialized data. The data
	 * is only used if this factory has any serialized fields.
	 *
	 * @param data
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T create(Map<String, Object> data)
	{
		Object[] args = new Object[arguments.length];
		for(int i=0, n=args.length; i<n; i++)
		{
			args[i] = arguments[i].getValue(data);
		}

		try
		{
			return (T) raw.newInstance(args);
		}
		catch(IllegalArgumentException e)
		{
			throw new SerializationException("Unable to create; " + e.getMessage(), e);
		}
		catch(InstantiationException e)
		{
			throw new SerializationException("Unable to create; " + e.getMessage(), e);
		}
		catch(IllegalAccessException e)
		{
			throw new SerializationException("Unable to create; " + e.getMessage(), e);
		}
		catch(InvocationTargetException e)
		{
			throw new SerializationException("Unable to create; " + e.getCause().getMessage(), e.getCause());
		}
	}

	/**
	 * Create a new instance using a plain arguments array.
	 *
	 * @param args
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T create(Object[] args)
	{
		for(int i=0, n=args.length; i<n; i++)
		{
			if(arguments[i] instanceof FactoryDefinition.InjectedArgument)
			{
				args[i] = arguments[i].getValue(null);
			}
		}

		try
		{
			return (T) raw.newInstance(args);
		}
		catch(IllegalArgumentException e)
		{
			throw new SerializationException("Unable to create; " + e.getMessage(), e);
		}
		catch(InstantiationException e)
		{
			throw new SerializationException("Unable to create; " + e.getMessage(), e);
		}
		catch(IllegalAccessException e)
		{
			throw new SerializationException("Unable to create; " + e.getMessage(), e);
		}
		catch(InvocationTargetException e)
		{
			throw new SerializationException("Unable to create; " + e.getCause().getMessage(), e.getCause());
		}
	}

	interface Argument
	{
		Object getValue(Map<String, Object> data);
	}

	static class SerializedArgument
		implements Argument
	{
		final Class<?> type;
		final String name;

		public SerializedArgument(Class<?> type, String name)
		{
			this.type = type;
			this.name = name;
		}

		@Override
		public Object getValue(Map<String, Object> data)
		{
			Object value = data.get(name);
			if(value == null && type.isPrimitive())
			{
				return Defaults.defaultValue(type);
			}

			return value;
		}
	}

	private static class InjectedArgument
		implements Argument
	{
		private final Supplier<?> supplier;

		public InjectedArgument(SerializerCollection collection, Type type, Annotation[] annotations)
		{
			supplier = collection.getInstanceFactory().supplier(type, annotations);
		}

		@Override
		public Object getValue(Map<String, Object> data)
		{
			try
			{
				return supplier.get();
			}
			catch(InstanceException e)
			{
				throw new SerializationException("Unable to get object for argument; " + e.getMessage(), e);
			}
		}
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
			.add("constructor", raw)
			.toString();
	}
}
