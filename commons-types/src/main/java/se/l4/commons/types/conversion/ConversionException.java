package se.l4.commons.types.conversion;

public class ConversionException
	extends RuntimeException
{
	public ConversionException(String message)
	{
		super(message);
	}

	public ConversionException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
