package se.l4.commons.types.matching;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiPredicate;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.SetMultimap;

import se.l4.commons.types.internal.TypeHierarchy;

public class ClassMatchingConcurrentHashMultimap<T, D>
	implements ClassMatchingMultimap<T, D>
{
	private final SetMultimap<Class<? extends T>, D> backingMap;
	private final ReadWriteLock lock;

	public ClassMatchingConcurrentHashMultimap()
	{
		backingMap = HashMultimap.create();
		lock = new ReentrantReadWriteLock();
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<MatchedType<T, D>> entries()
	{
		lock.readLock().lock();
		try
		{
			return backingMap.entries()
				.stream()
				.map(e -> new DefaultMatchedType<T, D>((Class) e.getKey(), e.getValue()))
				.collect(ImmutableList.toImmutableList());
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	@Override
	public void put(Class<? extends T> type, D data)
	{
		Objects.requireNonNull(type);
		Objects.requireNonNull(data);

		lock.writeLock().lock();
		try
		{
			backingMap.put(type, data);
		}
		finally
		{
			lock.writeLock().unlock();;
		}
	}

	@Override
	public Set<D> get(Class<? extends T> type)
	{
		Objects.requireNonNull(type);

		lock.readLock().lock();
		try
		{
			return backingMap.get(type);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<D> getBest(Class<? extends T> type)
	{
		Objects.requireNonNull(type);

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
		Objects.requireNonNull(type);

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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void findMatching(Class<? extends T> type, BiPredicate<Class<? extends T>, Set<D>> predicate)
	{
		TypeHierarchy.visitHierarchy(type, t -> {
			lock.readLock().lock();
			try
			{
				if(backingMap.containsKey(t))
				{
					Set<D> data = backingMap.get((Class) t);
					return predicate.test((Class) t, data);
				}

				return true;
			}
			finally
			{
				lock.readLock().unlock();
			}
		});
	}
}
