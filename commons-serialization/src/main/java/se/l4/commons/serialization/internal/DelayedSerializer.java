package se.l4.commons.serialization.internal;

import java.io.IOException;
import java.lang.annotation.Annotation;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerCollection;
import se.l4.commons.serialization.SerializerFormatDefinition;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.StreamingOutput;
import se.l4.commons.serialization.spi.Type;

/**
 * Serializer that is delayed in that it will not be assigned until the entire
 * serializer chain is resolved. Used to solve recursive serialization.
 *
 * @author Andreas Holstenson
 *
 * @param <T>
 */
public class DelayedSerializer<T>
	implements Serializer<T>
{
	private final SerializerCollection collection;
	private final Type type;
	private final Annotation[] hints;

	private volatile Serializer<T> instance;

	public DelayedSerializer(SerializerCollection collection, Type type, Annotation[] hints)
	{
		this.collection = collection;
		this.type = type;
		this.hints = hints;
	}

	@SuppressWarnings("unchecked")
	private void ensureSerializer()
	{
		if(instance == null)
		{
			instance = (Serializer<T>) collection.find(type, hints);
			if(instance instanceof DelayedSerializer)
			{
				instance = null;
			}
		}
	}

	@Override
	public T read(StreamingInput in) throws IOException
	{
		ensureSerializer();

		return instance.read(in);
	}


	@Override
	public void write(T object, String name, StreamingOutput stream)
		throws IOException
	{
		ensureSerializer();

		instance.write(object, name, stream);
	}

	@Override
	public SerializerFormatDefinition getFormatDefinition()
	{
		ensureSerializer();

		if(instance == null) return null;

		return instance.getFormatDefinition();
	}
}
