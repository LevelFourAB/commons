package se.l4.commons.types.matching;

import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

/**
 * Immutable implementation of {@link ClassMatchingMap}.
 *
 * @param <T>
 * @param <D>
 */
public class ImmutableClassMatchingMap<T, D>
	extends AbstractClassMatchingMap<T, D>
{
	public ImmutableClassMatchingMap(ImmutableMap<Class<? extends T>, D> map)
	{
		super(map);
	}

	@Override
	public ClassMatchingMap<T, D> toImmutable()
	{
		return this;
	}

	@Override
	public MutableClassMatchingMap<T, D> toMutable()
	{
		return new ClassMatchingUnifiedMap<T, D>(UnifiedMap.newMapWith(backingMap.keyValuesView()));
	}
}
