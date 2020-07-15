package se.l4.commons.types.matching;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;

public class ClassMatchingConcurrentHashMultimap<T, D>
	extends AbstractMutableClassMatchingMultimap<T, D>
{
	private final ReadWriteLock lock;

	public ClassMatchingConcurrentHashMultimap()
	{
		this(FastListMultimap.newMultimap());
	}

	public ClassMatchingConcurrentHashMultimap(FastListMultimap<Class<? extends T>, D> map)
	{
		super(map);

		lock = new ReentrantReadWriteLock();
	}

	@Override
	public void put(Class<? extends T> type, D data)
	{
		lock.writeLock().lock();
		try
		{
			super.put(type, data);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	@Override
	public RichIterable<MatchedType<T, D>> entries()
	{
		lock.readLock().lock();
		try
		{
			return super.entries();
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	@Override
	public ListIterable<D> get(Class<? extends T> type)
	{
		lock.readLock().lock();
		try
		{
			return super.get(type);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	@Override
	public ListIterable<D> getBest(Class<? extends T> type)
	{
		lock.readLock().lock();
		try
		{
			return super.getBest(type);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	@Override
	public ListIterable<MatchedType<T, D>> getAll(Class<? extends T> type)
	{
		lock.readLock().lock();
		try
		{
			return super.getAll(type);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	@Override
	public MutableClassMatchingMultimap<T, D> toMutable()
	{
		return new ClassMatchingConcurrentHashMultimap<>(
			FastListMultimap.newMultimap(backingMap)
		);
	}
}
