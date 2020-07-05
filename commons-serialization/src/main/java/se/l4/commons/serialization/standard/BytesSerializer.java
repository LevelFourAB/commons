package se.l4.commons.serialization.standard;

import java.io.IOException;

import se.l4.commons.io.Bytes;
import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.StreamingOutput;
import se.l4.commons.serialization.format.Token;

/**
 * Serializer for {@link Bytes}.
 */
public class BytesSerializer
	implements Serializer<Bytes>
{
	@Override
	public Bytes read(StreamingInput in)
		throws IOException
	{
		in.next(Token.VALUE);
		return Bytes.create(in.getByteArray());
	}

	@Override
	public void write(Bytes object, String name, StreamingOutput out)
		throws IOException
	{
		out.write(name, object.toByteArray());
	}
}
