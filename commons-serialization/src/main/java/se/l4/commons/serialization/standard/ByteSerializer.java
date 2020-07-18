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
public class ByteSerializer
	implements Serializer<Byte>
{
	private final SerializerFormatDefinition formatDefinition;

	public ByteSerializer()
	{
		formatDefinition = SerializerFormatDefinition.forValue(ValueType.BYTE);
	}

	@Override
	public Optional<QualifiedName> getName()
	{
		return Optional.of(new QualifiedName("", "byte"));
	}

	@Override
	public Byte read(StreamingInput in)
		throws IOException
	{
		in.next(Token.VALUE);
		return in.readByte();
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
