package se.l4.commons.io;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.function.Consumer;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

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
	void accept(@Nullable T item)
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
	@NonNull
	default IOConsumer<T> andThen(@NonNull IOConsumer<? super T> after)
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
	@NonNull
	default IOConsumer<T> andThen(@NonNull Consumer<? super T> after)
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
	@NonNull
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
	@NonNull
	static <T> IOConsumer<T> adapt(@NonNull Consumer<T> consumer)
	{
		Objects.requireNonNull(consumer);
		return item -> consumer.accept(item);
	}
}
