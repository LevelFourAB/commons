package se.l4.commons.serialization.standard;

import java.io.IOException;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerFormatDefinition;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.StreamingOutput;
import se.l4.commons.serialization.format.Token;
import se.l4.commons.serialization.format.ValueType;

/**
 * Serializer for {@link Float}.
 *
 * @author Andreas Holstenson
 *
 */
public class FloatSerializer
	implements Serializer<Float>
{
	private final SerializerFormatDefinition formatDefinition;

	public FloatSerializer()
	{
		formatDefinition = SerializerFormatDefinition.forValue(ValueType.FLOAT);
	}

	@Override
	public Float read(StreamingInput in)
		throws IOException
	{
		in.next(Token.VALUE);
		return in.getFloat();
	}

	@Override
	public void write(Float object, String name, StreamingOutput stream)
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
