package se.l4.commons.serialization;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import se.l4.commons.io.Bytes;
import se.l4.commons.serialization.collections.ListSerializerResolver;
import se.l4.commons.serialization.collections.MapSerializerResolver;
import se.l4.commons.serialization.collections.SetSerializerResolver;
import se.l4.commons.serialization.enums.EnumSerializerResolver;
import se.l4.commons.serialization.standard.BooleanSerializer;
import se.l4.commons.serialization.standard.ByteArraySerializer;
import se.l4.commons.serialization.standard.ByteSerializer;
import se.l4.commons.serialization.standard.BytesSerializer;
import se.l4.commons.serialization.standard.CharacterSerializer;
import se.l4.commons.serialization.standard.DoubleSerializer;
import se.l4.commons.serialization.standard.FloatSerializer;
import se.l4.commons.serialization.standard.IntSerializer;
import se.l4.commons.serialization.standard.LongSerializer;
import se.l4.commons.serialization.standard.OptionalSerializerResolver;
import se.l4.commons.serialization.standard.ShortSerializer;
import se.l4.commons.serialization.standard.StringSerializer;
import se.l4.commons.serialization.standard.UuidSerializer;
import se.l4.commons.types.DefaultInstanceFactory;
import se.l4.commons.types.InstanceFactory;

/**
 * Default implementation of {@link Serializers}.
 *
 * @author Andreas Holstenson
 *
 */
public class DefaultSerializers
	extends AbstractSerializers
{
	private final InstanceFactory instanceFactory;

	public DefaultSerializers()
	{
		this(new DefaultInstanceFactory());
	}

	public DefaultSerializers(InstanceFactory instanceFactory)
	{
		this.instanceFactory = instanceFactory;

		// Standard types
		bind(Boolean.class, new BooleanSerializer());
		bind(Byte.class, new ByteSerializer());
		bind(Character.class, new CharacterSerializer());
		bind(Double.class, new DoubleSerializer());
		bind(Float.class, new FloatSerializer());
		bind(Integer.class, new IntSerializer());
		bind(Long.class, new LongSerializer());
		bind(Short.class, new ShortSerializer());
		bind(String.class, new StringSerializer());
		bind(byte[].class, new ByteArraySerializer());
		bind(UUID.class, new UuidSerializer());

		// Collections
		bind(List.class, new ListSerializerResolver());
		bind(Map.class, new MapSerializerResolver());
		bind(Set.class, new SetSerializerResolver());

		// Enums
		bind(Enum.class, new EnumSerializerResolver());

		// Optional<T>
		bind(Optional.class, new OptionalSerializerResolver());

		bind(Bytes.class, new BytesSerializer());
	}

	@Override
	public InstanceFactory getInstanceFactory()
	{
		return instanceFactory;
	}
}
