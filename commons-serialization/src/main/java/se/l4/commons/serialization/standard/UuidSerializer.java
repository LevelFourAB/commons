package se.l4.commons.serialization.standard;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import se.l4.commons.serialization.QualifiedName;
import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerFormatDefinition;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.StreamingOutput;
import se.l4.commons.serialization.format.Token;
import se.l4.commons.serialization.format.ValueType;

/**
 * Serializer for {@link UUID} that transforms into a byte array.
 *
 * @author Andreas Holstenson
 *
 */
public class UuidSerializer
	implements Serializer<UUID>
{
	private final SerializerFormatDefinition formatDefinition;

	public UuidSerializer()
	{
		formatDefinition = SerializerFormatDefinition.forValue(ValueType.BYTES);
	}

	@Override
	public Optional<QualifiedName> getName()
	{
		return Optional.of(new QualifiedName("", "uuid"));
	}

	@Override
	public UUID read(StreamingInput in) throws IOException
	{
		in.next(Token.VALUE);
		return fromBytes0(in.readByteArray());
	}

	@Override
	public void write(UUID object, StreamingOutput stream)
		throws IOException
	{
		stream.writeBytes(toBytes0(object));
	}

	@Override
	public SerializerFormatDefinition getFormatDefinition()
	{
		return formatDefinition;
	}

	private static UUID fromBytes0(byte[] bytes)
	{
		if(bytes == null) return null;

		long msb = 0;
		long lsb = 0;
		for(int i=0; i<8; i++)
		{
			msb = (msb << 8) | (bytes[i] & 0xff);
			lsb = (lsb << 8) | (bytes[8 + i] & 0xff);
		}

		return new UUID(msb, lsb);
	}

	private static byte[] toBytes0(UUID uuid)
	{
		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits();

		byte[] buffer = new byte[16];
		for(int i=0; i<8; i++)
		{
			buffer[i] = (byte) (msb >>> 8 * (7 - i));
			buffer[8+i] = (byte) (lsb >>> 8 * (7 - i));
		}

		return buffer;
	}
}
