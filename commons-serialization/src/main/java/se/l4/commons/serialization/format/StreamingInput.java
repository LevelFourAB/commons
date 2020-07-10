package se.l4.commons.serialization.format;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import edu.umd.cs.findbugs.annotations.NonNull;
import se.l4.commons.io.Bytes;

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
	@NonNull
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
	 * Get the current value as a string.
	 *
	 * @return
	 */
	String readString()
		throws IOException;

	/**
	 * Read any value from the input. The types returned by this method will
	 * be input specific and will not perform any conversions.
	 *
	 * @return
	 * @throws IOException
	 */
	Object readDynamic()
		throws IOException;

	/**
	 * Get the value as a boolean.
	 *
	 * @return
	 */
	boolean readBoolean()
		throws IOException;

	/**
	 * Get the value as a byte.
	 *
	 * @return
	 */
	byte readByte()
		throws IOException;

	/**
	 * Get the value as a character.
	 *
	 * @return
	 */
	char readChar()
		throws IOException;

	/**
	 * Get the value as a double.
	 *
	 * @return
	 */
	double readDouble()
		throws IOException;

	/**
	 * Get the value as a float.
	 *
	 * @return
	 */
	float readFloat()
		throws IOException;

	/**
	 * Get the value as a long.
	 *
	 * @return
	 */
	long readLong()
		throws IOException;

	/**
	 * Get the value as an integer.
	 *
	 * @return
	 */
	int readInt()
		throws IOException;

	/**
	 * Get the value as a short.
	 *
	 * @return
	 */
	short readShort()
		throws IOException;

	/**
	 * Get the value as a byte[] array.
	 *
	 * @return
	 */
	byte[] readByteArray()
		throws IOException;

	/**
	 * Read the next value into a {@link Bytes} instance.
	 *
	 * @return
	 */
	Bytes readBytes()
		throws IOException;

	/**
	 * Get the current binary value as an {@link InputStream}.
	 *
	 * @return
	 */
	InputStream asInputStream()
		throws IOException;
}
