package se.l4.commons.serialization.standard;

import java.io.IOException;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerFormatDefinition;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.StreamingOutput;
import se.l4.commons.serialization.format.Token;
import se.l4.commons.serialization.format.ValueType;

/**
 * Serializer for {@link Boolean}.
 *
 * @author Andreas Holstenson
 *
 */
public class ByteSerializer
	implements Serializer<Byte>
{
	private final SerializerFormatDefinition formatDefinition;

	public ByteSerializer()
	{
		formatDefinition = SerializerFormatDefinition.forValue(ValueType.BYTE);
	}

	@Override
	public Byte read(StreamingInput in)
		throws IOException
	{
		in.next(Token.VALUE);
		return in.getByte();
	}

	@Override
	public void write(Byte object, String name, StreamingOutput stream)
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
