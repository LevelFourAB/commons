package se.l4.commons.config;

import java.util.Collection;

import se.l4.commons.config.internal.ConfigBuilderImpl;

/**
 * Configuration as loaded from config files. Instances of this type can
 * be created via {@link #builder()}.
 *
 * <p>
 * Example usage:
 * <pre>
 * Config config = Config.builder()
 *	.addFile("/etc/app/normal.conf")
 *	.build();
 *
 * Value<Thumbnails> thumbs = config.get("thumbs", Thumbnails.class);
 * Value<Size> mediumSize = config.get("thumbs.medium", Size.class);
 * </pre>
 *
 * <h2>Configuration file format</h2>
 * <p>
 * The format is similar to JSON but is not as strict. For example it does not
 * require quotes around keys or string values and the initial braces can be
 * skipped.
 *
 * <p>
 * Example:
 * <pre>
 * thumbs: {
 *	medium: { width: 400, height: 400 }
 *	small: {
 *		width: 100
 *		height: 100
 *	}
 * }
 *
 * # Override the width
 * thumbs.small.width: 150
 * </pre>
 *
 * @author Andreas Holstenson
 *
 */
public interface Config
{
	/**
	 * Get a new {@link ConfigBuilder} to create a new configuration.
	 *
	 * @return
	 */
	static ConfigBuilder builder()
	{
		return new ConfigBuilderImpl();
	}

	/**
	 * Resolve values as the given path as an object. This is equivalent
	 * to call {@link #get(String, Class)} and then {@link Value#getOrDefault()}
	 * with the value {@code null}.
	 *
	 * @param path
	 * @param type
	 * @return
	 */
	<T> T asObject(String path, Class<T> type);

	/**
	 * Resolve configuration values as an object. The object will be created
	 * via serialization.
	 *
	 * @param path
	 * @param type
	 * @return
	 */
	<T> Value<T> get(String path, Class<T> type);

	/**
	 * Get the direct subkeys of the given path.
	 *
	 * @param path
	 * @return
	 */
	Collection<String> keys(String path);
}
