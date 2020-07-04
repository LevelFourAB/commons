package se.l4.commons.serialization;

import java.util.Collection;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

import se.l4.commons.serialization.spi.NamingCallback;
import se.l4.commons.serialization.spi.SerializerResolver;
import se.l4.commons.serialization.spi.SerializerResolverChain;
import se.l4.commons.serialization.spi.SerializerResolverRegistry;
import se.l4.commons.types.InstanceFactory;

/**
 * Implementation of {@link SerializerCollection} that wraps another
 * collection.
 *
 * @author Andreas Holstenson
 *
 */
public class WrappedSerializerCollection
	extends AbstractSerializerCollection
{
	private final SerializerResolverRegistry resolverRegistry;
	private final SerializerCollection other;

	public WrappedSerializerCollection(SerializerCollection other)
	{
		this.other = other;
		resolverRegistry = new SerializerResolverRegistry(
			other.getInstanceFactory(),
			new NamingCallback()
			{
				@Override
				public void registerIfNamed(Class<?> from, Serializer<?> serializer)
				{
					WrappedSerializerCollection.this.registerIfNamed(from, serializer);
				}
			}
		);
	}

	@Override
	public InstanceFactory getInstanceFactory()
	{
		return other.getInstanceFactory();
	}

	@Override
	public <T> SerializerCollection bind(Class<T> type, SerializerResolver<? extends T> resolver)
	{
		resolverRegistry.bind(type, resolver);

		return this;
	}

	@Override
	public Optional<? extends SerializerResolver<?>> getResolver(Class<?> type)
	{
		Optional<? extends SerializerResolver<?>> r1 = resolverRegistry.getResolver(type);
		Optional<? extends SerializerResolver<?>> r2 = other.getResolver(type);

		if(r1.isPresent() && r2.isPresent())
		{
			Collection<? extends SerializerResolver<?>> merged = ImmutableList.of(r1.get(), r2.get());
			return Optional.of(new SerializerResolverChain(merged));
		}
		else if(r1.isPresent())
		{
			return r1;
		}
		else if(r2.isPresent())
		{
			return r2;
		}
		else
		{
			return Optional.empty();
		}
	}
}
