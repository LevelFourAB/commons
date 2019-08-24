package se.l4.commons.types.internal.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

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
}
