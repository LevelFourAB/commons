package se.l4.commons.serialization.standard;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import se.l4.commons.serialization.QualifiedName;
import se.l4.commons.serialization.SerializationException;
import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerFormatDefinition;
import se.l4.commons.serialization.SerializerOrResolver;
import se.l4.commons.serialization.SerializerResolver;
import se.l4.commons.serialization.Serializers;
import se.l4.commons.serialization.TypeEncounter;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.StreamingOutput;
import se.l4.commons.serialization.format.Token;
import se.l4.commons.serialization.format.ValueType;

/**
 * Serializer that will attempt to dynamically resolve serializers based on
 * their name.
 *
 * @author Andreas Holstenson
 *
 */
public class DynamicSerializer
	implements SerializerResolver<Object>
{
	@Override
	public Optional<? extends SerializerOrResolver<Object>> find(TypeEncounter encounter)
	{
		return Optional.of(new Impl(encounter.getCollection()));
	}

	public static class Impl
		implements Serializer<Object>
	{
		private final Serializers collection;
		private final SerializerFormatDefinition formatDefinition;

		public Impl(Serializers collection)
		{
			this.collection = collection;

			formatDefinition = SerializerFormatDefinition.builder()
				.field("namespace").using(ValueType.STRING)
				.field("name").using(ValueType.STRING)
				.field("value").using(SerializerFormatDefinition.any())
				.build();
		}

		@Override
		public Object read(StreamingInput in)
			throws IOException
		{
			// Read start of object
			in.next(Token.OBJECT_START);

			String namespace = "";
			String name = null;

			Object result = null;
			boolean resultRead = false;

			/*
			* Loop through values, first reading namespace and name. If value
			* is encountered before name abort.
			*/
			while(in.peek() != Token.OBJECT_END)
			{
				in.next(Token.KEY);
				String key = in.readString();

				if("namespace".equals(key))
				{
					in.next(Token.VALUE);

					String value = in.readString();
					namespace = value;
				}
				else if("name".equals(key))
				{
					in.next(Token.VALUE);
					String value = in.readString();

					name = value;
				}
				else if("value".equals(key))
				{
					if(name == null)
					{
						throw new SerializationException("Name of type must come before dynamic value");
					}

					resultRead = true;

					Optional<? extends Serializer<?>> serializer = collection.find(namespace, name);
					if(! serializer.isPresent())
					{
						throw new SerializationException("No serializer found for `" + name + (namespace != null ? "` in `" + namespace + "`" : "`"));
					}

					result = serializer.get().read(in);
				}
				else
				{
					in.skipValue();
				}
			}

			if(! resultRead)
			{
				throw new SerializationException("Dynamic serialization requires a value");
			}

			in.next(Token.OBJECT_END);
			return result;
		}

		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void write(Object object, String name, StreamingOutput stream)
			throws IOException
		{
			Serializer<?> serializer = collection.find(object.getClass());

			QualifiedName qname = serializer.getName()
				.orElseThrow(() -> new SerializationException("Tried to use dynamic serialization for " + object.getClass() + ", but type has no name"));

			stream.writeObjectStart(name);

			if(! qname.getNamespace().equals(""))
			{
				stream.write("namespace", qname.getNamespace());
			}

			stream.write("name", qname.getName());

			((Serializer) serializer).write(object, "value", stream);

			stream.writeObjectEnd(name);
		}

		@Override
		public SerializerFormatDefinition getFormatDefinition()
		{
			return formatDefinition;
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(collection);
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Impl other = (Impl) obj;
			return Objects.equals(collection, other.collection);
		}
	}
}
