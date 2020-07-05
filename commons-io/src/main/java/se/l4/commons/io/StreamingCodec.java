package se.l4.commons.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Codec for transforming an object into and from binary form that operates on
 * {@link InputStream} and {@link OutputStream}.
 *
 * @param <T>
 */
public interface StreamingCodec<T>
{
	/**
	 * Read an object from the given {@link InputStream}.
	 *
	 * @param in
	 *   the stream of bytes to read
	 * @return
	 *   object read from the stream
	 * @throws IOException
	 *   if unable to read
	 */
	@Nullable
	T read(@NonNull InputStream in)
		throws IOException;

	/**
	 * Write an object to the given {@link OutputStream}.
	 *
	 * @param item
	 *   item to write, should never be {@code null}
	 * @param out
	 *   output stream
	 * @throws IOException
	 *   if unable to write object
	 */
	void write(@Nullable T item, @NonNull OutputStream out)
		throws IOException;
}
