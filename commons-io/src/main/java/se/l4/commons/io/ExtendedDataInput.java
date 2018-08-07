package se.l4.commons.io;

import java.io.Closeable;
import java.io.DataInput;
import java.io.IOException;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * {@link DataInput} with extended methods for reading some more specific
 * but common types of data.
 */
public interface ExtendedDataInput
	extends DataInput, Closeable
{
	/**
	 * Read a single byte from the input.
	 */
	int read()
		throws IOException;

	/**
	 * Read an integer that has been encoded as a variable int.
	 *
	 * @see ExtendedDataOutput#writeVInt(int)
	 */
	int readVInt()
		throws IOException;

	/**
	 * Read a long that has been encoded as a variable long.
	 *
	 * @see ExtendedDataOutput#writeLong(long)
	 */
	long readVLong()
		throws IOException;

	/**
	 * Read a string that was written in a compact UTF-8 format.
	 *
	 * @see ExtendedDataOutput#writeString(String)
	 */
	@NonNull
	String readString()
		throws IOException;

	/**
	 * Read bytes that has been previously written, caching them in memory for
	 * later use.
	 */
	@NonNull
	Bytes readBytes()
		throws IOException;

	/**
	 * Read bytes that has been previously written, but make the read only
	 * valid while the input is open.
	 */
	@NonNull
	Bytes readTemporaryBytes()
		throws IOException;
}
