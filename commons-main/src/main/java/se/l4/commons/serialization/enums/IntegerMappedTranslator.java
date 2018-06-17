package se.l4.commons.serialization.enums;

import se.l4.commons.serialization.SerializationException;
import se.l4.commons.serialization.format.ValueType;

/**
 * Translator for use with {@link IntegerMappedEnum}.
 *
 * @author Andreas Holstenson
 *
 */
public class IntegerMappedTranslator
	implements ValueTranslator<Integer>
{
	private final Enum<?>[] values;

	public IntegerMappedTranslator(Class<? extends Enum<?>> type)
	{
		if(! IntegerMappedEnum.class.isAssignableFrom(type))
		{
			throw new SerializationException("Enum " + type + " does not implement the " + IntegerMappedEnum.class.getSimpleName() + " interface");
		}

		values = type.getEnumConstants();
	}

	@Override
	public ValueType getType()
	{
		return ValueType.INTEGER;
	}

	@Override
	public Integer fromEnum(Enum<?> value)
	{
		return ((IntegerMappedEnum) value).getMappedValue();
	}

	@Override
	public Enum<?> toEnum(Integer value)
	{
		int v = value;
		for(Enum<?> e : values)
		{
			if(((IntegerMappedEnum) e).getMappedValue() == v)
			{
				return e;
			}
		}
		return null;
	}

}
