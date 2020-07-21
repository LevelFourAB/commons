package se.l4.commons.serialization.enums;

import java.io.IOException;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerFormatDefinition;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.StreamingOutput;
import se.l4.commons.serialization.format.Token;
import se.l4.commons.serialization.internal.SerializerFormatDefinitionBuilderImpl;

/**
 * Serializer for {@link Enum}s. The enum serializer can use different
 * {@link ValueTranslator}s to encode enums in different ways.
 *
 * @author Andreas Holstenson
 *
 * @param <T>
 */
public class EnumSerializer<T extends Enum<T>>
	implements Serializer<T>
{
	@SuppressWarnings("rawtypes")
	private final ValueTranslator translator;
	private final SerializerFormatDefinition formatDefinition;

	public EnumSerializer(@SuppressWarnings("rawtypes") ValueTranslator translator)
	{
		this.translator = translator;

		formatDefinition = new SerializerFormatDefinitionBuilderImpl()
			.value(translator.getType())
			.build();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T read(StreamingInput in)
		throws IOException
	{
		in.next(Token.VALUE);

		Object value;
		switch(translator.getType())
		{
			case BOOLEAN:
				value = in.readBoolean();
				break;
			case DOUBLE:
				value = in.readDouble();
				break;
			case FLOAT:
				value = in.readFloat();
				break;
			case INTEGER:
				value = in.readInt();
				break;
			case LONG:
				value = in.readLong();
				break;
			case SHORT:
				value = in.readShort();
				break;
			case STRING:
				value = in.readString();
				break;
			case BYTE:
				value = in.readByte();
				break;
			case BYTES:
				value = in.readByteArray();
				break;
			case CHAR:
				value = in.readChar();
				break;
			default:
				throw new AssertionError("Unknown type: " + translator.getType());
		}

		return (T) translator.toEnum(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void write(T object, StreamingOutput stream)
		throws IOException
	{
		Object value = translator.fromEnum(object);
		switch(translator.getType())
		{
			case BOOLEAN:
				stream.writeBoolean((Boolean) value);
				break;
			case DOUBLE:
				stream.writeDouble((Double) value);
				break;
			case FLOAT:
				stream.writeFloat((Float) value);
				break;
			case INTEGER:
				stream.writeInt((Integer) value);
				break;
			case LONG:
				stream.writeLong((Long) value);
				break;
			case SHORT:
				stream.writeInt((Short) value);
				break;
			case STRING:
				stream.writeString((String) value);
				break;
			default:
				throw new AssertionError("Unknown type: " + translator.getType());
		}
	}

	@Override
	public SerializerFormatDefinition getFormatDefinition()
	{
		return formatDefinition;
	}
}
