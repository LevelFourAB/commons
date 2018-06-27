package se.l4.commons.io;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;

public interface IoConsumer<T>
{
	void accept(T item)
		throws IOException;

	static <T> Consumer<T> adapt(IoConsumer<T> consumer)
	{
		return item -> {
			try
			{
				consumer.accept(item);
			}
			catch(IOException e)
			{
				throw new UncheckedIOException("IOException while running; " + e.getMessage(), e);
			}
		};
	}
}
