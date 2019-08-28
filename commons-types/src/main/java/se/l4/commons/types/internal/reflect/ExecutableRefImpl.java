package se.l4.commons.types.internal.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

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
	public Annotation[] getAnnotations()
	{
		return executable.getAnnotations();
	}

	@Override
	public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotationClass)
	{
		return getAnnotation(annotationClass);
	}

	@Override
	public List<String> getTypeParameterNames()
	{
		return typeBindings.getNames();
	}

	@Override
	public List<TypeRef> getTypeParameters()
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
	public List<TypeRef> getParameterTypes()
	{
		return Arrays.stream(executable.getAnnotatedParameterTypes())
			.map(a -> TypeHelperImpl.resolve(a, typeBindings))
			.collect(ImmutableList.toImmutableList());
	}

	@Override
	public List<ParameterRef> getParameters()
	{
		return Arrays.stream(executable.getParameters())
			.map(p -> new ParameterRefImpl(this, p, typeBindings))
			.collect(ImmutableList.toImmutableList());
	}

	@Override
	public List<TypeRef> getExceptionTypes()
	{
		return Arrays.stream(executable.getAnnotatedExceptionTypes())
			.map(a -> TypeHelperImpl.resolve(a, typeBindings))
			.collect(ImmutableList.toImmutableList());
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
