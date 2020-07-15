package se.l4.commons.types.internal.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.util.Optional;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ListIterable;

import se.l4.commons.types.reflect.ExecutableRef;
import se.l4.commons.types.reflect.ParameterRef;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Implementation for {@link ExecutableRef}.
 */
public abstract class ExecutableRefImpl
	implements ExecutableRef
{
	protected final TypeRef parent;
	private final Executable executable;
	private final TypeRefBindings typeBindings;

	public ExecutableRefImpl(
		TypeRef parent,
		Executable executable,
		TypeRefBindings typeBindings
	)
	{
		this.parent = parent;
		this.executable = executable;
		this.typeBindings = typeBindings;
	}

	@Override
	public Executable getExecutable()
	{
		return executable;
	}

	@Override
	public Annotation[] getAnnotations()
	{
		return executable.getAnnotations();
	}

	@Override
	public boolean hasAnnotation(Class<? extends Annotation> annotationClass)
	{
		return executable.isAnnotationPresent(annotationClass);
	}

	@Override
	public ListIterable<String> getTypeParameterNames()
	{
		return typeBindings.getNames();
	}

	@Override
	public ListIterable<TypeRef> getTypeParameters()
	{
		return typeBindings.getResolvedTypeVariables();
	}

	@Override
	public Optional<TypeRef> getTypeParameter(String name)
	{
		return typeBindings.getBinding(name);
	}

	@Override
	public Optional<TypeRef> getTypeParameter(int index)
	{
		return typeBindings.getBinding(index);
	}

	@Override
	public TypeRef getDeclaringType()
	{
		Class<?> declaring = executable.getDeclaringClass();
		return parent.findSuperclassOrInterface(declaring).get();
	}

	@Override
	public TypeRef getReturnType()
	{
		return TypeHelperImpl.resolve(
			executable.getAnnotatedReturnType(),
			typeBindings,
			getAnnotations()
		);
	}

	@Override
	public TypeRef getReceiverType()
	{
		return TypeHelperImpl.resolve(
			executable.getAnnotatedReceiverType(),
			typeBindings
		);
	}

	@Override
	public ListIterable<TypeRef> getParameterTypes()
	{
		return Lists.immutable.of(executable.getAnnotatedParameterTypes())
			.collect(a -> TypeHelperImpl.resolve(a, typeBindings));
	}

	@Override
	public ListIterable<ParameterRef> getParameters()
	{
		return Lists.immutable.of(executable.getParameters())
			.collect(p -> new ParameterRefImpl(this, p, typeBindings));
	}

	@Override
	public int getParameterCount()
	{
		return executable.getParameterCount();
	}

	@Override
	public ListIterable<TypeRef> getExceptionTypes()
	{
		return Lists.immutable.of(executable.getAnnotatedExceptionTypes())
			.collect(a -> TypeHelperImpl.resolve(a, typeBindings));
	}

	@Override
	public boolean isSynthetic()
	{
		return executable.isSynthetic();
	}

	@Override
	public boolean isVarArgs()
	{
		return executable.isVarArgs();
	}
}
