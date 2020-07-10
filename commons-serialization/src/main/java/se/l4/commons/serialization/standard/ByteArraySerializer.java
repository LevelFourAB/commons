package se.l4.commons.serialization.standard;

import java.io.IOException;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerFormatDefinition;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.StreamingOutput;
import se.l4.commons.serialization.format.Token;
import se.l4.commons.serialization.format.ValueType;

/**
 * Serializer for byte arrays as they have special meaning in
 * {@link StreamingInput} and {@link StreamingOutput}.
 *
 * @author Andreas Holstenson
 *
 */
public class ByteArraySerializer
	implements Serializer<byte[]>
{
	private final SerializerFormatDefinition formatDefinition;

	public ByteArraySerializer()
	{
		formatDefinition = SerializerFormatDefinition.forValue(ValueType.BYTES);
	}

	@Override
	public byte[] read(StreamingInput in)
		throws IOException
	{
		in.next(Token.VALUE);
		return in.readByteArray();
	}

	@Override
	public void write(byte[] object, String name, StreamingOutput stream)
		throws IOException
	{
		stream.write(name, object);
	}

	@Override
	public SerializerFormatDefinition getFormatDefinition()
	{
		return formatDefinition;
	}
}
