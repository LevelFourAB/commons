package se.l4.commons.types.internal.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.Optional;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import se.l4.commons.types.reflect.AnnotationLocator;
import se.l4.commons.types.reflect.TypeUsage;

/**
 * Implementation of {@link TypeUsage} on top of a {@link AnnotatedType}.
 */
public class TypeUsageImpl
	implements TypeUsage
{
	private static final TypeUsage EMPTY = new TypeUsageImpl(Lists.immutable.empty());

	private final RichIterable<Annotation> annotations;

	public TypeUsageImpl(RichIterable<Annotation> annotations)
	{
		this.annotations = annotations;
	}

	@Override
	public RichIterable<Annotation> getAnnotations()
	{
		return annotations;
	}

	@Override
	public <T extends Annotation> Optional<T> findAnnotation(AnnotationLocator<T> locator)
	{
		return getAnnotation(locator);
	}

	@Override
	public boolean equals(Object obj)
	{
		if(! (obj instanceof TypeUsageImpl))
		{
			return false;
		}

		return this.annotations.equals(((TypeUsageImpl) obj).annotations);
	}

	@Override
	public int hashCode()
	{
		return this.annotations.hashCode();
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
		return new TypeUsageImpl(Lists.immutable.of(type.getAnnotations()));
	}

	public static TypeUsageImpl forAnnotatedType(AnnotatedType type, RichIterable<Annotation> extra)
	{
		if(extra == null || extra.isEmpty())
		{
			return forAnnotatedType(type);
		}

		return new TypeUsageImpl(mergeAnnotations(
			Lists.immutable.of(type.getAnnotations()),
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

	private static RichIterable<Annotation> mergeAnnotations(
		RichIterable<Annotation> annotations1,
		RichIterable<Annotation> annotations2
	)
	{
		MutableSet<Annotation> annotations = UnifiedSet.newSet();
		for(Annotation a : annotations1)
		{
			annotations.add(a);
		}

		for(Annotation a : annotations2)
		{
			annotations.add(a);
		}

		return annotations;
	}
}
