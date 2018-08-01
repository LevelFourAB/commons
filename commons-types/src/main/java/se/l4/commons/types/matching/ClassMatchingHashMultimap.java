package se.l4.commons.types.matching;

import com.google.common.collect.HashMultimap;

public class ClassMatchingHashMultimap<T, D>
	extends AbstractClassMatchingMultimap<T, D>
{

	public ClassMatchingHashMultimap()
	{
		super(HashMultimap.create());
	}

}
