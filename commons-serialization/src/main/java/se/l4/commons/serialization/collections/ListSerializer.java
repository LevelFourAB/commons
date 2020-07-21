package se.l4.commons.serialization.collections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;

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
public class ListSerializer<T>
	implements Serializer<List<T>>
{
	private final Serializer<T> itemSerializer;
	private final SerializerFormatDefinition formatDefinition;

	public ListSerializer(Serializer<T> itemSerializer)
	{
		this.itemSerializer = itemSerializer;

		formatDefinition = SerializerFormatDefinition.builder()
			.list(itemSerializer)
			.build();
	}

	@Override
	public List<T> read(StreamingInput in)
		throws IOException
	{
		in.next(Token.LIST_START);

		List<T> list = new ArrayList<T>();
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
	public void write(List<T> object, StreamingOutput stream)
		throws IOException
	{
		stream.writeListStart();

		if(object instanceof RandomAccess)
		{
			for(int i=0, n=object.size(); i<n; i++)
			{
				T value = object.get(i);
				stream.writeObject(itemSerializer, value);
			}
		}
		else
		{
			for(T value : object)
			{
				stream.writeObject(itemSerializer, value);
			}
		}

		System.out.println("Write end");
		stream.writeListEnd();
	}

	@Override
	public SerializerFormatDefinition getFormatDefinition()
	{
		return formatDefinition;
	}
}
