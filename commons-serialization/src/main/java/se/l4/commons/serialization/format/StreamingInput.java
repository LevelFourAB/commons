package se.l4.commons.serialization.format;

import java.io.Closeable;
import java.io.IOException;

import se.l4.commons.serialization.SerializationException;

/**
 * Input that is streamed as a set of token with values.
 *
 * @author Andreas Holstenson
 *
 */
public interface StreamingInput
	extends Closeable
{
	/**
	 * Peek into the stream and return the next token.
	 *
	 * @return
	 */
	Token peek()
		throws IOException;

	/**
	 * Advance to the next token.
	 *
	 * @return
	 * @throws IOException
	 */
	Token next()
		throws IOException;

	/**
	 * Advance to the next token checking that it is of a certain type.
	 *
	 * @param expected
	 * @return
	 * @throws IOException
	 */
	Token next(Token expected)
		throws IOException;

	/**
	 * Skip the started object, list or value. This method should only be used when
	 * when token is either {@link Token#OBJECT_START}, {@link Token#LIST_START}
	 * or {@link Token#VALUE}. See {@link #skipValue()} for skipping reading
	 * when the current token is {@link Token#KEY}.
	 *
	 * @throws IOException
	 */
	void skip()
		throws IOException;

	/**
	 * If this token is a {@link Token#KEY} this will skip its value.
	 *
	 * @throws IOException
	 */
	void skipValue()
		throws IOException;

	/**
	 * Get the current token.
	 *
	 * @return
	 */
	Token current();

	/**
	 * Get the type of value the input currently has. If the
	 * {@link #current() current token} is not {@link Token#VALUE} or
	 * {@link Token#NULL} this method will raise a
	 * {@link SerializationException}.
	 *
	 * <p>
	 * The type of value is defined by the input, and many number values can
	 * be converted.
	 *
	 * @return
	 *   current type of value
	 */
	default ValueType getValueType()
	{
		throw new UnsupportedOperationException("StreamingInput implementation does not extend AbstractStreamingInput and does not override getValueType()");
	}

	/**
	 * Get the current value.
	 *
	 * @return
	 */
	Object getValue();

	/**
	 * Get the current value as a string.
	 *
	 * @return
	 */
	String getString();

	/**
	 * Get the value as a boolean.
	 *
	 * @return
	 */
	boolean getBoolean();

	/**
	 * Get the value as a byte.
	 *
	 * @return
	 */
	default byte getByte()
	{
		return (byte) getInt();
	}

	/**
	 * Get the value as a character.
	 *
	 * @return
	 */
	default char getChar()
	{
		return (char) getInt();
	}

	/**
	 * Get the value as a double.
	 *
	 * @return
	 */
	double getDouble();

	/**
	 * Get the value as a float.
	 *
	 * @return
	 */
	float getFloat();

	/**
	 * Get the value as a long.
	 *
	 * @return
	 */
	long getLong();

	/**
	 * Get the value as an integer.
	 *
	 * @return
	 */
	int getInt();

	/**
	 * Get the value as a short.
	 *
	 * @return
	 */
	short getShort();

	/**
	 * Get the value as a byte[] array.
	 *
	 * @return
	 */
	byte[] getByteArray();
}
