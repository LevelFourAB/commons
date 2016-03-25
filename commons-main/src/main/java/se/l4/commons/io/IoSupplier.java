package se.l4.commons.io;

import java.io.IOException;

public interface IoSupplier<T>
{
	T get()
		throws IOException;
}
