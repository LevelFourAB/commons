package se.l4.commons.serialization;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import se.l4.commons.serialization.collections.ListSerializerResolver;
import se.l4.commons.serialization.collections.MapSerializerResolver;
import se.l4.commons.serialization.collections.SetSerializerResolver;
import se.l4.commons.serialization.enums.EnumSerializerResolver;
import se.l4.commons.serialization.spi.DefaultInstanceFactory;
import se.l4.commons.serialization.spi.InstanceFactory;
import se.l4.commons.serialization.spi.NamingCallback;
import se.l4.commons.serialization.spi.SerializerResolver;
import se.l4.commons.serialization.spi.SerializerResolverRegistry;
import se.l4.commons.serialization.standard.BooleanSerializer;
import se.l4.commons.serialization.standard.ByteArraySerializer;
import se.l4.commons.serialization.standard.DoubleSerializer;
import se.l4.commons.serialization.standard.FloatSerializer;
import se.l4.commons.serialization.standard.IntSerializer;
import se.l4.commons.serialization.standard.LongSerializer;
import se.l4.commons.serialization.standard.ShortSerializer;
import se.l4.commons.serialization.standard.StringSerializer;
import se.l4.commons.serialization.standard.UuidSerializer;

/**
 * Default implementation of {@link SerializerCollection}.
 * 
 * @author Andreas Holstenson
 *
 */
public class DefaultSerializerCollection
	extends AbstractSerializerCollection
{
	private final InstanceFactory instanceFactory;
	private final SerializerResolverRegistry resolverRegistry;
	
	public DefaultSerializerCollection()
	{
		this(new DefaultInstanceFactory());
	}
	
	public DefaultSerializerCollection(InstanceFactory instanceFactory)
	{
		this.instanceFactory = instanceFactory;
		
		resolverRegistry = new SerializerResolverRegistry(
			instanceFactory,
			new NamingCallback()
			{
				@Override
				public void registerIfNamed(Class<?> from, Serializer<?> serializer)
				{
					DefaultSerializerCollection.this.registerIfNamed(from, serializer);
				}
			}
		);
		
		// Standard types
		bind(Boolean.class, new BooleanSerializer(), "", "boolean");
		bind(Float.class, new FloatSerializer(), "", "float");
		bind(Double.class, new DoubleSerializer(), "", "double");
		bind(Short.class, new ShortSerializer(), "", "short");
		bind(Integer.class, new IntSerializer(), "", "integer");
		bind(Long.class, new LongSerializer(), "", "long");
		bind(String.class, new StringSerializer(), "", "string");
		bind(byte[].class, new ByteArraySerializer(), "", "byte[]");
		bind(UUID.class, new UuidSerializer(), "", "uuid");
		
		// Collections
		bind(List.class, new ListSerializerResolver());
		bind(Map.class, new MapSerializerResolver());
		bind(Set.class, new SetSerializerResolver());
		
		// Enums
		bind(Enum.class, new EnumSerializerResolver());
	}
	
	@Override
	public InstanceFactory getInstanceFactory()
	{
		return instanceFactory;
	}
	
	@Override
	public <T> SerializerCollection bind(Class<T> type, SerializerResolver<? extends T> resolver)
	{
		resolverRegistry.bind(type, resolver);
		
		return this;
	}
	
	public SerializerResolver<?> getResolver(Class<?> type)
	{
		return resolverRegistry.getResolver(type);
	}
}
