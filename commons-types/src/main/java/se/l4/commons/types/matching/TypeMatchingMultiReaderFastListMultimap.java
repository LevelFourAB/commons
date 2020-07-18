package se.l4.commons.types.matching;

import org.eclipse.collections.impl.multimap.list.MultiReaderFastListMultimap;

/**
 * Thread-safe version of {@link MutableTypeMatchingMultimap} using
 * {@link MultiReaderFastListMultimap}.
 *
 * @param <D>
 */
public class TypeMatchingMultiReaderFastListMultimap<D>
	extends AbstractMutableTypeMatchingMultimap<D>
{
	public TypeMatchingMultiReaderFastListMultimap()
	{
		this(MultiReaderFastListMultimap.newMultimap());
	}

	public TypeMatchingMultiReaderFastListMultimap(MultiReaderFastListMultimap<Class<?>, TypeRefHolder<D>> backingMap)
	{
		super(backingMap);
	}

	@Override
	public MutableTypeMatchingMultimap<D> toMutable()
	{
		return new TypeMatchingMultiReaderFastListMultimap<>(MultiReaderFastListMultimap.newMultimap(backingMap));
	}
}
