package se.l4.commons.types;

import java.lang.reflect.InvocationTargetException;

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
			return type.getDeclaredConstructor().newInstance();
		}
		catch(InstantiationException e)
		{
			throw new InstanceException("Unable to create; " + e.getCause().getMessage(), e.getCause());
		}
		catch(IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
		{
			throw new InstanceException("Unable to create; " + e.getMessage(), e);
		}
	}

}
