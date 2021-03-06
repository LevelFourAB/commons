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
 * Serializer for {@link Character}.
 *
 * @author Andreas Holstenson
 *
 */
public class CharacterSerializer
	implements Serializer<Character>
{
	private final SerializerFormatDefinition formatDefinition;

	public CharacterSerializer()
	{
		formatDefinition = SerializerFormatDefinition.forValue(ValueType.CHAR);
	}

	@Override
	public Optional<QualifiedName> getName()
	{
		return Optional.of(new QualifiedName("", "char"));
	}

	@Override
	public Character read(StreamingInput in)
		throws IOException
	{
		in.next(Token.VALUE);
		return in.readChar();
	}

	@Override
	public void write(Character object, StreamingOutput stream)
		throws IOException
	{
		stream.writeChar(object);
	}

	@Override
	public SerializerFormatDefinition getFormatDefinition()
	{
		return formatDefinition;
	}
}
