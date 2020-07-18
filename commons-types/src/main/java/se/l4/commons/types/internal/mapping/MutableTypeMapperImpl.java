package se.l4.commons.types.internal.mapping;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.MultiReaderFastList;

import se.l4.commons.types.Types;
import se.l4.commons.types.mapping.MutableTypeMapper;
import se.l4.commons.types.mapping.OutputDeduplicator;
import se.l4.commons.types.mapping.ResolutionEncounter;
import se.l4.commons.types.mapping.Resolver;
import se.l4.commons.types.matching.MutableTypeMatchingMultimap;
import se.l4.commons.types.matching.TypeMatchingMultiReaderFastListMultimap;
import se.l4.commons.types.reflect.TypeRef;

public class MutableTypeMapperImpl<I extends ResolutionEncounter<O>, O>
	extends AbstractTypeMapper<I, O>
	implements MutableTypeMapper<I, O>
{
	private final MutableTypeMatchingMultimap<Resolver<I, O>> resolvers;
	private final MutableList<Resolver<I, O>> annotationResolvers;

	public MutableTypeMapperImpl(
		Function<TypeRef, I> inputCreator,
		ErrorStrategy errorStrategy,
		int cachingSize,
		OutputDeduplicator<O> deduplicator,
		MutableTypeMatchingMultimap<Resolver<I, O>> resolvers,
		MutableList<Resolver<I, O>> annotationResolvers
	)
	{
		super(inputCreator, errorStrategy, cachingSize, deduplicator, resolvers, annotationResolvers);

		this.resolvers = resolvers;
		this.annotationResolvers = annotationResolvers;
	}

	@Override
	public void addSpecific(Class<?> type, O result)
	{
		addSpecific(Types.reference(type), result);
	}

	@Override
	public void addSpecific(TypeRef type, O result)
	{
		addHierarchyResolver(type, in -> in.getType().isSameType(type) ? Optional.of(result) : Optional.empty());
	}

	@Override
	public void addHierarchyResolver(Class<?> type, Resolver<I, O> resolver)
	{
		addHierarchyResolver(Types.reference(type), resolver);
	}

	@Override
	public void addHierarchyResolver(TypeRef type, Resolver<I, O> resolver)
	{
		resolvers.put(type.wrap(), resolver);
	}

	@Override
	public void addAnnotationResolver(Resolver<I, O> resolver)
	{
		annotationResolvers.add(resolver);
	}

	public static <I extends ResolutionEncounter<O>, O> Builder<I, O> builder(
		Function<TypeRef, I> encounterCreator
	)
	{
		return new Builder<I, O>()
		{
			private int caching = 0;
			private ErrorStrategy errorStrategy = ErrorStrategy.BREAK;
			private OutputDeduplicator<O> deduplicator = OutputDeduplicator.none();

			@Override
			public Builder<I, O> withCaching(int maximumSize)
			{
				this.caching = maximumSize;
				return this;
			}

			@Override
			public Builder<I, O> withErrorStrategy(ErrorStrategy strategy)
			{
				this.errorStrategy = Objects.requireNonNull(strategy);
				return this;
			}

			@Override
			public Builder<I, O> withOutputDeduplication(OutputDeduplicator<O> deduplicator)
			{
				this.deduplicator = Objects.requireNonNull(deduplicator);
				return this;
			}

			@Override
			public MutableTypeMapper<I, O> build()
			{
				return new MutableTypeMapperImpl<>(
					encounterCreator,
					errorStrategy,
					caching,
					deduplicator,
					new TypeMatchingMultiReaderFastListMultimap<>(),
					MultiReaderFastList.newList()
				);
			}
		};
	}
}
