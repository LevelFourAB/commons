package se.l4.commons.types.matching;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Extension to {@link ClassMatchingMultimap} for maps that can be mutated after
 * construction.
 */
public interface MutableClassMatchingMultimap<T, D>
	extends ClassMatchingMultimap<T, D>
{
	/**
	 * Associate some data with a type. This will allow matching against
	 * the type and its subclasses or in the case of interfaces its
	 * implementors.
	 *
	 * @param type
	 *   the type to associate data with
	 * @param data
	 *   the data to store, never {@code null}
	 */
	void put(@NonNull Class<? extends T> type, @NonNull D data);
}
