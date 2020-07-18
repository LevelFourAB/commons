package se.l4.commons.types.matching;

import org.eclipse.collections.api.multimap.list.ImmutableListMultimap;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;

public class ImmutableTypeMatchingMultimap<D>
	extends AbstractTypeMatchingMultimap<D>
{
	protected ImmutableTypeMatchingMultimap(ImmutableListMultimap<Class<?>, TypeRefHolder<D>> backingMap)
	{
		super(backingMap);
	}

	@Override
	public TypeMatchingMultimap<D> toImmutable()
	{
		return this;
	}

	@Override
	public MutableTypeMatchingMultimap<D> toMutable()
	{
		return new TypeMatchingFastListMultimap<>(
			FastListMultimap.newMultimap(backingMap)
		);
	}
}
