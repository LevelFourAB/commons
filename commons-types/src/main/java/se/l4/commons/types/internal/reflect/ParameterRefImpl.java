package se.l4.commons.types.internal.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Optional;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Lists;

import se.l4.commons.types.reflect.AnnotationLocator;
import se.l4.commons.types.reflect.ExecutableRef;
import se.l4.commons.types.reflect.ParameterRef;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Implementation of {@link ParameterRef}.
 */
public class ParameterRefImpl
	implements ParameterRef
{
	private final ExecutableRef parent;
	private final Parameter parameter;
	private final TypeRefBindings typeBindings;

	public ParameterRefImpl(
		ExecutableRef parent,
		Parameter parameter,
		TypeRefBindings typeBindings
	)
	{
		this.parent = parent;
		this.parameter = parameter;
		this.typeBindings = typeBindings;

	}

	@Override
	public Parameter getParameter()
	{
		return parameter;
	}

	@Override
	public RichIterable<Annotation> getAnnotations()
	{
		return Lists.immutable.of(parameter.getAnnotations());
	}

	@Override
	public <T extends Annotation> Optional<T> findAnnotation(AnnotationLocator<T> locator)
	{
		return getAnnotation(locator);
	}

	@Override
	public Optional<String> getName()
	{
		return parameter.isNamePresent() ? Optional.of(parameter.getName()) : Optional.empty();
	}

	@Override
	public boolean isNamePresent()
	{
		return parameter.isNamePresent();
	}

	@Override
	public TypeRef getType()
	{
		return TypeHelperImpl.resolve(parameter.getAnnotatedType(), typeBindings);
	}

	@Override
	public boolean isImplicit()
	{
		return parameter.isImplicit();
	}

	@Override
	public boolean isSynthetic()
	{
		return parameter.isSynthetic();
	}

	@Override
	public boolean isVarArgs()
	{
		return parameter.isVarArgs();
	}

	@Override
	public String toString()
	{
		return "ParameterRef{" + getType().toTypeName() + "}";
	}
}
