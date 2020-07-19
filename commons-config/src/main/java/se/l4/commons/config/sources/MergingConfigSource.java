package se.l4.commons.config.sources;

import java.util.stream.StreamSupport;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;

/**
 * Implementation of {@link ConfigSource} that merges configuration in order
 * of priority.
 */
public class MergingConfigSource
	implements ConfigSource
{
	private final ConfigSource[] sources;

	public MergingConfigSource(Iterable<ConfigSource> sources)
	{
		this.sources = StreamSupport.stream(sources.spliterator(), false)
			.toArray(ConfigSource[]::new);
	}

	@Override
	public ImmutableMap<String, Object> getProperties()
	{
		MutableMap<String, Object> result = Maps.mutable.empty();
		for(int i=sources.length-1; i>=0; i--)
		{
			result.withAllKeyValues(sources[i].getProperties().keyValuesView());
		}
		return result.toImmutable();
	}

	@Override
	public RichIterable<String> getKeys(String path)
	{
		MutableSet<String> keys = Sets.mutable.empty();
		for(ConfigSource source : sources)
		{
			keys.withAll(source.getKeys(path));
		}
		return keys;
	}

	@Override
	public Object getValue(String path)
	{
		for(ConfigSource source : sources)
		{
			Object value = source.getValue(path);
			if(value != null)
			{
				return value;
			}
		}

		return null;
	}
}
