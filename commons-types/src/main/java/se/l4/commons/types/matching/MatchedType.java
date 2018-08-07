package se.l4.commons.types.matching;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Information about a type that has been matched.
 */
public interface MatchedType<T, D>
{
	/**
	 * Get the class that matched.
	 */
	@NonNull
	Class<? extends T> getType();

	/**
	 * Get the data.
	 */
	@NonNull
	D getData();
}
