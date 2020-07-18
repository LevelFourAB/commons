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
 * Serializer for {@link Integer}.
 *
 * @author Andreas Holstenson
 *
 */
public class IntSerializer
	implements Serializer<Integer>
{
	private final SerializerFormatDefinition formatDefinition;

	public IntSerializer()
	{
		formatDefinition = SerializerFormatDefinition.forValue(ValueType.INTEGER);
	}

	@Override
	public Optional<QualifiedName> getName()
	{
		return Optional.of(new QualifiedName("", "integer"));
	}

	@Override
	public Integer read(StreamingInput in)
		throws IOException
	{
		in.next(Token.VALUE);
		return in.readInt();
	}

	@Override
	public void write(Integer object, String name, StreamingOutput stream)
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
