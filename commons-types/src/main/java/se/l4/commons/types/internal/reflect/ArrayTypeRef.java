package se.l4.commons.types.internal.reflect;

import java.lang.reflect.Type;
import java.util.Optional;

import se.l4.commons.types.reflect.TypeRef;
import se.l4.commons.types.reflect.TypeUsage;

public class ArrayTypeRef
	extends TypeRefImpl
{
	private final TypeRef componentType;

	ArrayTypeRef(
		Type type,
		TypeRefBindings typeBindings,
		TypeUsage typeUsage,
		TypeRef componentType
	)
	{
		super(type, typeBindings, typeUsage);

		this.componentType = componentType;
	}

	@Override
	public Optional<TypeRef> getComponentType()
	{
		return Optional.of(componentType);
	}
}
