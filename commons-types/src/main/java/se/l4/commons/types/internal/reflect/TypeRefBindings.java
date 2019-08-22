package se.l4.commons.types.internal.reflect;

import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

import se.l4.commons.types.reflect.TypeRef;

/**
 * Container for working with type parameters.
 */
public class TypeRefBindings
{
	private static final TypeRefBindings EMPTY = new TypeRefBindings(
		Collections.emptyList(),
		Collections.emptyList()
	);

	private final List<TypeVariable<?>> typeVariables;
	private final List<TypeRef> resolvedTypeVariables;

	private TypeRefBindings(
		List<TypeVariable<?>> typeVariables,
		List<TypeRef> resolvedTypeVariables
	)
	{
		this.typeVariables = typeVariables;
		this.resolvedTypeVariables = resolvedTypeVariables;
	}

	public boolean isEmpty()
	{
		return typeVariables.isEmpty();
	}

	public List<TypeRef> getResolvedTypeVariables()
	{
		return resolvedTypeVariables;
	}

	public List<TypeVariable<?>> getTypeVariables()
	{
		return typeVariables;
	}

	public Optional<TypeRef> getBinding(int index)
	{
		if(index < resolvedTypeVariables.size())
		{
			return Optional.of(resolvedTypeVariables.get(index));
		}

		return Optional.empty();
	}

	public Optional<TypeRef> getBinding(String name)
	{
		for(int i=0, n=typeVariables.size(); i<n; i++)
		{
			TypeVariable<?> tv = (TypeVariable<?>) typeVariables.get(i);
			if(tv.getName().equals(name))
			{
				return Optional.of(resolvedTypeVariables.get(i));
			}
		}

		return Optional.empty();
	}

	public List<String> getNames()
	{
		return typeVariables.stream()
			.map(tv -> tv.getName())
			.collect(ImmutableList.toImmutableList());
	}

	@Override
	public int hashCode()
	{
		return resolvedTypeVariables.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if(! (obj instanceof TypeRefBindings))
		{
			return false;
		}

		return resolvedTypeVariables.equals(((TypeRefBindings) obj).resolvedTypeVariables);
	}

	public TypeRefBindings with(
		TypeVariable<?>[] typeVariables,
		TypeRef[] resolvedVariables
	)
	{
		return new TypeRefBindings(
			ImmutableList.<TypeVariable<?>>builder().addAll(this.typeVariables).add(typeVariables).build(),
			ImmutableList.<TypeRef>builder().addAll(this.resolvedTypeVariables).add(resolvedVariables).build()
		);
	}

	public static TypeRefBindings createUnresolved(
		TypeVariable<?>[] typeVariables
	)
	{
		ImmutableList.Builder<TypeRef> resolved = ImmutableList.builder();
		for(TypeVariable<?> tv : typeVariables)
		{
			resolved.add(TypeHelperImpl.reference(tv));
		}

		return new TypeRefBindings(
			ImmutableList.copyOf(typeVariables),
			resolved.build()
		);
	}

	public static TypeRefBindings create(
		TypeVariable<?>[] typeVariables,
		TypeRef[] resolvedVariables
	)
	{
		return new TypeRefBindings(
			ImmutableList.copyOf(typeVariables),
			ImmutableList.copyOf(resolvedVariables)
		);
	}

	public static TypeRefBindings empty()
	{
		return EMPTY;
	}
}
