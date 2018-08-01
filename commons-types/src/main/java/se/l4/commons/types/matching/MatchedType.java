package se.l4.commons.types.matching;

/**
 * Information about a type that has been matched.
 */
public interface MatchedType<T, D>
{
	/**
	 * Get the class that matched.
	 */
	Class<? extends T> getType();

	/**
	 * Get the data.
	 */
	D getData();
}
