package se.l4.commons.types.internal.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;

import se.l4.commons.types.reflect.FieldRef;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Implementation of {@link FieldRef}.
 */
public class FieldRefImpl
	implements FieldRef
{
	private final Field field;
	private final TypeRefBindings typeBindings;

	public FieldRefImpl(
		Field field,
		TypeRefBindings typeBindings
	)
	{
		this.field = field;
		this.typeBindings = typeBindings;
	}

	@Override
	public Field getField()
	{
		return field;
	}

	@Override
	public Annotation[] getAnnotations()
	{
		return field.getAnnotations();
	}

	@Override
	public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotationClass)
	{
		return getAnnotation(annotationClass);
	}

	@Override
	public TypeRef getType()
	{
		return TypeHelperImpl.resolve(field.getAnnotatedType(), typeBindings);
	}

	@Override
	public String getName()
	{
		return field.getName();
	}

}
