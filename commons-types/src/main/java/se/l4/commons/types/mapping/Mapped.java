package se.l4.commons.types.mapping;

import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.collections.api.list.ListIterable;

public interface Mapped<T>
{
	/**
	 * Get if there is a result.
	 *
	 * @return
	 */
	boolean isPresent();

	/**
	 * Get the result.
	 *
	 * @return
	 */
	T get();

	/**
	 * Get this mapped result as an optional. If any errors occurred and no
	 * result is present a {@link RuntimeException} will be thrown.
	 *
	 * @return
	 */
	Optional<T> asOptional();

	/**
	 * Get this mapped result as an optional. If any errors occurred and no
	 * result is present the given supplier will be asked to raise an exception.
	 *
	 * @param errorSupplier
	 * @return
	 */
	<E extends Exception> Optional<T> asOptional(Supplier<E> errorSupplier)
		throws E;

	/**
	 * Get if there are errors present.
	 *
	 * @return
	 */
	boolean hasErrors();

	/**
	 * Get the errors that occurred during mapping.
	 *
	 * @return
	 */
	ListIterable<Exception> getErrors();
}
