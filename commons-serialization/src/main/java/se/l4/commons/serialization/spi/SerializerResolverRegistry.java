package se.l4.commons.serialization.spi;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.primitives.Primitives;

import org.eclipse.collections.api.list.ListIterable;

import se.l4.commons.serialization.SerializationException;
import se.l4.commons.serialization.Serializers;
import se.l4.commons.serialization.collections.ArraySerializerResolver;
import se.l4.commons.types.InstanceFactory;
import se.l4.commons.types.matching.ClassMatchingConcurrentHashMultimap;
import se.l4.commons.types.matching.MutableClassMatchingMultimap;

/**
 * Finder of {@link SerializerResolver}s, used when implementing a
 * {@link Serializers}.
 *
 * @author Andreas Holstenson
 *
 */
public class SerializerResolverRegistry
{
	private static final SerializerResolver<?> ARRAY_RESOLVER = new ArraySerializerResolver();

	private final InstanceFactory instanceFactory;
	private final NamingCallback naming;

	private final MutableClassMatchingMultimap<Object, SerializerResolver<?>> boundTypeToResolver;
	private final LoadingCache<Class<?>, Optional<SerializerResolver<?>>> typeToResolverCache;

	public SerializerResolverRegistry(InstanceFactory instanceFactory, NamingCallback naming)
	{
		this.instanceFactory = instanceFactory;
		this.naming = naming;

		boundTypeToResolver = new ClassMatchingConcurrentHashMultimap<>();
		typeToResolverCache = CacheBuilder.newBuilder()
			.build(new CacheLoader<Class<?>, Optional<SerializerResolver<?>>>()
			{
				@Override
				public Optional<SerializerResolver<?>> load(Class<?> key)
					throws Exception
				{
					SerializerResolver<?> result = findOrCreateSerializerResolver(key);
					return Optional.ofNullable(result);
				}
			});
	}

	/**
	 * Bind a resolver for the given type.
	 *
	 * @param type
	 * @param resolver
	 */
	public <T> void bind(Class<T> type, SerializerResolver<? extends T> resolver)
	{
		typeToResolverCache.put(type, Optional.<SerializerResolver<?>>of(resolver));
		boundTypeToResolver.put(type, resolver);
	}

	/**
	 * Get a resolver for the given type, returning {@code null} if
	 * the resolver can not be found.
	 *
	 * @param type
	 *   the {@link Class} to find a resolver for
	 * @return
	 *   the found resolver, or {@code null} if no resolver is found
	 * @throws SerializationException
	 *   if the resolver could not be constructed from some reason
	 */
	public Optional<SerializerResolver<?>> getResolver(Class<?> type)
	{
		try
		{
			return typeToResolverCache.get(Primitives.wrap(type));
		}
		catch(ExecutionException e)
		{
			Throwables.throwIfInstanceOf(e.getCause(), SerializationException.class);

			throw new SerializationException("Unable to retrieve serializer for " + type + "; " + e.getCause().getMessage(), e.getCause());
		}
	}

	protected SerializerResolver<?> findOrCreateSerializerResolver(Class<?> from)
	{
		SerializerResolver<?> resolver = createViaUse(from);
		if(resolver != null)
		{
			return resolver;
		}

		if(from.isArray())
		{
			// Arrays have special treatment, always use the array resolver
			return ARRAY_RESOLVER;
		}

		ListIterable<SerializerResolver<?>> resolvers = boundTypeToResolver.getBest(from);
		if(resolvers.isEmpty())
		{
			return null;
		}

		if(resolvers.size() == 1)
		{
			return resolvers.iterator().next();
		}

		return new SerializerResolverChain(resolvers);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected SerializerResolver<?> createViaUse(Class<?> from)
	{
		return null;
	}
}
