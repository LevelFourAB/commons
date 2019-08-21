package se.l4.commons.types.internal.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.Arrays;
import java.util.LinkedHashSet;
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
		return annotations;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(! (obj instanceof TypeUsageImpl))
		{
			return false;
		}

		return Arrays.equals(getAnnotations(), ((TypeUsage) obj).getAnnotations());
	}

	@Override
	public int hashCode()
	{
		return Arrays.hashCode(getAnnotations());
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		for(Annotation annotation : getAnnotations())
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

	public static TypeUsageImpl merge(TypeUsage t1, TypeUsage t2)
	{
		Set<Annotation> annotations = new LinkedHashSet<>();
		for(Annotation a : t1.getAnnotations())
		{
			annotations.add(a);
		}

		for(Annotation a : t2.getAnnotations())
		{
			annotations.add(a);
		}

		return new TypeUsageImpl(annotations.toArray(new Annotation[annotations.size()]));
	}
}
