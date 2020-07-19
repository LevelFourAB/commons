package se.l4.commons.serialization.internal.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.collections.api.map.MapIterable;

import se.l4.commons.serialization.Expose;
import se.l4.commons.serialization.Factory;
import se.l4.commons.serialization.SerializationException;
import se.l4.commons.serialization.Serializers;
import se.l4.commons.types.InstanceException;
import se.l4.commons.types.Types;
import se.l4.commons.types.reflect.ConstructorRef;
import se.l4.commons.types.reflect.ParameterRef;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Factory that can be used to create an instance of a certain object.
 *
 * @author Andreas Holstenson
 *
 */
public class FactoryDefinition<T>
{
	private final Constructor<?> raw;
	final Argument[] arguments;

	private final boolean hasSerializedFields;
	private final boolean isInjectable;

	public FactoryDefinition(
		Constructor<?> raw,
		Argument[] arguments,
		boolean hasSerializedFields,
		boolean isInjectable
	)
	{
		this.raw = raw;
		this.arguments = arguments;
		this.hasSerializedFields = hasSerializedFields;
		this.isInjectable = isInjectable;
	}

	public static <T> FactoryDefinition<T> resolve(
		Serializers collection,
		TypeRef parentType,
		MapIterable<String, FieldDefinition> fields,
		MapIterable<String, FieldDefinition> nonRenamed,
		ConstructorRef constructor
	)
	{
		List<Argument> args = new ArrayList<>();

		String[] names = findNames(constructor);

		// Figure out if we are injectable
		boolean isInjectable = constructor.getParameterCount() == 0;
		for(Annotation a : constructor.getAnnotations())
		{
			if(a.annotationType() == Factory.class
				|| a.annotationType().getSimpleName().equals("Inject"))
			{
				isInjectable = true;
				break;
			}
		}

		// Get if any fields are going to be serialized
		boolean hasSerializedFields = false;
		int i = 0;
		for(ParameterRef param : constructor.getParameters())
		{
			Optional<Expose> expose = param.getAnnotation(Expose.class);
			if(expose.isPresent())
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

			i++;
		}

		if(! isInjectable && ! hasSerializedFields)
		{
			// Neither injectable nor any serialized fields - skip this definition
			return null;
		}

		for(ParameterRef parameter : constructor.getParameters())
		{
			Optional<Expose> expose = parameter.getAnnotation(Expose.class);
			if(expose.isPresent())
			{
				// Try to serialize
				if("".equals(expose.get().value().trim()))
				{
					throw new SerializationException("The annotation @" +
						Expose.class.getSimpleName() +
						" when used in a constructor must have a name (for " +
						parentType.getErasedType() + ")");
				}

				FieldDefinition def = fields.get(expose.get().value());
				if(def == null)
				{
					throw new SerializationException(expose + " was used on a " +
							"constructor but there was no such field declared" +
							" (for " + parentType.getErasedType() + ")");
				}
				else if(Types.wrap(def.getType()) != Types.wrap(parameter.getType().getErasedType()))
				{
					throw new SerializationException(expose + " was used on a " +
						"constructor but the type of the argument was different " +
						"from the field. The field was resolved to " +
						def.getType() + " but the argument was of type " +
						parameter.getType().getErasedType() +
						" (for " + parentType.getErasedType() + ")");
				}

				args.add(new SerializedArgument(def.getType(), expose.get().value()));
			}
			else
			{
				if(i < names.length && names[i] != null)
				{
					String name = names[i];
					FieldDefinition def = nonRenamed.get(name);
					if(def != null)
					{
						args.add(new SerializedArgument(def.getType(), def.getName()));
						continue;
					}
				}

				try
				{
					args.add(new InjectedArgument(collection, parameter.getType(), parameter.getAnnotations()));
				}
				catch(Exception e)
				{
					throw new SerializationException("Error in constructor for " + constructor.toDescription() + " while processing argument " + i + "; " + e.getMessage(), e);
				}
			}
		}

		Argument[] arguments = args.toArray(new Argument[args.size()]);
		return new FactoryDefinition<>(
			constructor.getConstructor(),
			arguments,
			hasSerializedFields,
			isInjectable
		);
	}

	private static String[] findNames(ConstructorRef c)
	{
		String[] names = findNamesViaConstructorProperties(c);
		if(names != null) return names;

		return findNamesViaReflection(c);
	}

	private static String[] findNamesViaConstructorProperties(ConstructorRef c)
	{
		for(Annotation a : c.getAnnotations())
		{
			if("java.beans.ConstructorProperties".equals(a.annotationType().getName()))
			{
				// This is the annotation we are looking for
				try
				{
					Method method = a.annotationType().getMethod("value");
					return (String[]) method.invoke(a);
				}
				catch(NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
				{
					// Ignore that we can't access this
					return null;
				}
			}
		}

		return null;
	}

	private static String[] findNamesViaReflection(ConstructorRef c)
	{
		return c.getParameters()
			.collect(p -> p.isNamePresent() ? p.getName() : null)
			.toArray(new String[0]);
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
				return Types.defaultValue(type);
			}

			return value;
		}
	}

	private static class InjectedArgument
		implements Argument
	{
		private final Supplier<?> supplier;

		public InjectedArgument(Serializers collection, TypeRef type, Iterable<? extends Annotation> annotations)
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
		return getClass().getSimpleName() + "{constructor=" + raw + "}";
	}
}
