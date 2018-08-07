package se.l4.commons.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Implementation of {@link Bytes} using a {@code byte[]} array.
 */
public class BytesOverByteArray
	implements Bytes
{
	public static final Bytes EMPTY = new BytesOverByteArray(new byte[0], 0, 0);

	private final byte[] data;
	private final int offset;
	private final int length;

	/**
	 * Create a new instance using the given data. This will <b>not</b> copy
	 * the data and instead borrows it. If the byte array is modified later
	 * the data within this instance will change.
	 *
	 * @param data
	 *   the byte array to use
	 * @param offset
	 *   the offset to start data at
	 * @param length
	 *   the number of bytes from the byte array to handle
	 */
	@SuppressFBWarnings("EI_EXPOSE_REP2")
	public BytesOverByteArray(@NonNull byte[] data, int offset, int length)
	{
		this.data = Objects.requireNonNull(data);
		this.offset = offset;
		this.length = length;
	}

	@Override
	public InputStream asInputStream()
		throws IOException
	{
		return new ByteArrayInputStream(data);
	}

	@Override
	public byte[] toByteArray()
		throws IOException
	{
		return Arrays.copyOfRange(data, offset, offset + length);
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "{size=" + data.length + "}";
	}
}
