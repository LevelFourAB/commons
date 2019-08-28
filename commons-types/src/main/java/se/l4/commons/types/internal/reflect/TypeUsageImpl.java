package se.l4.commons.types.internal.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import se.l4.commons.types.reflect.TypeUsage;

/**
 * Implementation of {@link TypeUsage} on top of a {@link AnnotatedType}.
 */
public class TypeUsageImpl
	implements TypeUsage
{
	private static final TypeUsage EMPTY = new TypeUsageImpl(new Annotation[0]);

	private final Annotation[] annotations;

	public TypeUsageImpl(Annotation[] annotations)
	{
		this.annotations = annotations;
	}

	@Override
	public Annotation[] getAnnotations()
	{
		return Arrays.copyOf(annotations, annotations.length);
	}

	@Override
	public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotationClass)
	{
		return getAnnotation(annotationClass);
	}

	@Override
	public boolean equals(Object obj)
	{
		if(! (obj instanceof TypeUsageImpl))
		{
			return false;
		}

		return Arrays.equals(this.annotations, ((TypeUsageImpl) obj).annotations);
	}

	@Override
	public int hashCode()
	{
		return Arrays.hashCode(this.annotations);
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		for(Annotation annotation : annotations)
		{
			if(builder.length() > 0)
			{
				builder.append(" ");
			}

			builder.append(annotation.toString());
		}

		return builder.toString();
	}

	public static TypeUsage empty()
	{
		return EMPTY;
	}

	public static TypeUsageImpl forAnnotatedType(AnnotatedType type)
	{
		return new TypeUsageImpl(type.getAnnotations());
	}

	public static TypeUsageImpl forAnnotatedType(AnnotatedType type, Annotation[] extra)
	{
		if(extra == null)
		{
			return forAnnotatedType(type);
		}

		return new TypeUsageImpl(mergeAnnotations(
			type.getAnnotations(),
			extra
		));
	}

	public static TypeUsageImpl merge(TypeUsage t1, TypeUsage t2)
	{
		return new TypeUsageImpl(mergeAnnotations(
			t1.getAnnotations(),
			t2.getAnnotations()
		));
	}

	private static Annotation[] mergeAnnotations(Annotation[] annotations1, Annotation[] annotations2)
	{
		Set<Annotation> annotations = new LinkedHashSet<>();
		for(Annotation a : annotations1)
		{
			annotations.add(a);
		}

		for(Annotation a : annotations2)
		{
			annotations.add(a);
		}

		return annotations.toArray(new Annotation[annotations.size()]);
	}
}
