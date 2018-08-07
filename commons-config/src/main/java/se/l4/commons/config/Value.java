package se.l4.commons.config;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Value within a configuration.
 *
 * @author Andreas Holstenson
 *
 * @param <T>
 */
public interface Value<T>
{
	/**
	 * Get the current value.
	 *
	 * @return
	 */
	@NonNull
	T get();

	/**
	 * Get the current value or return a default value if it is not set.
	 *
	 * @param defaultInstance
	 * @return
	 */
	@Nullable
	T getOrDefault(@Nullable T defaultInstance);

	/**
	 * Get if this value exists.
	 *
	 * @return
	 */
	boolean exists();
}
