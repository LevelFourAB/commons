package se.l4.commons.serialization.collections;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerFormatDefinition;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.StreamingOutput;
import se.l4.commons.serialization.format.Token;

public class MapAsObjectSerializer<V>
	implements Serializer<Map<String, V>>
{
	private final Serializer<V> serializer;
	private final SerializerFormatDefinition formatDefinition;

	public MapAsObjectSerializer(Serializer<V> serializer)
	{
		this.serializer = serializer;

		formatDefinition = SerializerFormatDefinition.builder()
			.field("*").using(serializer)
			.build();
	}

	@Override
	public Map<String, V> read(StreamingInput in)
		throws IOException
	{
		in.next(Token.OBJECT_START);

		Map<String, V> result = new HashMap<String, V>();
		while(in.peek() != Token.OBJECT_END)
		{
			if(in.peek() == Token.NULL)
			{
				// Due to old error with writing of null values
				in.next();
				continue;
			}

			in.next(Token.KEY);
			String key = in.readString();
			if(key.startsWith("__commons__:"))
			{
				in.skipValue();
				continue;
			}

			V value = in.peek() == Token.NULL ? null : serializer.read(in);

			result.put(key, value);
		}

		in.next(Token.OBJECT_END);

		return result;
	}

	@Override
	public void write(Map<String, V> object, StreamingOutput stream)
		throws IOException
	{
		stream.writeObjectStart();

		for(Entry<String, V> e : object.entrySet())
		{
			V value = e.getValue();

			stream.writeString(e.getKey());
			stream.writeObject(serializer, value);
		}

		stream.writeObjectEnd();
	}

	@Override
	public SerializerFormatDefinition getFormatDefinition()
	{
		return formatDefinition;
	}
}
