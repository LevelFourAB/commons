package se.l4.commons.serialization.collections;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerFormatDefinition;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.StreamingOutput;
import se.l4.commons.serialization.format.Token;

/**
 * Serializer for {@link List}.
 *
 * @author Andreas Holstenson
 *
 * @param <T>
 */
public class SetSerializer<T>
	implements Serializer<Set<T>>
{
	private final Serializer<T> itemSerializer;
	private final SerializerFormatDefinition formatDefinition;

	public SetSerializer(Serializer<T> itemSerializer)
	{
		this.itemSerializer = itemSerializer;

		formatDefinition = SerializerFormatDefinition.builder()
			.list(itemSerializer)
			.build();
	}

	@Override
	public Set<T> read(StreamingInput in)
		throws IOException
	{
		in.next(Token.LIST_START);

		Set<T> list = new HashSet<T>();
		while(in.peek() != Token.LIST_END)
		{
			T value;
			if(in.peek() == Token.NULL)
			{
				in.next();
				value = null;
			}
			else
			{
				value = itemSerializer.read(in);
			}

			list.add(value);
		}

		in.next(Token.LIST_END);

		return list;
	}

	@Override
	public void write(Set<T> object, StreamingOutput stream)
		throws IOException
	{
		stream.writeListStart();

		for(T value : object)
		{
			stream.writeObject(itemSerializer, value);
		}

		stream.writeListEnd();
	}

	@Override
	public SerializerFormatDefinition getFormatDefinition()
	{
		return formatDefinition;
	}
}
