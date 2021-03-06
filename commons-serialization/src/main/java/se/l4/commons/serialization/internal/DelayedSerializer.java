package se.l4.commons.serialization.internal;

import java.io.IOException;
import java.lang.annotation.Annotation;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerFormatDefinition;
import se.l4.commons.serialization.Serializers;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.StreamingOutput;
import se.l4.commons.types.reflect.TypeRef;

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
	private volatile Serializer<T> instance;

	public DelayedSerializer(Serializers collection, TypeRef type)
	{
		instance = new Serializer<T>()
		{
			@SuppressWarnings("unchecked")
			private void ensureSerializer()
			{
				Serializer<T> resolved = (Serializer<T>) collection.find(type);
				if(resolved instanceof DelayedSerializer)
				{
					return;
				}

				instance = resolved;
			}

			@Override
			public T read(StreamingInput in) throws IOException
			{
				ensureSerializer();

				return instance.read(in);
			}


			@Override
			public void write(T object, StreamingOutput stream)
				throws IOException
			{
				ensureSerializer();

				instance.write(object, stream);
			}

			@Override
			public SerializerFormatDefinition getFormatDefinition()
			{
				ensureSerializer();

				if(instance == this) return null;

				return instance.getFormatDefinition();
			}
		};
	}

	@Override
	public T read(StreamingInput in) throws IOException
	{
		return instance.read(in);
	}


	@Override
	public void write(T object, StreamingOutput stream)
		throws IOException
	{
		instance.write(object, stream);
	}

	@Override
	public SerializerFormatDefinition getFormatDefinition()
	{
		return instance.getFormatDefinition();
	}
}
