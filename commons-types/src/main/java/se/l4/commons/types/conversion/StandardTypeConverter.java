package se.l4.commons.types.conversion;

import se.l4.commons.types.matching.ClassMatchingHashMultimap;

/**
 * Standard version of {@link TypeConverter}. Supports registering and finding
 * conversions.
 */
public class StandardTypeConverter
	extends AbstractTypeConverter
{

	public StandardTypeConverter()
	{
		super(new ClassMatchingHashMultimap<>());

		DefaultConversions.register(this::addConversion);
	}
}
