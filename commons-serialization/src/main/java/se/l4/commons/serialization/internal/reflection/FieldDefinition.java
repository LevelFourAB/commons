package se.l4.commons.serialization.internal.reflection;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

import se.l4.commons.serialization.SerializationException;
import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.StreamingOutput;
import se.l4.commons.serialization.format.Token;
import se.l4.commons.types.Types;

/**
 * Definition of a field within a reflection serializer.
 *
 * @author Andreas Holstenson
 *
 */
@SuppressWarnings("rawtypes")
public class FieldDefinition
{
	private final Field field;
	private final Serializer serializer;
	private final String name;
	private final Class<?> type;
	private final boolean readOnly;
	private final boolean skipIfDefault;
	private final boolean nullHandling;

	public FieldDefinition(Field field, String name, Serializer serializer, Class type, boolean skipIfDefault)
	{
		this.field = field;
		this.name = name;
		this.serializer = serializer;
		this.type = type;
		this.skipIfDefault = skipIfDefault;
		this.nullHandling = serializer instanceof Serializer.NullHandling;
		readOnly = Modifier.isFinal(field.getModifiers());
	}

	public String getName()
	{
		return name;
	}

	public Serializer getSerializer()
	{
		return serializer;
	}

	public boolean isSkipIfDefault()
	{
		return skipIfDefault;
	}

	public boolean isReadOnly()
	{
		return readOnly;
	}

	public Class<?> getType()
	{
		return type;
	}

	public Object read(StreamingInput in)
		throws IOException
	{
		if(in.peek() == Token.NULL)
		{
			if(nullHandling)
			{
				// Let the serializer handle the null value
				return serializer.read(in);
			}

			// Consume and return null
			in.next();
			return Types.defaultValue(type);
		}

		return serializer.read(in);
	}

	public void read(Object target, StreamingInput in)
		throws IOException
	{
		set(target, read(in));
	}

	public void set(Object target, Object value)
		throws IOException
	{
		try
		{
			if(value == null && type.isPrimitive())
			{
				value = Types.defaultValue(type);
			}

			field.set(target, value);
		}
		catch(Exception e)
		{
			throw new SerializationException("Unable to read object; " + e.getMessage(), e);
		}
	}

	public Object getValue(Object target)
	{
		try
		{
			return field.get(target);
		}
		catch(IllegalArgumentException e)
		{
			throw new SerializationException("Unable to write object; " + e.getMessage(), e);
		}
		catch(IllegalAccessException e)
		{
			throw new SerializationException("Unable to write object; " + e.getMessage(), e);
		}

	}

	@SuppressWarnings("unchecked")
	public void write(Object target, StreamingOutput stream)
		throws IOException
	{
		Object value = getValue(target);

		if(skipIfDefault)
		{
			Object defaultValue = Types.defaultValue(type);
			if(Objects.equals(defaultValue, value))
			{
				// Write nothing as the default value and our value matches
				return;
			}
		}

		stream.writeString(name);

		if(value == null)
		{
			if(nullHandling)
			{
				serializer.write(null, stream);
			}
			else
			{
				stream.writeNull();
			}
		}
		else
		{
			serializer.write(value, stream);
		}
	}

	public Annotation[] getHints()
	{
		return field.getAnnotations();
	}
}
