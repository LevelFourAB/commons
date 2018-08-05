package se.l4.commons.types.matching;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;

import com.google.common.collect.SetMultimap;

/**
 * Abstract implementation of {@link ClassMatchingMap} that implements all of
 * the matching methods on top any {@link Map} implementation.
 */
public abstract class AbstractClassMatchingMultimap<T, D>
	implements ClassMatchingMultimap<T, D>
{
	private final SetMultimap<Class<? extends T>, D> backingMap;

	protected AbstractClassMatchingMultimap(SetMultimap<Class<? extends T>, D> backingMap)
	{
		this.backingMap = backingMap;
	}

	@Override
	public void put(Class<? extends T> type, D data)
	{
		backingMap.put(type, data);
	}

	@Override
	public Set<D> get(Class<? extends T> type)
	{
		return backingMap.get(type);
	}

	@Override
	public Set<D> getBest(Class<? extends T> type)
	{
		Set<D> result = new HashSet<>();
		findMatching(type, (t, d) -> {
			result.addAll(d);

			return false;
		});

		return result;
	}

	@Override
	public List<MatchedType<T, D>> getAll(Class<? extends T> type)
	{
		List<MatchedType<T, D>> result = new ArrayList<>();
		findMatching(type, (t, all) -> {
			for(D d : all)
			{
				result.add(new DefaultMatchedType<>(t, d));
			}

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
	protected void findMatching(Class<? extends T> type, BiPredicate<Class<? extends T>, Set<D>> predicate)
	{
		traverseType(type, predicate, new HashSet<>());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean traverseType(
		Class<?> type,
		BiPredicate<Class<? extends T>, Set<D>> predicate,
		Set<Class<?>> checked
	)
	{
		// Add this to checked set and abort if already traversed
		if(! checked.add(type)) return true;

		// First check if the type matches directly
		Set<D> data = backingMap.get((Class) type);
		if(! data.isEmpty() && ! predicate.test((Class) type, data))
		{
			// The predicate doesn't want more items
			return false;
		}

		// Check all of the interfaces directly declared by the class
		for(Class<?> c : type.getInterfaces())
		{
			if(! checked.add(c)) continue;

			data = backingMap.get((Class) c);
			if(! data.isEmpty() && ! predicate.test((Class) c, data))
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
}
