package se.l4.commons.types.matching;

import java.util.HashMap;

/**
 * Implementation of {@link ClassMatchingMap} using a {@link HashMap}.
 */
public class ClassMatchingHashMap<T, D>
	extends AbstractClassMatchingMap<T, D>
{
	public ClassMatchingHashMap()
	{
		super(new HashMap<>());
	}
}
