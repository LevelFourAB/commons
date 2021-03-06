package se.l4.commons.types.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

import org.eclipse.collections.api.RichIterable;

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
	RichIterable<? extends Annotation> getAnnotations();

	/**
	 * Get if an annotation of the specific type is present.
	 */
	default boolean hasAnnotation(@NonNull Class<? extends Annotation> annotationClass)
	{
		for(Annotation a : getAnnotations())
		{
			if(a.annotationType() == annotationClass)
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
			if(a.annotationType() == annotationClass)
			{
				return Optional.of((T) a);
			}
		}

		return Optional.empty();
	}

	/**
	 * Get an annotation by using the given {@link AnnotationLocator} to locate
	 * it.
	 *
	 * @param <T>
	 * @param annotationClass
	 * @return
	 */
	@NonNull
	default <T extends Annotation> Optional<T> getAnnotation(@NonNull AnnotationLocator<T> locator)
	{
		for(Annotation a : getAnnotations())
		{
			Optional<T> r = locator.get(a);
			if(r.isPresent())
			{
				return r;
			}
		}

		return Optional.empty();
	}

	/**
	 * Find an annotation. Depending on the type of object that is annotated
	 * this will perform different things.
	 *
	 * <ul>
	 *   <li>
	 *     For a {@link TypeRef} it will look through interfaces and
	 *     superclasses until an instance of the annotation is found.
	 *   <li>
	 *     For a {@link FieldRef} it will only search through annotations
	 *     directly present.
	 *   <li>
	 *     For a {@link MethodRef} it will search through interfaces and
	 *     superclasses and if the method is present will return the first
	 *     annotation seen.
	 * </ul>
	 *
	 *
	 * @param <T>
	 * @param annotationClass
	 * @return
	 */
	default <T extends Annotation> Optional<T> findAnnotation(@NonNull Class<T> annotationClass)
	{
		return findAnnotation(AnnotationLocator.direct(annotationClass));
	}

	/**
	 * Find an annotation. Depending on the type of object that is annotated
	 * this will perform different things.
	 *
	 * <ul>
	 *   <li>
	 *     For a {@link TypeRef} it will look through interfaces and
	 *     superclasses until an instance of the annotation is found.
	 *   <li>
	 *     For a {@link FieldRef} it will only search through annotations
	 *     directly present.
	 *   <li>
	 *     For a {@link MethodRef} it will search through interfaces and
	 *     superclasses and if the method is present will return the first
	 *     annotation seen.
	 * </ul>
	 *
	 *
	 * @param <T>
	 * @param locator
	 * @return
	 */
	<T extends Annotation> Optional<T> findAnnotation(@NonNull AnnotationLocator<T> locator);
}
