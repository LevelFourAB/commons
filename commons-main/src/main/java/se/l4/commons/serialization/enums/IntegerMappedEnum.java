package se.l4.commons.serialization.enums;

/**
 * Interface used to mark {@link Enum}s that can be mapped to and from an int, used together with
 * {@link IntegerMappedTranslator}.
 *
 * @author Andreas Holstenson
 *
 */
public interface IntegerMappedEnum
{
	/**
	 * Get the value that is enum should mapped to.
	 *
	 * @return
	 */
	int getMappedValue();
}
