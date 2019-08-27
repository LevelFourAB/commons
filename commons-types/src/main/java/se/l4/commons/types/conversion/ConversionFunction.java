package se.l4.commons.types.conversion;

/**
 * Conversion between one type to another.
 *
 * @param <I>
 * @param <O>
 */
@FunctionalInterface
public interface ConversionFunction<I, O>
{
	/**
	 * Convert the given object to the output type.
	 *
	 * @return
	 */
	O convert(I object);
}
