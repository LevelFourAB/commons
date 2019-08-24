package se.l4.commons.types.internal.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;

import se.l4.commons.types.reflect.FieldRef;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Implementation of {@link FieldRef}.
 */
public class FieldRefImpl
	implements FieldRef
{
	private final TypeRef parent;
	private final Field field;
	private final TypeRefBindings typeBindings;

	public FieldRefImpl(
		TypeRef parent,
		Field field,
		TypeRefBindings typeBindings
	)
	{
		this.parent = parent;
		this.field = field;
		this.typeBindings = typeBindings;
	}

	@Override
	public TypeRef getDeclaringType()
	{
		Class<?> declaring = field.getDeclaringClass();
		return parent.findSuperclassOrInterface(declaring).get();
	}

	@Override
	public boolean isSynthetic()
	{
		return field.isSynthetic();
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

	@Override
	public int getModifiers()
	{
		return field.getModifiers();
	}

	@Override
	public boolean isStatic()
	{
		return Modifier.isStatic(getModifiers());
	}

	@Override
	public boolean isFinal()
	{
		return Modifier.isFinal(getModifiers());
	}

	@Override
	public boolean isTransient()
	{
		return Modifier.isTransient(getModifiers());
	}

	@Override
	public boolean isVolatile()
	{
		return Modifier.isVolatile(getModifiers());
	}
}
