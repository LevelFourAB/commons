package se.l4.commons.types.internal.reflect;

import java.lang.reflect.Type;
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

	public Optional<TypeVariable<?>> getTypeVariable(int index)
	{
		if(index >= 0 && index < typeVariables.size())
		{
			return Optional.of(typeVariables.get(index));
		}

		return Optional.empty();
	}

	public Optional<TypeVariable<?>> getTypeVariable(String name)
	{
		for(int i=0, n=typeVariables.size(); i<n; i++)
		{
			TypeVariable<?> tv = (TypeVariable<?>) typeVariables.get(i);
			if(tv.getName().equals(name))
			{
				return Optional.of(tv);
			}
		}

		return Optional.empty();
	}

	public Optional<TypeRef> getBinding(int index)
	{
		if(index >= 0 && index < resolvedTypeVariables.size())
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

	public Optional<TypeRefBindings> withParameter(int index, TypeRef ref)
	{
		if(index < 0 || index >= typeVariables.size())
		{
			return Optional.empty();
		}

		if(! isAssignable(typeVariables.get(index), ref))
		{
			return Optional.empty();
		}

		ImmutableList.Builder<TypeRef> resolved = ImmutableList.builder();
		for(int i=0, n=resolvedTypeVariables.size(); i<n; i++)
		{
			if(i == index)
			{
				resolved.add(ref);
			}
			else
			{
				resolved.add(resolvedTypeVariables.get(i));
			}
		}

		return Optional.of(
			new TypeRefBindings(typeVariables, resolved.build())
		);
	}

	public Optional<TypeRefBindings> withParameter(String name, TypeRef ref)
	{
		int index = -1;
		for(int i=0, n=typeVariables.size(); i<n; i++)
		{
			TypeVariable<?> tv = (TypeVariable<?>) typeVariables.get(i);
			if(tv.getName().equals(name))
			{
				index = i;
				break;
			}
		}

		return withParameter(index, ref);
	}

	private boolean isAssignable(TypeVariable<?> v, TypeRef ref)
	{
		for(Type type : v.getBounds())
		{
			if(! TypeHelperImpl.reference(type).isAssignableFrom(ref))
			{
				return false;
			}
		}

		return true;
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
