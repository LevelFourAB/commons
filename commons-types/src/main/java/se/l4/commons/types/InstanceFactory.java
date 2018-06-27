package se.l4.commons.types;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.function.Supplier;

/**
 * Factory for instances, used to support dependency injection if available.
 *
 * @author Andreas Holstenson
 *
 */
public interface InstanceFactory
{
	/**
	 * Create the specified type.
	 *
	 * @param <T>
	 * @param type
	 * @return
	 */
	<T> T create(Class<T> type);

	/**
	 * Create the specified generic type.
	 */
	@SuppressWarnings("unchecked")
	default <T> T create(Type type)
	{
		if(! (type instanceof Class))
		{
			throw new InstanceException("Can not create " + type + ", generics are unsupported by this factory");
		}

		return create((Class<T>) type);
	}

	/**
	 * Create the specified type using the given annotation as hints.
	 *
	 * @param type
	 * @param annotations
	 * @return
	 */
	default <T> T create(Type type, Annotation[] annotations)
	{
		// Default implementation ignores the annotations
		return create(type);
	}

	/**
	 * Create a supplier for instances of the given type.
	 *
	 * @param type
	 *   class that the supplier should create
	 * @return
	 *   supplier that when invoked will create the given type
	 */
	default <T> Supplier<T> supplier(Class<T> type)
	{
		return () -> create(type);
	}

	/**
	 * Create a supplier for instances of the given type.
	 *
	 * @param type
	 *   the type that the supplier should create
	 * @return
	 *   supplier that when invoked will create the given type
	 */
	default <T> Supplier<T> supplier(Type type)
	{
		return () -> create(type);
	}

	/**
	 * Create a supplier for instances of the given type using the
	 * annotations as hints.
	 *
	 * @param type
	 *   the type that the supplier should create
	 * @param annotations
	 *   the annotations to use as hints
	 * @return
	 *   supplier that will create the type based on the type and annotations
	 */
	default <T> Supplier<T> supplier(Type type, Annotation[] annotations)
	{
		return () -> create(type, annotations);
	}
}
