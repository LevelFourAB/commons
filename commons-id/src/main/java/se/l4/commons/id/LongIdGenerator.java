package se.l4.commons.id;

/**
 * Generator for long based identifiers.
 *
 * @author Andreas Holstenson
 *
 */
public interface LongIdGenerator
{
	/**
	 * Get the next identifier.
	 *
	 * @return
	 */
	long next();
}
