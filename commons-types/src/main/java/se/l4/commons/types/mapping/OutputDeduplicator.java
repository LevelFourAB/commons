package se.l4.commons.types.mapping;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

/**
 * Cache to use for caching the mapped output of {@link TypeMapper}.
 */
public interface OutputDeduplicator<T>
{
	/**
	 * Cache the given instance.
	 *
	 * @param instance
	 * @return
	 */
	T deduplicate(T instance);

	/**
	 * Get a cache that does nothing.
	 *
	 * @param <T>
	 * @return
	 */
	static <T> OutputDeduplicator<T> none()
	{
		return i -> i;
	}

	/**
	 * Get a cache that will keep references to all objects and replace new
	 * ones with previous ones if they are still referenced by other objects.
	 *
	 * @param <T>
	 * @return
	 */
	static <T> OutputDeduplicator<T> weak()
	{
		LoadingCache<T, T> cache = Caffeine.newBuilder()
			.weakKeys()
			.build(i -> i);

		return cache::get;
	}
}
