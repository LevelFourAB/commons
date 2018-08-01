package se.l4.commons.types.matching;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of {@link ClassMatchingMap} using a {@link ConcurrentHashMap}.
 */
public class ClassMatchingConcurrentHashMap<T, D>
	extends AbstractClassMatchingMap<T, D>
{
	public ClassMatchingConcurrentHashMap()
	{
		super(new ConcurrentHashMap<>());
	}
}
