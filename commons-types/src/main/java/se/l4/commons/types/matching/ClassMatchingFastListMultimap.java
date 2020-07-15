package se.l4.commons.types.matching;

import org.eclipse.collections.impl.multimap.list.FastListMultimap;

public class ClassMatchingFastListMultimap<T, D>
	extends AbstractMutableClassMatchingMultimap<T, D>
{
	public ClassMatchingFastListMultimap()
	{
		super(FastListMultimap.newMultimap());
	}

	public ClassMatchingFastListMultimap(FastListMultimap<Class<? extends T>, D> map)
	{
		super(map);
	}

	@Override
	public MutableClassMatchingMultimap<T, D> toMutable()
	{
		return new ClassMatchingFastListMultimap<>(
			FastListMultimap.newMultimap(backingMap)
		);
	}
}
