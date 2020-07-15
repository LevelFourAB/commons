package se.l4.commons.types.matching;

import org.eclipse.collections.api.multimap.list.ImmutableListMultimap;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;

public class ImmutableClassMatchingMultimap<T, D>
	extends AbstractClassMatchingMultimap<T, D>
{
	public ImmutableClassMatchingMultimap(ImmutableListMultimap<Class<? extends T>, D> map)
	{
		super(map);
	}

	@Override
	public ClassMatchingMultimap<T, D> toImmutable()
	{
		return this;
	}

	@Override
	public MutableClassMatchingMultimap<T, D> toMutable()
	{
		return new ClassMatchingFastListMultimap<>(
			FastListMultimap.newMultimap(backingMap)
		);
	}
}
