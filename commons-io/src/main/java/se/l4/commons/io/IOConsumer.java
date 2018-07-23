package se.l4.commons.io;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Version of {@link Consumer} that throws an {@link IOException}.
 */
@FunctionalInterface
public interface IOConsumer<T>
{
	/**
	 * Perform this operation on the given argument.
	 *
	 * @param item
	 *   the input argument
	 * @throws IOException
	 *   on I/O failure
	 */
	void accept(T item)
		throws IOException;

	/**
	 * Compose this consumer with another one, the composed consumer will run
	 * the consumers in sequence.
	 *
	 * @param after
	 *   the operation to perform after this operation
	 * @return
	 *   composed consumer
	 */
	default IOConsumer<T> andThen(IOConsumer<? super T> after)
	{
		Objects.requireNonNull(after);
		return item -> { accept(item); after.accept(item); };
	}

	/**
	 * Compose this consumer with another one, the composed consumer will run
	 * the consumers in sequence.
	 *
	 * @param after
	 *   the operation to perform after this operation
	 * @return
	 *   composed consumer
	 */
	default IOConsumer<T> andThen(Consumer<? super T> after)
	{
		Objects.requireNonNull(after);
		return item -> { accept(item); after.accept(item); };
	}

	/**
	 * Adapt this {@link IOConsumer} into a {@link Consumer} that will throw
	 * {@link UncheckedIOException}.
	 *
	 * @return
	 *   instance of {@link Consumer}
	 */
	default Consumer<T> toConsumer()
	{
		return item -> {
			try
			{
				accept(item);
			}
			catch(IOException e)
			{
				throw new UncheckedIOException("IOException while running; " + e.getMessage(), e);
			}
		};
	}

	/**
	 * Adapt a {@link Consumer} into a {@link IOConsumer}.
	 *
	 * @param consumer
	 *   the consumer to adapt
	 * @return
	 *   {@link IOConsumer} that runs the given regular {@link Consumer}
	 */
	static <T> IOConsumer<T> adapt(Consumer<T> consumer)
	{
		return item -> consumer.accept(item);
	}
}
