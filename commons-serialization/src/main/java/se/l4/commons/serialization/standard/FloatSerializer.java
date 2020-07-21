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
	public Optional<QualifiedName> getName()
	{
		return Optional.of(new QualifiedName("", "float"));
	}

	@Override
	public Float read(StreamingInput in)
		throws IOException
	{
		in.next(Token.VALUE);
		return in.readFloat();
	}

	@Override
	public void write(Float object, StreamingOutput stream)
		throws IOException
	{
		stream.writeFloat(object);
	}

	@Override
	public SerializerFormatDefinition getFormatDefinition()
	{
		return formatDefinition;
	}
}
