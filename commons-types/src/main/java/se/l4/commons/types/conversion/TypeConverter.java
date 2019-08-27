package se.l4.commons.types.conversion;

import java.util.Optional;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Type converter for converting between one type to another.
 */
public interface TypeConverter
{
	/**
	 * Add a conversion, resolving the types based on how the interface is
	 * implemented.
	 *
	 * @param function
	 */
	void addConversion(@NonNull ConversionFunction<?, ?> function);

	/**
	 * Add a conversion between the given input and output.
	 *
	 * @param <I>
	 * @param <O>
	 * @param in
	 * @param out
	 * @param function
	 */
	<I, O> void addConversion(
		@NonNull Class<I> in,
		@NonNull Class<O> out,
		@NonNull ConversionFunction<I, O> function
	);

	/**
	 * Add a conversion.
	 *
	 * @param conversion
	 */
	void addConversion(@NonNull Conversion<?, ?> conversion);

	/**
	 * Convert the given input to another type.
	 *
	 * @param <T>
	 * 		type of output
	 * @param in
	 * 		value to convert (input)
	 * @param output
	 * 		output type
	 * @return
	 * 		converted value
	 * @throws ConversionException
	 * 		if unable to convert
	 */
	<T> T convert(Object in, Class<T> output);

	/**
	 * Check if a conversion is supported.
	 *
	 * @param in
	 * @param out
	 * @return
	 */
	boolean canConvertBetween(@NonNull Class<?> in, @NonNull Class<?> out);

	/**
	 * Check if a conversion is supported.
	 *
	 * @param in
	 * @param out
	 * @return
	 */
	boolean canConvertBetween(Object in, @NonNull Class<?> out);

	/**
	 * Find a conversion between the given input type and output type.
	 *
	 * @param <I>
	 * @param <O>
	 * @param input
	 * @param output
	 * @return
	 */
	@NonNull
	<I, O> Optional<Conversion<I, O>> getConversion(
		@NonNull Class<I> input,
		@NonNull Class<O> output
	);

	/**
	 * Find a conversion between the given object and the output type.
	 *
	 * @param <I>
	 * @param <O>
	 * @param input
	 * @param output
	 * @return
	 */
	@NonNull
	<I, O> Optional<Conversion<I, O>> getConversion(
		I input,
		@NonNull Class<O> output
	);

	/**
	 * Get a conversion between the given input and output if possible, or
	 * return a dynamic converter that converts to the output.
	 *
	 * @param in
	 * @param out
	 * @return
	 */
	<I, O> Conversion<I, O> getDynamicConversion(Class<I> in, Class<O> out);

	/**
	 * Create a conversion that converts any object to the specific type.
	 *
	 * @param out
	 * @return
	 */
	<T> Conversion<? extends Object, T> createDynamicConversionTo(Class<T> out);
}
