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
 * Serializer for {@link Double}.
 *
 * @author Andreas Holstenson
 *
 */
public class DoubleSerializer
	implements Serializer<Double>
{
	private final SerializerFormatDefinition formatDefinition;

	public DoubleSerializer()
	{
		formatDefinition = SerializerFormatDefinition.forValue(ValueType.DOUBLE);
	}

	@Override
	public Optional<QualifiedName> getName()
	{
		return Optional.of(new QualifiedName("", "double"));
	}

	@Override
	public Double read(StreamingInput in)
		throws IOException
	{
		in.next(Token.VALUE);
		return in.readDouble();
	}

	@Override
	public void write(Double object, StreamingOutput stream)
		throws IOException
	{
		stream.writeDouble(object);
	}

	@Override
	public SerializerFormatDefinition getFormatDefinition()
	{
		return formatDefinition;
	}
}
