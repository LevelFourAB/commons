package se.l4.commons.types.matching;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.list.ListIterable;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Specialized map to associated classes and interfaces with data. This map
 * supports matching against the type hierarchy to fetch data. It also supports
 * associating multiple data instances with a certain type.
 */
public interface ClassMatchingMultimap<T, D>
{
	/**
	 * Get data directly associated with the given type.
	 *
	 * @param type
	 *   the type get data for
	 * @return
	 *   optional containing data if there is direct match or empty optional
	 */
	@NonNull
	ListIterable<D> get(@NonNull Class<? extends T> type);

	/**
	 * Get the best matching data for the given type.
	 *
	 * @param type
	 *   class to match against
	 * @return
	 *   optional with data or empty optional
	 */
	@NonNull
	ListIterable<D> getBest(@NonNull Class<? extends T> type);

	/**
	 * Get all types and their associated data matching the given type.
	 *
	 * @param type
	 *   the type to match against
	 * @return
	 *   list of matching entries. The list will be empty if no matches are
	 *   available.
	 */
	@NonNull
	ListIterable<MatchedType<T, D>> getAll(@NonNull Class<? extends T> type);

	/**
	 * Get all of the entries in this map without doing any matching.
	 *
	 * @return
	 *   immutable list of all entries registered
	 */
	RichIterable<MatchedType<T, D>> entries();

	/**
	 * Get an immutable version of this map.
	 *
	 * @return
	 */
	ClassMatchingMultimap<T, D> toImmutable();

	/**
	 * Get a mutable copy of this map.
	 *
	 * @return
	 */
	MutableClassMatchingMultimap<T, D> toMutable();
}
