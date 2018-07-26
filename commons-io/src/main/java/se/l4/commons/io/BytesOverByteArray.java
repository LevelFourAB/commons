package se.l4.commons.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

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

	public BytesOverByteArray(byte[] data, int offset, int length)
	{
		this.data = data;
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
