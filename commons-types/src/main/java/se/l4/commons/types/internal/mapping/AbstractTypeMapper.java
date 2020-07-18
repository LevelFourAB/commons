package se.l4.commons.types.internal.mapping;

import java.util.Optional;
import java.util.function.Function;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;

import se.l4.commons.types.Types;
import se.l4.commons.types.mapping.Mapped;
import se.l4.commons.types.mapping.OutputDeduplicator;
import se.l4.commons.types.mapping.ResolutionEncounter;
import se.l4.commons.types.mapping.Resolver;
import se.l4.commons.types.mapping.TypeMapper;
import se.l4.commons.types.matching.TypeMatchingMultimap;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Abstract implementation of {@link TypeMapper} that uses a {@link TypeMatchingMultimap}
 * for resolvers and caching to reduce how
 *
 * @param <I>
 * @param <O>
 */
public abstract class AbstractTypeMapper<I extends ResolutionEncounter<O>, O>
	implements TypeMapper<I, O>
{
	private final ErrorStrategy errorStrategy;
	private final Function<TypeRef, I> encounterCreator;

	private final TypeMatchingMultimap<Resolver<I, O>> resolvers;
	private final ListIterable<Resolver<I, O>> annotationResolvers;

	private final LoadingCache<TypeRef, Mapped<O>> cache;
	private final OutputDeduplicator<O> deduplicator;

	public AbstractTypeMapper(
		Function<TypeRef, I> encounterCreator,
		ErrorStrategy errorStrategy,
		int cachingSize,
		OutputDeduplicator<O> deduplicator,
		TypeMatchingMultimap<Resolver<I, O>> resolvers,
		ListIterable<Resolver<I, O>> annotationResolvers
	)
	{
		this.errorStrategy = errorStrategy;
		this.encounterCreator = encounterCreator;
		this.deduplicator = deduplicator;
		this.resolvers = resolvers;
		this.annotationResolvers = annotationResolvers;

		cache = Caffeine.newBuilder()
			.maximumSize(cachingSize)
			.build(this::resolve);
	}

	@Override
	public Mapped<O> get(Class<?> type)
	{
		return get(Types.reference(type));
	}

	@Override
	public Mapped<O> get(TypeRef type)
	{
		return cache.get(type);
	}

	private Mapped<O> resolve(TypeRef type)
	{
		type = type.wrap();
		I encounter = encounterCreator.apply(type);

		// Collect all of the resolvers to try in order
		MutableList<Resolver<I, O>> resolversToTry = Lists.mutable.ofAll(annotationResolvers);
		resolversToTry.addAllIterable(resolvers.getAll(type).collect(m -> m.getData()));

		// Attempt to resolve
		MutableList<Exception> exceptions = Lists.mutable.empty();
		for(Resolver<I, O> resolver : resolversToTry)
		{
			try
			{
				Optional<O> result = resolver.resolve(encounter);
				if(result.isPresent())
				{
					// Run the output through the cache
					O cached = deduplicator.deduplicate(result.get());
					return new MappedImpl<>(cached, exceptions);
				}
			}
			catch(Exception e)
			{
				exceptions.add(e);

				if(errorStrategy == ErrorStrategy.BREAK)
				{
					// Break and return errors
					break;
				}
			}
		}

		return new MappedImpl<>(null, exceptions);
	}
}
