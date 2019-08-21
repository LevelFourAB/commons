package se.l4.commons.types.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents something that can be annotated using {@link Annotation}s. This
 * is a null-safe version of {@link AnnotatedElement} with some additional
 * useful methods for finding annotations.
 */
public interface Annotated
{
	/**
	 * Get annotations that are present.
	 *
	 * @see AnnotatedElement#getAnnotations()
	 */
	@NonNull
	Annotation[] getAnnotations();

	/**
	 * Get if an annotation of the specific type is present.
	 */
	default boolean hasAnnotation(@NonNull Class<?> annotationClass)
	{
		for(Annotation a : getAnnotations())
		{
			if(annotationClass.isAssignableFrom(a.getClass()))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Get the annotation of the specified type if present.
	 *
	 * @param <T>
	 * @param annotationClass
	 * @return
	 */
	@NonNull
	@SuppressWarnings("unchecked")
	default <T extends Annotation> Optional<T> getAnnotation(@NonNull Class<T> annotationClass)
	{
		for(Annotation a : getAnnotations())
		{
			if(annotationClass.isAssignableFrom(a.getClass()))
			{
				return Optional.of((T) a);
			}
		}

		return Optional.empty();
	}
}
