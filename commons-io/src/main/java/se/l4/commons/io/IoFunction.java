package se.l4.commons.io;

import java.io.IOException;

/**
 * Function that can throw {@link IOException}.
 * 
 * @author Andreas Holstenson
 *
 * @param <T>
 * @param <R>
 */
public interface IoFunction<T, R>
{
	R apply(T t)
		throws IOException;
}
