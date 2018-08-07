package se.l4.commons.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Builder for instances of {@link Bytes}.
 */
public class BytesBuilder
{
	private final ByteArrayOutputStream out;

	public BytesBuilder()
	{
		out = new ByteArrayOutputStream(8192);
	}

	/**
	 * Add a chunk of data to the {@link Bytes} instance.
	 *
	 * @param buffer
	 *   byte data to add
	 * @return
	 *   self
	 */
	public BytesBuilder addChunk(@NonNull byte[] buffer)
	{
		return addChunk(buffer, 0, buffer.length);
	}

	/**
	 * Add a chunk of data to the {@link Bytes} instance.
	 *
	 * @param buffer
	 *   byte data to add
	 * @param offset
	 *   offset to start adding byte data from
	 * @param length
	 *   the length of the data to add
	 */
	public BytesBuilder addChunk(@NonNull byte[] buffer, int offset, int length)
	{
		Objects.requireNonNull(buffer);

		out.write(buffer, offset, length);
		return this;
	}

	/**
	 * Build the {@link Bytes} instance.
	 */
	@NonNull
	public Bytes build()
	{
		return Bytes.create(out.toByteArray());
	}

	/**
	 * Create an instance of {@link Bytes} that is created lazily by the
	 * creator whenever byte data is requested.
	 *
	 * @param creator
	 *   the creator of byte data
	 * @return
	 *   instance of bytes
	 */
	@NonNull
	static Bytes createViaLazyDataOutput(@NonNull IOConsumer<ExtendedDataOutput> creator)
	{
		return createViaLazyDataOutput(creator, 8192);
	}

	/**
	 * Create an instance of {@link Bytes} that is created lazily and is of
	 * the expected size.
	 *
	 * @param creator
	 *   the creator of byte data
	 * @param expectedSize
	 *   the expected size of the created byte data, used to allocate memory
	 *   for the data
	 * @return
	 *   instance of bytes
	 */
	@NonNull
	static Bytes createViaLazyDataOutput(@NonNull IOConsumer<ExtendedDataOutput> creator, int expectedSize)
	{
		return new DataOutputBytes(creator, expectedSize);
	}

	/**
	 * Create an instance of {@link Bytes} that is created and stored in
	 * memory.
	 *
	 * @param creator
	 *   the creator of byte data
	 * @return
	 *   instance of bytes
	 */
	static Bytes createViaDataOutput(@NonNull IOConsumer<ExtendedDataOutput> creator)
			throws IOException
	{
		return createViaDataOutput(creator, 8192);
	}

	/**
	 * Create an instance of {@link Bytes} that is created and stored in
	 * memory.
	 *
	 * @param creator
	 *   the creator of byte data
	 * @param expectedSize
	 *   the expected size of the created byte data, used to allocate memory
	 *   for the data
	 * @return
	 *   instance of bytes
	 */
	static Bytes createViaDataOutput(@NonNull IOConsumer<ExtendedDataOutput> creator, int expectedSize)
		throws IOException
	{
		Objects.requireNonNull(creator);
		if(expectedSize <= 0) throw new IllegalArgumentException("expectedSize should be larger than 0");

		ByteArrayOutputStream out = new ByteArrayOutputStream(expectedSize);
		try(ExtendedDataOutput dataOut = new ExtendedDataOutputStream(out))
		{
			creator.accept(dataOut);
		}
		return Bytes.create(out.toByteArray());
	}

	private static class DataOutputBytes
		implements Bytes
	{
		private final IOConsumer<ExtendedDataOutput> creator;
		private final int expectedSize;

		public DataOutputBytes(@NonNull IOConsumer<ExtendedDataOutput> creator, int expectedSize)
		{
			this.creator = Objects.requireNonNull(creator);

			if(expectedSize <= 0) throw new IllegalArgumentException("expectedSize should be larger than 0");
			this.expectedSize = expectedSize;
		}

		@Override
		public InputStream asInputStream()
			throws IOException
		{
			return new ByteArrayInputStream(toByteArray());
		}

		@Override
		public byte[] toByteArray()
			throws IOException
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream(expectedSize);
			try(ExtendedDataOutput dataOut = new ExtendedDataOutputStream(out))
			{
				creator.accept(dataOut);
			}
			return out.toByteArray();
		}

		@Override
		public void asChunks(ByteArrayConsumer consumer)
			throws IOException
		{
			try(ExtendedDataOutput dataOut = new ExtendedDataOutputStream(new ChunkOutputStream(4096, consumer)))
			{
				creator.accept(dataOut);
			}
		}
	}
}
