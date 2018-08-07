package se.l4.commons.types;

import java.lang.annotation.Annotation;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;
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
	@NonNull
	Set<Class<?>> getTypesAnnotatedWith(@NonNull Class<? extends Annotation> annotationType);

	/**
	 * Get classes that have the given annotation, automatically creating them.
	 *
	 * @param annotationType
	 * @return
	 */
	@NonNull
	Set<? extends Object> getTypesAnnotatedWithAsInstances(@NonNull Class<? extends Annotation> annotationType);

	/**
	 * Get sub types of the given class.
	 *
	 * @param type
	 * @return
	 */
	@NonNull
	<T> Set<Class<? extends T>> getSubTypesOf(@NonNull Class<T> type);

	/**
	 * Get sub types of the given class automatically creating them.
	 *
	 * @param type
	 * @return
	 */
	@NonNull
	<T> Set<? extends T> getSubTypesAsInstances(@NonNull Class<T> type);

	/**
	 * Return a builder to create an instance of {@link TypeFinder}.
	 *
	 * @return
	 *   builder that can be used to configure the finder
	 */
	@NonNull
	static TypeFinderBuilder builder()
	{
		return new TypeFinderOverScanResultBuilder();
	}
}
