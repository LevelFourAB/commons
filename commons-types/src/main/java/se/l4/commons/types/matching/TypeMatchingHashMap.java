package se.l4.commons.types.matching;

import java.util.HashMap;

import org.eclipse.collections.impl.multimap.set.UnifiedSetMultimap;

/**
 * {@link TypeMatchingMap} that uses a {@link HashMap} for storage.
 *
 * @param <D>
 */
public class TypeMatchingHashMap<D>
	extends AbstractMutableTypeMatchingMap<D>
{
	public TypeMatchingHashMap()
	{
		super(UnifiedSetMultimap.newMultimap());
	}
}
