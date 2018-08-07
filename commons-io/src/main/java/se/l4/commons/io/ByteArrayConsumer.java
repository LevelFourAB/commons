package se.l4.commons.io;

import java.io.IOException;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Consumer of {@code byte[]} arrays.
 *
 * @author Andreas Holstenson
 *
 */
public interface ByteArrayConsumer
{
	/**
	 * Consumer the given data from the specified offset and no longer than
	 * the given length.
	 *
	 * @param data
	 *   the byte array of data
	 * @param offset
	 *   the offset to start consumption as
	 * @param length
	 *   the number of bytes to consume
	 */
	void consume(@NonNull byte[] data, int offset, int length)
		throws IOException;
}
