package se.l4.commons.types.matching;

import org.eclipse.collections.impl.multimap.list.FastListMultimap;

/**
 * A {@link MutableTypeMatchingMap} that is implemented using a
 * {@link FastListMultimap}.
 */
public class TypeMatchingFastListMultimap<D>
	extends AbstractMutableTypeMatchingMultimap<D>
{
	public TypeMatchingFastListMultimap()
	{
		super(FastListMultimap.newMultimap());
	}

	public TypeMatchingFastListMultimap(FastListMultimap<Class<?>, TypeRefHolder<D>> backingMap)
	{
		super(backingMap);
	}

	@Override
	public MutableTypeMatchingMultimap<D> toMutable()
	{
		return new TypeMatchingFastListMultimap<>(FastListMultimap.newMultimap(backingMap));
	}
}
