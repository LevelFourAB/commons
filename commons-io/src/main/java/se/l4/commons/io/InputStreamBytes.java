package se.l4.commons.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.NonNull;

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

	public InputStreamBytes(@NonNull IOSupplier<InputStream> in)
	{
		this.in = Objects.requireNonNull(in);
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
