package se.l4.commons.serialization.spi;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Abstract implementation of {@link SerializerResolver}.
 * 
 * @author Andreas Holstenson
 *
 * @param <T>
 */
public abstract class AbstractSerializerResolver<T>
	implements SerializerResolver<T>
{
	@Override
	public Set<Class<? extends Annotation>> getHints()
	{
		return null;
	}
}
