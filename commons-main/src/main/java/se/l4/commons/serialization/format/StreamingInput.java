package se.l4.commons.serialization.format;

import java.io.Closeable;
import java.io.IOException;

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
	 * Skip the started object or list. This method should only be used when
	 * when token is either {@link Token#OBJECT_START} or {@link Token#LIST_START}.
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