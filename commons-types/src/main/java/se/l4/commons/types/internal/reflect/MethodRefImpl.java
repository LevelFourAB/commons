package se.l4.commons.types.internal.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import se.l4.commons.types.reflect.MethodRef;
import se.l4.commons.types.reflect.TypeRef;
import se.l4.commons.types.reflect.TypeSpecificity;

public class MethodRefImpl
	extends ExecutableRefImpl
	implements MethodRef
{
	private final Method method;

	public MethodRefImpl(
		TypeRef parent,
		Method method,
		TypeRefBindings typeBindings
	)
	{
		super(parent, method, typeBindings);

		this.method = method;
	}

	@Override
	public TypeRef getDeclaringType()
	{
		Class<?> declaring = method.getDeclaringClass();
		return parent.findSuperclassOrInterface(declaring).get();
	}

	@Override
	public Method getMethod()
	{
		return method;
	}

	@Override
	public String getName()
	{
		return method.getName();
	}

	@Override
	public int getModifiers()
	{
		return method.getModifiers();
	}

	@Override
	public boolean isAbstract()
	{
		return Modifier.isAbstract(getModifiers());
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
	public boolean isSynchronized()
	{
		return Modifier.isFinal(getModifiers());
	}

	@Override
	public boolean isNative()
	{
		return Modifier.isNative(getModifiers());
	}

	@Override
	public boolean isStrict()
	{
		return Modifier.isStrict(getModifiers());
	}

	@Override
	public boolean isBridge()
	{
		return method.isBridge();
	}

	@Override
	public boolean isDefault()
	{
		return method.isDefault();
	}

	@Override
	public String toDescription()
	{
		return getReturnType().toTypeName() + " "
		 	+ getName() + "(" +
			getParameterTypes()
				.stream()
				.map(t -> t.toTypeName())
				.collect(Collectors.joining(", "))
			+ ")";
	}

	@Override
	public Optional<MethodRef> findIn(TypeRef type, TypeSpecificity returnTypeSpecificity)
	{
		Objects.requireNonNull(type);
		Objects.requireNonNull(returnTypeSpecificity);

		if(type == this.parent)
		{
			return Optional.of(this);
		}

		if(isPrivate())
		{
			// Private methods are never found in other types
			return Optional.empty();
		}

		return type.getDeclaredMethodViaClassParameters(
			method.getName(),
			method.getParameterTypes()
		).filter(ref -> {
			// The found method is private, not available
			if(ref.isPrivate()) return false;

			// Both methods must be static or non-static
			if(ref.isStatic() != isStatic()) return false;

			switch(returnTypeSpecificity)
			{
				case IDENTICAL:
					return ref.getMethod().getReturnType() == method.getReturnType();
				case LESS:
					if(isProtected() && ref.isPublic())
					{
						// This method has reduced visibility compared to the parent
						return false;
					}

					return ref.getMethod().getReturnType().isAssignableFrom(method.getReturnType());
				case MORE:
					if(isPublic() && ref.isProtected())
					{
						// This method is public but the more specific method has reduced visibility
						return false;
					}

					return method.getReturnType().isAssignableFrom(ref.getMethod().getReturnType());
			}

			return false;
		});
	}
}
