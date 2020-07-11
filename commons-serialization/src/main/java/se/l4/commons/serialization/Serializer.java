package se.l4.commons.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.UnknownNullness;
import se.l4.commons.io.StreamingCodec;
import se.l4.commons.serialization.format.StreamingFormat;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.StreamingOutput;
import se.l4.commons.serialization.format.Token;

/**
 * Serializer for a specific class. A serializer is used to read and write
 * objects and is usually bound to a specific class. Serializers are retrieved
 * via a {@link Serializers}.
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
	 * Take this serializer and transform it into a {@link StreamingCodec}.
	 *
	 * @param format
	 * @return
	 */
	@NonNull
	default StreamingCodec<T> toCodec(StreamingFormat format)
	{
		return new StreamingCodec<T>()
		{
			@Override
			public T read(InputStream in)
				throws IOException
			{
				try(StreamingInput streamingIn = format.createInput(in))
				{
					if(streamingIn.peek() == Token.NULL && ! (Serializer.this instanceof NullHandling))
					{
						/*
						 * If this serializer doesn't handle null values read
						 * it and return null.
						 */
						streamingIn.next(Token.NULL);
						return null;
					}

					return Serializer.this.read(streamingIn);
				}
			}

			@Override
			public void write(T item, OutputStream out)
				throws IOException
			{
				try(StreamingOutput streamingOut = format.createOutput(out))
				{
					if(item == null && ! (this instanceof NullHandling))
					{
						streamingOut.writeNull("root");
					}
					else
					{
						Serializer.this.write(item, "root", streamingOut);
					}
				}
			}
		};
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
