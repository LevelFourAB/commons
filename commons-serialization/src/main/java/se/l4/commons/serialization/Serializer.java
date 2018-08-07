package se.l4.commons.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Function;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.UnknownNullness;
import se.l4.commons.serialization.format.BinaryInput;
import se.l4.commons.serialization.format.BinaryOutput;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.StreamingOutput;

/**
 * Serializer for a specific class. A serializer is used to read and write
 * objects and is usually bound to a specific class. Serializers are retrieved
 * via a {@link SerializerCollection}.
 *
 * @author Andreas Holstenson
 *
 * @param <T>
 */
public interface Serializer<T>
	extends SerializerOrResolver<T>
{
	/**
	 * Read an object from the specified stream.
	 *
	 * @param in
	 * @return
	 * @throws IOException
	 */
	@Nullable
	T read(@NonNull StreamingInput in)
		throws IOException;

	/**
	 * Write and object to the specified stream.
	 *
	 * @param object
	 *   object to write, if the serializer implements {@link NullHandling}
	 *   this may be {@code null}, if not the serializer can assume it is not
	 *   {@code null}
	 * @param name
	 * 	 the name the object should have, should be passed along to the output
	 * @param out
	 * 	 the stream to use for writing
	 * @throws IOException
	 *   if unable to write the object
	 */
	void write(@UnknownNullness T object, @NonNull String name, @NonNull StreamingOutput out)
		throws IOException;

	/**
	 * Get the definition that describes what this serializer can
	 * read and write.
	 *
	 * @return
	 */
	@NonNull
	default SerializerFormatDefinition getFormatDefinition()
	{
		return SerializerFormatDefinition.unknown();
	}

	/**
	 * Turn an object into a byte array.
	 *
	 * @param instance
	 * @return
	 */
	@Nullable
	default byte[] toBytes(@Nullable T instance)
	{
		/*
		 * If the value being serialized is null and we do not handle null
		 * return null data.
		 */
		if(instance == null && ! (this instanceof NullHandling)) return null;

		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
			BinaryOutput out = new BinaryOutput(baos);
			this.write(instance, "root", out);
			out.flush();
			return baos.toByteArray();
		}
		catch(IOException e)
		{
			throw new SerializationException(e);
		}
	}

	/**
	 * Create a new function that turns objects into byte arrays.
	 *
	 * @return
	 */
	@NonNull
	default Function<T, byte[]> toBytes()
	{
		return this::toBytes;
	}

	/**
	 * Read an instance from the given byte data.
	 *
	 * @param data
	 * @return
	 */
	@Nullable
	default T fromBytes(@Nullable byte[] data)
	{
		if(data == null) return null;

		try
		{
			ByteArrayInputStream in = new ByteArrayInputStream(data);
			BinaryInput bin = new BinaryInput(in);
			return this.read(bin);
		}
		catch(IOException e)
		{
			throw new SerializationException(e);
		}
	}

	/**
	 * Create a new function that turns byte arrays into objects.
	 *
	 * @return
	 */
	@NonNull
	default Function<byte[], T> fromBytes()
	{
		return this::fromBytes;
	}

	/**
	 * Marker interface used when a serializer wants to handle an incoming
	 * {@code null} value. If a serializer does not implement this interface
	 * {@code null} values are mapped to default values automatically by
	 * the reflection serializer.
	 */
	interface NullHandling
	{
	}
}
