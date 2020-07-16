package se.l4.commons.types.internal.reflect;

import java.lang.annotation.Annotation;
import java.util.Optional;

import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import se.l4.commons.types.reflect.AnnotationLocator;

/**
 * Finder that will look for an annotation directly and also on other
 * annotations.
 *
 * @param <T>
 */
public class MetaAnnotationLocator<T extends Annotation>
	implements AnnotationLocator<T>
{
	private final Class<T> annotationClass;

	public MetaAnnotationLocator(Class<T> annotationClass)
	{
		this.annotationClass = annotationClass;
	}

	@Override
	public Optional<T> get(Annotation annotation)
	{
		MutableSet<Class<?>> handled = UnifiedSet.newSet();
		return find(annotation, handled);
	}

	@SuppressWarnings("unchecked")
	private Optional<T> find(Annotation annotation, MutableSet<Class<?>> handled)
	{
		if(annotation.annotationType() == annotationClass)
		{
			return Optional.of((T) annotation);
		}

		for(Annotation a : annotation.annotationType().getAnnotations())
		{
			if(! handled.add(a.annotationType())) continue;

			Optional<T> subAnnotation = find(a, handled);
			if(subAnnotation.isPresent())
			{
				return subAnnotation;
			}
		}

		return Optional.empty();
	}
}
