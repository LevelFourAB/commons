package se.l4.commons.serialization.standard;

import java.io.IOException;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerFormatDefinition;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.StreamingOutput;
import se.l4.commons.serialization.format.Token;
import se.l4.commons.serialization.format.ValueType;

/**
 * Serializer for {@link Long}.
 * 
 * @author Andreas Holstenson
 *
 */
public class LongSerializer
	implements Serializer<Long>
{
	private final SerializerFormatDefinition formatDefinition;

	public LongSerializer()
	{
		formatDefinition = SerializerFormatDefinition.forValue(ValueType.LONG);
	}

	@Override
	public Long read(StreamingInput in)
		throws IOException
	{
		in.next(Token.VALUE);
		return in.getLong();
	}

	@Override
	public void write(Long object, String name, StreamingOutput stream)
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
