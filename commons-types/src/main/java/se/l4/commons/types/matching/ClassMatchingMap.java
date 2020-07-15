package se.l4.commons.types.matching;

import java.util.Optional;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.list.ListIterable;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Specialized map to associated classes and interfaces with data. This map
 * supports matching against the type hierarchy to fetch data.
 */
public interface ClassMatchingMap<T, D>
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
	Optional<D> get(@NonNull Class<? extends T> type);

	/**
	 * Get the best matching data for the given type.
	 *
	 * @param type
	 *   class to match against
	 * @return
	 *   optional with data or empty optional
	 */
	@NonNull
	Optional<D> getBest(@NonNull Class<? extends T> type);

	/**
	 * Get all types and their associated data matching the given type. This
	 * will return an iterable that ordered from the closest matching type to
	 * the furthest matching type.
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
	 *   immutable set of all entries registered
	 */
	RichIterable<MatchedType<T, D>> entries();

	/**
	 * Get an immutable copy of this map.
	 *
	 * @return
	 */
	ClassMatchingMap<T, D> toImmutable();

	/**
	 * Get a mutable copy of this map.
	 *
	 * @return
	 */
	MutableClassMatchingMap<T, D> toMutable();
}
