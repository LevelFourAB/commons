package se.l4.commons.types.matching;

import java.util.HashMap;

/**
 * {@link TypeMatchingMap} that uses a {@link HashMap} for storage.
 *
 * @param <D>
 */
public class TypeMatchingHashMap<D>
	extends AbstractTypeMatchingMap<D>
{
	public TypeMatchingHashMap()
	{
		super(new HashMap<>(), HashMap::new);
	}
}
