package se.l4.commons.types.proxies;

/**
 * Exception thrown for issues with proxy creation.
 */
public class ProxyException
	extends RuntimeException
{

	public ProxyException()
	{
		super();
	}

	public ProxyException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ProxyException(String message)
	{
		super(message);
	}

	public ProxyException(Throwable cause)
	{
		super(cause);
	}

}
