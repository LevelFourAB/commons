package se.l4.commons.types.reflect;

import java.lang.annotation.Annotation;
import java.util.Optional;

import se.l4.commons.types.internal.reflect.MetaAnnotationLocator;

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

	/**
	 * Get a finder that will look for meta annotations. A meta annotation is
	 * either directly present on a an element or present on another annotation
	 * that in turn is present on the element.
	 *
	 * @param <T>
	 * @param annotationClass
	 * @return
	 */
	static <T extends Annotation> AnnotationLocator<T> meta(Class<T> annotationClass)
	{
		return new MetaAnnotationLocator<>(annotationClass);
	}
}
