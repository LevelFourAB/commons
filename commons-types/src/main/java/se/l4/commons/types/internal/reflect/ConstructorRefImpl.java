package se.l4.commons.types.internal.reflect;

import java.lang.reflect.Constructor;

import se.l4.commons.types.reflect.ConstructorRef;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Implementation of {@link ConstructorRef}.
 */
public class ConstructorRefImpl
	extends ExecutableRefImpl
	implements ConstructorRef
{
	private final Constructor<?> constructor;

	public ConstructorRefImpl(
		TypeRef parent,
		Constructor<?> constructor,
		TypeRefBindings typeBindings
	)
	{
		super(parent, constructor, typeBindings);

		this.constructor = constructor;
	}

	@Override
	public Constructor<?> getConstructor()
	{
		return constructor;
	}

	@Override
	public String getName()
	{
		return constructor.getName();
	}

	@Override
	public int getModifiers()
	{
		return constructor.getModifiers();
	}
}
