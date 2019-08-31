package se.l4.commons.types.conversion;

import java.util.function.Consumer;

public class DefaultConversions
{
	public static final ConversionFunction<Number, String> NUMBER_TO_STRING =
		new ConversionFunction<Number, String>()
		{
			@Override
			public String convert(Number object)
			{
				return object.toString();
			}
		};

	public static final ConversionFunction<Number, Byte> NUMBER_TO_BYTE =
		new ConversionFunction<Number, Byte>()
		{
			@Override
			public Byte convert(Number object)
			{
				return object.byteValue();
			}
		};

	public static final ConversionFunction<Number, Short> NUMBER_TO_SHORT =
		new ConversionFunction<Number, Short>()
		{
			@Override
			public Short convert(Number object)
			{
				return object.shortValue();
			}
		};

	public static final ConversionFunction<Number, Integer> NUMBER_TO_INTEGER =
		new ConversionFunction<Number, Integer>()
		{
			@Override
			public Integer convert(Number object)
			{
				return object.intValue();
			}
		};

	public static final ConversionFunction<Number, Long> NUMBER_TO_LONG =
		new ConversionFunction<Number, Long>()
		{
			@Override
			public Long convert(Number object)
			{
				return object.longValue();
			}
		};

	public static final ConversionFunction<Number, Float> NUMBER_TO_FLOAT =
		new ConversionFunction<Number, Float>()
		{
			@Override
			public Float convert(Number object)
			{
				return object.floatValue();
			}
		};

	public static final ConversionFunction<Number, Double> NUMBER_TO_DOUBLE =
		new ConversionFunction<Number, Double>()
		{
			@Override
			public Double convert(Number object)
			{
				return object.doubleValue();
			}
		};

	public static final ConversionFunction<String, Integer> STRING_TO_INTEGER =
		new ConversionFunction<String, Integer>()
		{
			@Override
			public Integer convert(String object)
			{
				try
				{
					return Integer.parseInt(object);
				}
				catch(NumberFormatException e)
				{
					throw new ConversionException("Invalid number", e);
				}
			}
		};

	public static final ConversionFunction<String, Long> STRING_TO_LONG =
		new ConversionFunction<String, Long>()
		{
			@Override
			public Long convert(String object)
			{
				try
				{
					return Long.parseLong(object);
				}
				catch(NumberFormatException e)
				{
					throw new ConversionException("Invalid number", e);
				}
			}
		};

	public static final ConversionFunction<String, Double> STRING_TO_DOUBLE =
		new ConversionFunction<String, Double>()
		{
			@Override
			public Double convert(String object)
			{
				try
				{
					return Double.parseDouble(object);
				}
				catch(NumberFormatException e)
				{
					throw new ConversionException("Invalid number", e);
				}
			}
		};

	public static final ConversionFunction<String, Float> STRING_TO_FLOAT =
		new ConversionFunction<String, Float>()
		{
			@Override
			public Float convert(String object)
			{
				try
				{
					return Float.parseFloat(object);
				}
				catch(NumberFormatException e)
				{
					throw new ConversionException("Invalid number", e);
				}
			}
		};

	public static final ConversionFunction<String, Short> STRING_TO_SHORT =
		new ConversionFunction<String, Short>()
		{
			@Override
			public Short convert(String object)
			{
				try
				{
					return Short.parseShort(object);
				}
				catch(NumberFormatException e)
				{
					throw new ConversionException("Invalid number", e);
				}
			}
		};

	public static final ConversionFunction<String, Byte> STRING_TO_BYTE =
		new ConversionFunction<String, Byte>()
		{
			@Override
			public Byte convert(String object)
			{
				try
				{
					return Byte.parseByte(object);
				}
				catch(NumberFormatException e)
				{
					throw new ConversionException("Invalid number", e);
				}
			}
		};

	private DefaultConversions()
	{
	}

	public static void register(Consumer<ConversionFunction<?, ?>> consumer)
	{
		consumer.accept(NUMBER_TO_STRING);
		consumer.accept(NUMBER_TO_BYTE);
		consumer.accept(NUMBER_TO_SHORT);
		consumer.accept(NUMBER_TO_INTEGER);
		consumer.accept(NUMBER_TO_LONG);
		consumer.accept(NUMBER_TO_FLOAT);
		consumer.accept(NUMBER_TO_DOUBLE);

		consumer.accept(STRING_TO_BYTE);
		consumer.accept(STRING_TO_SHORT);
		consumer.accept(STRING_TO_INTEGER);
		consumer.accept(STRING_TO_LONG);
		consumer.accept(STRING_TO_FLOAT);
		consumer.accept(STRING_TO_DOUBLE);
	}
}
