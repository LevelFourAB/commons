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
import se.l4.commons.serialization.internal.SerializerFormatDefinitionBuilderImpl;

/**
 * Serializer that will attempt to dynamically resolve serializers based on
 * their name.
 *
 * @author Andreas Holstenson
 *
 */
public class CompactDynamicSerializer
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

			formatDefinition = new SerializerFormatDefinitionBuilderImpl()
				.list(SerializerFormatDefinition.any())
				.build();
		}

		@Override
		public Object read(StreamingInput in)
			throws IOException
		{
			// Read start of object
			in.next(Token.LIST_START);

			in.next(in.peek() == Token.NULL ? Token.NULL : Token.VALUE);
			String namespace;
			if(in.current() == Token.NULL)
			{
				namespace = "";
			}
			else
			{
				namespace = in.readString();
			}

			in.next(Token.VALUE);
			String name = in.readString();

			Object result = null;

			Optional<? extends Serializer<?>> serializer = collection.find(namespace, name);
			if(! serializer.isPresent())
			{
				throw new SerializationException("No serializer found for `" + name + (namespace != null ? "` in `" + namespace + "`" : "`"));
			}

			result = serializer.get().read(in);

			in.next(Token.LIST_END);
			return result;
		}

		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void write(Object object, StreamingOutput stream)
			throws IOException
		{
			Serializer<?> serializer = collection.find(object.getClass());

			QualifiedName qname = serializer.getName()
				.orElseThrow(() -> new SerializationException("Tried to use dynamic serialization for " + object.getClass() + ", but type has no name"));

			stream.writeListStart();

			if(! qname.getNamespace().equals(""))
			{
				stream.writeString(qname.getNamespace());
			}
			else
			{
				stream.writeNull();
			}

			stream.writeString(qname.getName());

			stream.writeObject((Serializer) serializer, object);

			stream.writeListEnd();
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
