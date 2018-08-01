package se.l4.commons.types.matching;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;

/**
 * Abstract implementation of {@link ClassMatchingMap} that implements all of
 * the matching methods on top any {@link Map} implementation.
 */
public abstract class AbstractClassMatchingMap<T, D>
	implements ClassMatchingMap<T, D>
{
	private final Map<Class<? extends T>, D> backingMap;

	protected AbstractClassMatchingMap(Map<Class<? extends T>, D> backingMap)
	{
		this.backingMap = backingMap;
	}

	@Override
	public void put(Class<? extends T> type, D data)
	{
		backingMap.put(type, data);
	}

	@Override
	public Optional<D> get(Class<? extends T> type)
	{
		return Optional.ofNullable(backingMap.get(type));
	}

	@Override
	@SuppressWarnings("unchecked")
	public Optional<D> getBest(Class<? extends T> type)
	{
		MutableHolder holder = new MutableHolder();
		findMatching(type, (t, d) -> {
			holder.data = d;

			return false;
		});

		return Optional.ofNullable((D) holder.data);
	}

	@Override
	public List<Entry<T, D>> getAll(Class<? extends T> type)
	{
		List<Entry<T, D>> result = new ArrayList<>();
		findMatching(type, (t, d) -> {
			result.add(new EntryImpl<>(t, d));

			// Always continue
			return true;
		});

		return result;
	}

	/**
	 * Perform matching against the given type. This will go through the
	 * hierarchy and interfaces of the type trying to find if this map has
	 * an entry for them.
	 */
	protected void findMatching(Class<? extends T> type, BiPredicate<Class<? extends T>, D> predicate)
	{
		traverseType(type, predicate, new HashSet<>());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean traverseType(
		Class<?> type,
		BiPredicate<Class<? extends T>, D> predicate,
		Set<Class<?>> checked
	)
	{
		// Add this to checked set and abort if already traversed
		if(! checked.add(type)) return true;

		// First check if the type matches directly
		D data = backingMap.get(type);
		if(data != null && ! predicate.test((Class) type, data))
		{
			// The predicate doesn't want more items
			return false;
		}

		// Check all of the interfaces directly declared by the class
		for(Class<?> c : type.getInterfaces())
		{
			if(! checked.add(c)) continue;

			data = backingMap.get(c);
			if(data != null && ! predicate.test((Class) c, data))
			{
				// The predicate doesn't want more items
				return false;
			}
		}

		// Check the hierarchy of the interfaces
		for(Class<?> c : type.getInterfaces())
		{
			if(! traverseType(c, predicate, checked))
			{
				// The predicate doesn't want more items
				return false;
			}
		}

		// Look at the super class
		if(type.getSuperclass() == null)
		{
			// No superclass, so continue the search
			return true;
		}

		// Traverse up to the superclass
		return traverseType(type.getSuperclass(), predicate, checked);
	}

	private static class MutableHolder
	{
		private Object data;
	}

	private static class EntryImpl<T, D>
		implements Entry<T, D>
	{
		private final Class<? extends T> type;
		private final D data;

		public EntryImpl(Class<? extends T> type, D data)
		{
			this.type = type;
			this.data = data;
		}

		@Override
		public Class<? extends T> getType()
		{
			return type;
		}

		@Override
		public D getData()
		{
			return data;
		}

		@Override
		public String toString()
		{
			return "Entry{" + type + " => " + data + "}";
		}
	}
}