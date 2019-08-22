package se.l4.commons.types.internal.reflect;

import java.lang.reflect.Method;

import se.l4.commons.types.reflect.MethodRef;
import se.l4.commons.types.reflect.TypeRef;

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
	public Method getMethod()
	{
		return method;
	}
}
