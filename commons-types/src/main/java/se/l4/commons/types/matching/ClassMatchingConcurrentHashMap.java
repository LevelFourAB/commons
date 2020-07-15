package se.l4.commons.types.matching;

import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;

/**
 * Implementation of {@link MutableClassMatchingMap} using a
 * {@link ConcurrentHashMap}.
 */
public class ClassMatchingConcurrentHashMap<T, D>
	extends AbstractMutableClassMatchingMap<T, D>
{
	public ClassMatchingConcurrentHashMap()
	{
		super(ConcurrentHashMap.newMap());
	}

	protected ClassMatchingConcurrentHashMap(ConcurrentHashMap<Class<? extends T>, D> map)
	{
		super(map);
	}

	@Override
	public ClassMatchingConcurrentHashMap<T, D> toMutable()
	{
		return new ClassMatchingConcurrentHashMap<T, D>(
			(ConcurrentHashMap<Class<? extends T>, D>) backingMap.clone()
		);
	}
}
