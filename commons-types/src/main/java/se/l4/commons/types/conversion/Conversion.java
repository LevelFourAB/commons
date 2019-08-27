package se.l4.commons.types.conversion;

/**
 * {@link ConversionFunction} including metadata about the types that can be
 * converted.
 *
 * @param <I>
 * @param <O>
 */
public interface Conversion<I, O>
	extends ConversionFunction<I, O>
{
	/**
	 * Get type of input supported.
	 */
	Class<I> getInput();

	/**
	 * Get the type of output supported.
	 *
	 * @return
	 */
	Class<O> getOutput();
}
