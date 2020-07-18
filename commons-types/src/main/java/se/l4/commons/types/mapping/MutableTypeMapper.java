package se.l4.commons.types.mapping;

import java.util.function.Function;

import edu.umd.cs.findbugs.annotations.NonNull;
import se.l4.commons.types.internal.mapping.MutableTypeMapperImpl;
import se.l4.commons.types.reflect.TypeRef;

public interface MutableTypeMapper<I extends ResolutionEncounter<O>, O>
	extends TypeMapper<I, O>
{
	/**
	 * Add a specific value that will be returned for the given class.
	 *
	 * @param type
	 * @param result
	 */
	void addSpecific(@NonNull Class<?> type, @NonNull O result);

	/**
	 * Add a specific value that will be returned for the given type.
	 *
	 * @param type
	 * @param result
	 */
	void addSpecific(@NonNull TypeRef type, @NonNull O result);

	/**
	 * Add a resolver tied to a class, the resolver will be invoked for the
	 * type, its subclasses (if a class) or classes that implement it (if an
	 * interface).
	 *
	 * @param type
	 * @param resolver
	 */
	void addHierarchyResolver(Class<?> type, Resolver<I, O> resolver);

	/**
	 * Add a resolver tied to a type, the resolver will be invoked for the
	 * type, its subclasses (if a class) or classes that implement it (if an
	 * interface).
	 *
	 * @param type
	 * @param resolver
	 */
	void addHierarchyResolver(@NonNull TypeRef type, Resolver<I, O> resolver);

	/**
	 * Add a resolver that will be called for any type before the hierarchy
	 * resolvers are asked.
	 *
	 * @param resolver
	 */
	void addAnnotationResolver(Resolver<I, O> resolver);

	/**
	 * Start creating a new instance.
	 *
	 * @param <I>
	 * @param <O>
	 * @param encounterCreator
	 * @return
	 */
	static <I extends ResolutionEncounter<O>, O> Builder <I, O> create(Function<TypeRef, I> encounterCreator)
	{
		return MutableTypeMapperImpl.builder(encounterCreator);
	}

	interface Builder<I extends ResolutionEncounter<O>, O>
	{
		/**
		 * Set the strategy that is used for resolvers that throw errors.
		 *
		 * @param strategy
		 * @return
		 */
		Builder<I, O> withErrorStrategy(TypeMapper.ErrorStrategy strategy);

		/**
		 * Set the maximum number of items to cache. This will try to cache
		 * combinations of {@link TypeRef} and the output those generates.
		 *
		 * @param maximumSize
		 * @return
		 */
		Builder<I, O> withCaching(int maximumSize);

		/**
		 * Set that mapped instances should be cached. This is used as an
		 * additional layer to {@link #withCaching(int)} to deduplicate.
		 *
		 * @param deduplicator
		 * @return
		 */
		Builder<I, O> withOutputDeduplication(OutputDeduplicator<O> deduplicator);

		/**
		 * Create the instance.
		 *
		 * @return
		 */
		MutableTypeMapper<I, O> build();
	}
}
