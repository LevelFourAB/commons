package se.l4.commons.types.matching;

import org.eclipse.collections.impl.multimap.list.MultiReaderFastListMultimap;

/**
 * Thread-safe version of {@link MutableClassMatchingMultimap} that uses
 * a {@link MultiReaderFastListMultimap}. This type of map allows for multiple
 * reads but will block when a write needs to occur.
 *
 * @param <T>
 * @param <D>
 */
public class ClassMatchingMultiReaderFastListMultimap<T, D>
	extends AbstractMutableClassMatchingMultimap<T, D>
{
	public ClassMatchingMultiReaderFastListMultimap()
	{
		this(MultiReaderFastListMultimap.newMultimap());
	}

	public ClassMatchingMultiReaderFastListMultimap(MultiReaderFastListMultimap<Class<? extends T>, D> map)
	{
		super(map);
	}

	@Override
	public MutableClassMatchingMultimap<T, D> toMutable()
	{
		return new ClassMatchingMultiReaderFastListMultimap<>(
			MultiReaderFastListMultimap.newMultimap(backingMap)
		);
	}
}
