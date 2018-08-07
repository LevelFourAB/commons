package se.l4.commons.types;

/**
 * Exception thrown by {@link InstanceFactory} when an instance can not be
 * created.
 */
public class InstanceException
	extends RuntimeException
{
	private static final long serialVersionUID = 5490805858681960298L;

	public InstanceException()
	{
		super();
	}

	public InstanceException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InstanceException(String message)
	{
		super(message);
	}

	public InstanceException(Throwable cause)
	{
		super(cause);
	}
}
