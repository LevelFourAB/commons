package se.l4.commons.io;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.function.Supplier;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * {@link Supplier} of a result that can throw a {@link IOException}.
 */
@FunctionalInterface
public interface IOSupplier<T>
{
	/**
	 * Gets a result.
	 *
	 * @return
	 *   a result
	 * @throws IOException
	 *   if I/O error occurs
	 */
	@Nullable
	T get()
		throws IOException;


	/**
	 * Turns this {@link IOSupplier} into a normal {@link Supplier}. The
	 * normal supplier will throw {@link UncheckedIOException} if an
	 * {@link IOException} is raised.
	 *
	 * @return
	 *   supplier
	 */
	@NonNull
	default Supplier<T> toSupplier()
	{
		return () -> {
			try
			{
				return get();
			}
			catch(IOException e)
			{
				throw new UncheckedIOException(e);
			}
		};
	}

	/**
	 * Adapt a regular {@link Supplier} into an {@link IOSupplier<T>}.
	 *
	 * @return
	 *   {@link IOSupplier<T>} that delegates to the given supplier
	 */
	@NonNull
	static <T> IOSupplier<T> adapt(@NonNull Supplier<T> supplier)
	{
		Objects.requireNonNull(supplier);
		return () -> supplier.get();
	}
}
