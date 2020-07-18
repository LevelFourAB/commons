package se.l4.commons.types.matching;

import org.eclipse.collections.api.multimap.set.ImmutableSetMultimap;
import org.eclipse.collections.impl.multimap.set.UnifiedSetMultimap;

/**
 * Immutable version of {@link TypeMatchingMap}.
 *
 * @param <D>
 */
public class ImmutableTypeMatchingMap<D>
	extends AbstractTypeMatchingMap<D>
{
	protected ImmutableTypeMatchingMap(ImmutableSetMultimap<Class<?>, TypeRefHolder<D>> backingMap)
	{
		super(backingMap);
	}

	@Override
	public TypeMatchingMap<D> toImmutable()
	{
		return this;
	}

	@Override
	public MutableTypeMatchingMap<D> toMutable()
	{
		return new TypeMatchingUnifiedSetMap<>(
			UnifiedSetMultimap.newMultimap(backingMap)
		);
	}
}
