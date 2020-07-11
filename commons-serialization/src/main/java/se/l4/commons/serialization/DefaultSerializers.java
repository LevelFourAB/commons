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
import se.l4.commons.serialization.internal.UseSerializerResolver;
import se.l4.commons.serialization.spi.NamingCallback;
import se.l4.commons.serialization.spi.SerializerResolver;
import se.l4.commons.serialization.spi.SerializerResolverRegistry;
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
	private final SerializerResolverRegistry resolverRegistry;

	public DefaultSerializers()
	{
		this(new DefaultInstanceFactory());
	}

	public DefaultSerializers(InstanceFactory instanceFactory)
	{
		this.instanceFactory = instanceFactory;

		resolverRegistry = new SerializerResolverRegistry(
			instanceFactory,
			new NamingCallback()
			{
				@Override
				public void registerIfNamed(Class<?> from, Serializer<?> serializer)
				{
					DefaultSerializers.this.registerIfNamed(from, serializer);
				}
			}
		);

		// Standard types
		bind(Boolean.class, new BooleanSerializer(), "", "boolean");
		bind(Byte.class, new ByteSerializer(), "", "byte");
		bind(Character.class, new CharacterSerializer(), "", "char");
		bind(Double.class, new DoubleSerializer(), "", "double");
		bind(Float.class, new FloatSerializer(), "", "float");
		bind(Integer.class, new IntSerializer(), "", "integer");
		bind(Long.class, new LongSerializer(), "", "long");
		bind(Short.class, new ShortSerializer(), "", "short");
		bind(String.class, new StringSerializer(), "", "string");
		bind(byte[].class, new ByteArraySerializer(), "", "byte[]");
		bind(UUID.class, new UuidSerializer(), "", "uuid");

		// Collections
		bind(List.class, new ListSerializerResolver());
		bind(Map.class, new MapSerializerResolver());
		bind(Set.class, new SetSerializerResolver());

		// Enums
		bind(Enum.class, new EnumSerializerResolver());

		// Optional<T>
		bind(Optional.class, new OptionalSerializerResolver());

		bind(Bytes.class, new BytesSerializer());

		bind(Object.class, new UseSerializerResolver(instanceFactory));
	}

	@Override
	public InstanceFactory getInstanceFactory()
	{
		return instanceFactory;
	}

	@Override
	public <T> Serializers bind(Class<T> type, SerializerResolver<? extends T> resolver)
	{
		resolverRegistry.bind(type, resolver);

		return this;
	}

	public Optional<SerializerResolver<?>> getResolver(Class<?> type)
	{
		return resolverRegistry.getResolver(type);
	}
}
