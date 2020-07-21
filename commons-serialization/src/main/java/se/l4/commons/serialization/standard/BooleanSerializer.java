package se.l4.commons.serialization.standard;

import java.io.IOException;
import java.util.Optional;

import se.l4.commons.serialization.QualifiedName;
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
public class BooleanSerializer
	implements Serializer<Boolean>
{
	private final SerializerFormatDefinition formatDefinition;

	public BooleanSerializer()
	{
		formatDefinition = SerializerFormatDefinition.forValue(ValueType.BOOLEAN);
	}

	@Override
	public Optional<QualifiedName> getName()
	{
		return Optional.of(new QualifiedName("", "boolean"));
	}

	@Override
	public Boolean read(StreamingInput in)
		throws IOException
	{
		in.next(Token.VALUE);
		return in.readBoolean();
	}

	@Override
	public void write(Boolean object, StreamingOutput stream)
		throws IOException
	{
		stream.writeBoolean(object);
	}

	@Override
	public SerializerFormatDefinition getFormatDefinition()
	{
		return formatDefinition;
	}
}
