package se.l4.commons.types.matching;

import java.util.List;
import java.util.Set;

import se.l4.commons.types.matching.ClassMatchingMap.Entry;

/**
 * Specialized map to associated classes and interfaces with data. This map
 * supports matching against the type hierarchy to fetch data. It also supports
 * associating multiple data instances with a certain type.
 */
public interface ClassMatchingMultimap<T, D>
{
	/**
	 * Associated some data with a type. This will allow matching against
	 * the type and its subclasses or in the case of interfaces its
	 * implementors.
	 *
	 * @param type
	 *   the type to associate data with
	 * @param data
	 *   the data to store
	 */
	void put(Class<? extends T> type, D data);

	/**
	 * Get data directly associated with the given type.
	 *
	 * @param type
	 *   the type get data for
	 * @return
	 *   optional containing data if there is direct match or empty optional
	 */
	Set<D> get(Class<? extends T> type);

	/**
	 * Get the best matching data for the given type.
	 *
	 * @param type
	 *   class to match against
	 * @return
	 *   optional with data or empty optional
	 */
	Set<D> getBest(Class<? extends T> type);

	/**
	 * Get all types and their associated data matching the given type.
	 *
	 * @param type
	 *   the type to match against
	 * @return
	 *   list of matching entries. The list will be empty if no matches are
	 *   available.
	 */
	List<Entry<T, D>> getAll(Class<? extends T> type);
}
