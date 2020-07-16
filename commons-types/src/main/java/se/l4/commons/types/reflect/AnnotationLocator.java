package se.l4.commons.types.reflect;

import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * Finder that helps locating an annotation, used to support meta annotations
 * and similar patterns.
 *
 * @param <T>
 */
public interface AnnotationLocator<T extends Annotation>
{
	/**
	 * Get if the given annotation matches.
	 *
	 * @param annotation
	 * @return
	 */
	Optional<T> get(Annotation annotation);

	/**
	 * Get a finder that will look for an annotation directly present on an
	 * element.
	 *
	 * @param <T>
	 * @param annotationClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static <T extends Annotation> AnnotationLocator<T> direct(Class<T> annotationClass)
	{
		return a -> a.annotationType() == annotationClass ? Optional.of((T) a) : Optional.empty();
	}
}
