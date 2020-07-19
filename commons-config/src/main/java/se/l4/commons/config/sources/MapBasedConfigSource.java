package se.l4.commons.config.sources;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MapIterable;

/**
 * Abstract implementation of {@link ConfigSource that uses an {@link ImmutableMap}
 * to store all properties.
 *
 * <p>
 * This implementation assumes that properties are delimited with
 * {@link ConfigSource#PATH_DELIMITER}. It is possible to override
 * {@link #getKeys(String)} and {@link #getValue(String)} to provide custom
 * behavior for sources where this isn't true, such as for {@link EnvironmentConfigSource}.
 */
public class MapBasedConfigSource
	implements ConfigSource
{
	protected final MapIterable<String, Object> properties;

	public MapBasedConfigSource(MapIterable<String, Object> properties)
	{
		this.properties = properties;
	}

	@Override
	public MapIterable<String, Object> getProperties()
	{
		return properties;
	}

	@Override
	public RichIterable<String> getKeys(String path)
	{
		String prefix = path.isEmpty() ? path : path + ConfigKeys.PATH_DELIMITER;
		int length = prefix.length();
		return properties.keysView().asLazy()
			.selectWith(String::startsWith, prefix)
			.collect(key -> {
				int idx = key.indexOf(ConfigKeys.PATH_DELIMITER, length);
				return idx >= 0 ? key.substring(length, idx) : key.substring(length);
			})
			.distinct();
	}

	@Override
	public Object getValue(String path)
	{
		return properties.get(path);
	}
}
