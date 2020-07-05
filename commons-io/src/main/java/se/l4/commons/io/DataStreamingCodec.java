package se.l4.commons.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Codec for transforming an object into and from binary form that uses a
 * {@link ExtendedDataInput} and {@link ExtendedDataOutput}.
 *
 * @param <T>
 */
public interface DataStreamingCodec<T>
	extends StreamingCodec<T>
{
	/**
	 * Read an object from the given {@link ExtendedDataInput}.
	 *
	 * @param input
	 * @return
	 * @throws IOException
	 */
	T read(ExtendedDataInput input)
		throws IOException;

	@Override
	default T read(InputStream in)
		throws IOException
	{
		try(ExtendedDataInput dataIn = new ExtendedDataInputStream(in))
		{
			return read(dataIn);
		}
	}

	/**
	 * Write an object to the given {@link ExtendedDataOutput}.
	 *
	 * @param item
	 * @param out
	 * @throws IOException
	 */
	void write(T item, ExtendedDataOutput out)
		throws IOException;

	@Override
	default void write(T item, OutputStream out)
		throws IOException
	{
		try(ExtendedDataOutput dataOut = new ExtendedDataOutputStream(out))
		{
			write(item, dataOut);
		}
	}
}
