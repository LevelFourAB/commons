package se.l4.commons.types.matching;

import org.eclipse.collections.impl.multimap.set.MultiReaderUnifiedSetMultimap;

/**
 * Thread-safe version of {@link MutableTypeMatchingMap} that uses
 * {@link MultiReaderUnifiedSetMultimap}.
 *
 * @param <D>
 */
public class TypeMatchingMultiReaderUnifiedSetMap<D>
	extends AbstractMutableTypeMatchingMap<D>
{
	public TypeMatchingMultiReaderUnifiedSetMap()
	{
		this(MultiReaderUnifiedSetMultimap.newMultimap());
	}

	public TypeMatchingMultiReaderUnifiedSetMap(MultiReaderUnifiedSetMultimap<Class<?>, TypeRefHolder<D>> map)
	{
		super(map);
	}

	@Override
	public MutableTypeMatchingMap<D> toMutable()
	{
		return new TypeMatchingMultiReaderUnifiedSetMap<>(
			MultiReaderUnifiedSetMultimap.newMultimap(backingMap)
		);
	}
}
