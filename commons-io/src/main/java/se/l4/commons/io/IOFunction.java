package se.l4.commons.io;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.function.Function;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * {@link Function} that can throw {@link IOException}.
 *
 * @author Andreas Holstenson
 *
 * @param <T>
 * @param <R>
 */
@FunctionalInterface
public interface IOFunction<T, R>
{
	/**
	 * Apply this function to the given input.
	 *
	 * @param t
	 *   input to apply to
	 * @return
	 *   result
	 */
	@Nullable
	R apply(@Nullable T t)
		throws IOException;

	/**
	 * Compose the before function with this one. The before function will be
	 * applied first followed by this one.
	 *
	 * @param before
	 *   the function to apply before this one
	 * @return
	 *   composed function
	 */
	@NonNull
	default <V> IOFunction<V, R> compose(@NonNull IOFunction<? super V, ? extends T> before)
	{
		Objects.requireNonNull(before);
		return t -> apply(before.apply(t));
	}

	/**
	 * Compose the before function with this one. The before function will be
	 * applied first followed by this one.
	 *
	 * @param before
	 *   the function to apply before this one
	 * @return
	 *   composed function
	 */
	@NonNull
	default <V> IOFunction<V, R> compose(@NonNull Function<? super V, ? extends T> before)
	{
		Objects.requireNonNull(before);
		return t -> apply(before.apply(t));
	}

	/**
	 * Compose this function with another one. This function will be applied
	 * first followed by the after function.
	 *
	 * @param after
	 *   the function to apply after this function
	 * @return
	 *   composed function
	 */
	@NonNull
	default <V> IOFunction<T, V> andThen(@NonNull IOFunction<? super R, ? extends V> after)
	{
		Objects.requireNonNull(after);
		return t -> after.apply(apply(t));
	}

	/**
	 * Compose this function with another one. This function will be applied
	 * first followed by the after function.
	 *
	 * @param after
	 *   the function to apply after this function
	 * @return
	 *   composed function
	 */
	@NonNull
	default <V> IOFunction<T, V> andThen(@NonNull Function<? super R, ? extends V> after)
	{
		Objects.requireNonNull(after);
		return t -> after.apply(apply(t));
	}

	/**
	 * Turn this {@link IoFunction} into a regular {@link Function}. The
	 * regular function will throw {@link UncheckedIOException} if this
	 * function raises an {@link IOException}.
	 *
	 * @return
	 *   function that invokes this one
	 */
	@NonNull
	default Function<T, R> toFunction()
	{
		return t -> {
			try
			{
				return apply(t);
			}
			catch(IOException e)
			{
				throw new UncheckedIOException(e);
			}
		};
	}

	/**
	 * Adapt a {@link Function} into a {@link IOFunction}.
	 *
	 * @param func
	 *   the function to adapt
	 * @return
	 *   {@link IOFunction} that runs the given regular {@link Function}
	 */
	@NonNull
	static <T, R> IOFunction<T, R> adapt(@NonNull Function<T, R> func)
	{
		Objects.requireNonNull(func);
		return item -> func.apply(item);
	}
}
