package se.l4.commons.types;

import java.lang.annotation.Annotation;
import java.util.Set;

import se.l4.commons.types.internal.TypeFinderOverScanResultBuilder;

/**
 * Interface to help discover and load types on the classpath.
 *
 * @author Andreas Holstenson
 *
 */
public interface TypeFinder
{
	/**
	 * Get classes that have been annotated with a certain annotation.
	 *
	 * @param annotationType
	 * @return
	 */
	Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotationType);

	/**
	 * Get classes that have the given annotation, automatically creating them.
	 *
	 * @param annotationType
	 * @return
	 */
	Set<? extends Object> getTypesAnnotatedWithAsInstances(Class<? extends Annotation> annotationType);

	/**
	 * Get sub types of the given class.
	 *
	 * @param type
	 * @return
	 */
	<T> Set<Class<? extends T>> getSubTypesOf(Class<T> type);

	/**
	 * Get sub types of the given class automatically creating them.
	 *
	 * @param type
	 * @return
	 */
	<T> Set<? extends T> getSubTypesAsInstances(Class<T> type);

	/**
	 * Return a builder to create an instance of {@link TypeFinder}.
	 *
	 * @return
	 *   builder that can be used to configure the finder
	 */
	static TypeFinderBuilder builder()
	{
		return new TypeFinderOverScanResultBuilder();
	}
}
