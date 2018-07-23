package se.l4.commons.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of {@link Bytes} over a {@link InputStream}.
 *
 * @author Andreas Holstenson
 *
 */
public class InputStreamBytes
	implements Bytes
{
	private final IOSupplier<InputStream> in;

	public InputStreamBytes(IOSupplier<InputStream> in)
	{
		this.in = in;
	}

	@Override
	public InputStream asInputStream()
		throws IOException
	{
		return in.get();
	}

	@Override
	public byte[] toByteArray()
		throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try(InputStream in = this.in.get())
		{
			byte[] data = new byte[8192];
			int read;
			while((read = in.read(data)) != -1)
			{
				out.write(data, 0, read);
			}
		}
		return out.toByteArray();
	}
}
