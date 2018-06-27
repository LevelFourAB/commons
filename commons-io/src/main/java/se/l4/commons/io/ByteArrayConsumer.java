package se.l4.commons.io;

import java.io.IOException;

/**
 * Consumer of {@code byte[]} arrays.
 *
 * @author Andreas Holstenson
 *
 */
public interface ByteArrayConsumer
{
	void consume(byte[] data, int offset, int length)
		throws IOException;
}
