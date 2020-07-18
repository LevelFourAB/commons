package se.l4.commons.types.matching;

import org.eclipse.collections.impl.multimap.set.UnifiedSetMultimap;

/**
 * {@link TypeMatchingMap} that uses a {@link UnifiedSetMultimap} for storage.
 *
 * @param <D>
 */
public class TypeMatchingUnifiedSetMap<D>
	extends AbstractMutableTypeMatchingMap<D>
{
	public TypeMatchingUnifiedSetMap()
	{
		super(UnifiedSetMultimap.newMultimap());
	}

	public TypeMatchingUnifiedSetMap(UnifiedSetMultimap<Class<?>, TypeRefHolder<D>> map)
	{
		super(map);
	}

	@Override
	public MutableTypeMatchingMap<D> toMutable()
	{
		return new TypeMatchingUnifiedSetMap<>(
			UnifiedSetMultimap.newMultimap(backingMap)
		);
	}
}
