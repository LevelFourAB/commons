package se.l4.commons.serialization.spi;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import org.eclipse.collections.api.RichIterable;

import se.l4.commons.serialization.Serializer;

/**
 * Chain of {@link SerializerResolver}s that are tried in order. The first
 * resolver that returns a {@link Serializer} determines the result.
 */
public class SerializerResolverChain
	implements SerializerResolver<Object>
{
	private final SerializerResolver<?>[] resolvers;
	private final Set<Class<? extends Annotation>> hints;

	@SuppressWarnings({ "rawtypes" })
	public SerializerResolverChain(RichIterable<? extends SerializerResolver<?>> resolvers)
	{
		int i = 0;
		ImmutableSet.Builder<Class<? extends Annotation>> builder = ImmutableSet.builder();
		SerializerResolver[] resolverArray = new SerializerResolver[resolvers.size()];
		for(SerializerResolver<?> r : resolvers)
		{
			resolverArray[i++] = r;
			builder.addAll(r.getHints());
		}

		this.hints = builder.build();
		this.resolvers = resolverArray;
	}

	@Override
	@SuppressWarnings({ "unchecked" , "rawtypes" })
	public Optional<Serializer<Object>> find(TypeEncounter encounter)
	{
		for(SerializerResolver<?> resolver : resolvers)
		{
			Optional serializer = resolver.find(encounter);
			if(serializer.isPresent())
			{
				return serializer;
			}
		}

		return Optional.empty();
	}

	@Override
	public Set<Class<? extends Annotation>> getHints()
	{
		return hints;
	}
}
