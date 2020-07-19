package se.l4.commons.config.sources;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.map.MapIterable;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Source that provides configuration values, used to abstract away reading
 * and merging of several property sources.
 */
public interface ConfigSource
{
	/**
	 * Get the properties that this source has available.
	 *
	 * @return
	 */
	MapIterable<String, Object> getProperties();

	/**
	 * Get the keys that are available directly under the given path.
	 *
	 * @param path
	 * @return
	 */
	RichIterable<String> getKeys(@NonNull String path);

	/**
	 * Attempt to get a value from this source. This is expected to only return
	 * wrapped primitives, {@link String} or {@code null} if the value is not
	 * available.
	 *
	 * @param path
	 * @return
	 */
	Object getValue(String path);
}
