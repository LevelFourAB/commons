package se.l4.commons.serialization.collections;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerFormatDefinition;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.StreamingOutput;
import se.l4.commons.serialization.format.Token;

/**
 * Serializer for arrays.
 * 
 * @author Andreas Holstenson
 *
 */
public class ArraySerializer
	implements Serializer<Object>
{
	private final Class<?> componentType;
	@SuppressWarnings("rawtypes")
	private final Serializer itemSerializer;
	private final SerializerFormatDefinition formatDefinition;

	public ArraySerializer(Class<?> componentType, Serializer<?> itemSerializer)
	{
		this.componentType = componentType;
		this.itemSerializer = itemSerializer;
		
		formatDefinition = SerializerFormatDefinition.builder()
			.list(itemSerializer)
			.build();
	}
	
	@Override
	public Object read(StreamingInput in)
		throws IOException
	{
		in.next(Token.LIST_START);
		
		List<Object> list = new ArrayList<Object>();
		while(in.peek() != Token.LIST_END)
		{
			Object value = in.peek() == Token.NULL ? null : itemSerializer.read(in);
			list.add(value);
		}
		
		in.next(Token.LIST_END);
		
		Object array = Array.newInstance(componentType, list.size());
		for(int i=0, n=list.size(); i<n; i++)
		{
			Array.set(array, i, list.get(i));
		}
		return array;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void write(Object object, String name, StreamingOutput stream)
		throws IOException
	{
		stream.writeListStart(name);
		for(int i=0, n=Array.getLength(object); i<n; i++)
		{
			Object value = Array.get(object, i);
			if(value == null)
			{
				stream.writeNull("item");
			}
			else
			{
				itemSerializer.write(value, "item", stream);
			}
		}
		stream.writeListEnd(name);
	}
	
	@Override
	public SerializerFormatDefinition getFormatDefinition()
	{
		return formatDefinition;
	}
}
