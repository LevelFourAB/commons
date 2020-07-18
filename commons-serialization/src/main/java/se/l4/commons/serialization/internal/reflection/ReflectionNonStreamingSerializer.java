package se.l4.commons.serialization.internal.reflection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import se.l4.commons.serialization.QualifiedName;
import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerFormatDefinition;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.StreamingOutput;
import se.l4.commons.serialization.format.Token;

/**
 * Serializer that uses only fields or methods. Can fully stream the object.
 *
 * @author Andreas Holstenson
 *
 * @param <T>
 */
public class ReflectionNonStreamingSerializer<T>
	implements Serializer<T>
{
	private final TypeInfo<T> type;
	private final int size;

	public ReflectionNonStreamingSerializer(TypeInfo<T> type)
	{
		this.type = type;
		this.size = type.getAllFields().length;
	}

	@Override
	public Optional<QualifiedName> getName()
	{
		return Optional.ofNullable(type.getName());
	}

	@Override
	public T read(StreamingInput in)
		throws IOException
	{
		in.next(Token.OBJECT_START);

		// First create a map with all the data
		Map<String, Object> data = new HashMap<>(size);
		while(in.peek() != Token.OBJECT_END)
		{
			in.next(Token.KEY);
			String key = in.readString();

			FieldDefinition def = type.getField(key);
			if(def == null)
			{
				// No such field, skip the entire value
				in.skipValue();
			}
			else
			{
				data.put(key, def.read(in));
			}
		}

		in.next(Token.OBJECT_END);

		// Create the instance
		T instance = type.newInstance(data);

		// Transfer any other fields
		for(Map.Entry<String, Object> entry : data.entrySet())
		{
			FieldDefinition def = type.getField(entry.getKey());
			if(! def.isReadOnly())
			{
				def.set(instance, entry.getValue());
			}
		}

		return instance;
	}

	@Override
	public void write(T object, String name, StreamingOutput stream)
		throws IOException
	{
		stream.writeObjectStart(name);

		for(FieldDefinition def : type.getAllFields())
		{
			def.write(object, stream);
		}

		stream.writeObjectEnd(name);
	}

	@Override
	public SerializerFormatDefinition getFormatDefinition()
	{
		return type.getFormatDefinition();
	}
}
