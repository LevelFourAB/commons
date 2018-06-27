package se.l4.commons.types;

/**
 * Default implementation of {@link InstanceFactory}.
 *
 * @author Andreas Holstenson
 *
 */
public class DefaultInstanceFactory
	implements InstanceFactory
{

	@Override
	public <T> T create(Class<T> type)
	{
		try
		{
			return type.newInstance();
		}
		catch(InstantiationException e)
		{
			throw new InstanceException("Unable to create; " + e.getCause().getMessage(), e.getCause());
		}
		catch(IllegalAccessException e)
		{
			throw new InstanceException("Unable to create; " + e.getMessage(), e);
		}
	}

}
