package se.l4.commons.serialization.format;

/**
 * Tokens that the input can return.
 *
 * @author Andreas Holstenson
 *
 */
public enum Token
{
	/**
	 * Start of a list.
	 */
	LIST_START,
	/**
	 * End of a list.
	 */
	LIST_END,
	/**
	 * Start of an object.
	 */
	OBJECT_START,
	/**
	 * End of an object.
	 */
	OBJECT_END,
	/**
	 * Key, value is available via {@link StreamingInput#getString()}.
	 */
	KEY,
	/**
	 * Value, available via getters (and {@link StreamingInput#getValue()}).
	 */
	VALUE,
	/**
	 * Null, special case of {@link #VALUE}.
	 */
	NULL,
	/**
	 * Special token returned when end of stream has been reached.
	 */
	END_OF_STREAM
}
