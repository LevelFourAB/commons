package se.l4.commons.serialization.standard;

import java.io.IOException;

import se.l4.commons.serialization.SerializationException;
import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.StreamingOutput;
import se.l4.commons.serialization.format.Token;

/**
 * Serializer for {@link Number}, {@link Boolean} or {@link String}.
 *
 * @author Andreas Holstenson
 *
 */
public class SimpleTypeSerializer
	implements Serializer<Object>
{

	@Override
	public Object read(StreamingInput in)
		throws IOException
	{
		in.next(Token.VALUE);
		return in.readDynamic();
	}

	@Override
	public void write(Object object, StreamingOutput stream)
		throws IOException
	{
		if(object instanceof Byte)
		{
			stream.writeInt(((Byte) object).intValue());
		}
		else if(object instanceof Integer)
		{
			stream.writeInt((Integer) object);
		}
		else if(object instanceof Long)
		{
			stream.writeLong((Long) object);
		}
		else if(object instanceof Float)
		{
			stream.writeFloat((Float) object);
		}
		else if(object instanceof Double)
		{
			stream.writeDouble((Double) object);
		}
		else if(object instanceof Boolean)
		{
			stream.writeBoolean((Boolean) object);
		}
		else if(object instanceof String)
		{
			stream.writeString((String) object);
		}
		else
		{
			throw new SerializationException("Can't serialize the given object: " + object);
		}
	}

}
