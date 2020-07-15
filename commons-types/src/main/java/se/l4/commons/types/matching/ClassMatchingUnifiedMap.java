package se.l4.commons.types.matching;

import org.eclipse.collections.impl.map.mutable.UnifiedMap;

/**
 * Implementation of {@link ClassMatchingMap} using a {@link UnifiedMap}.
 */
public class ClassMatchingUnifiedMap<T, D>
	extends AbstractMutableClassMatchingMap<T, D>
{
	public ClassMatchingUnifiedMap()
	{
		super(UnifiedMap.newMap());
	}

	public ClassMatchingUnifiedMap(UnifiedMap<Class<? extends T>, D> map)
	{
		super(map);
	}

	@Override
	public ClassMatchingUnifiedMap<T, D> toMutable()
	{
		return new ClassMatchingUnifiedMap<T, D>(
			(UnifiedMap<Class<? extends T>, D>) backingMap.clone()
		);
	}
}
