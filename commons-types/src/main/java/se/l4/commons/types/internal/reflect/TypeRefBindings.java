package se.l4.commons.types.internal.reflect;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Optional;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;

import se.l4.commons.types.reflect.TypeRef;

/**
 * Container for working with type parameters.
 */
public class TypeRefBindings
{
	private static final TypeRefBindings EMPTY = new TypeRefBindings(
		Lists.immutable.empty(),
		Lists.immutable.empty()
	);

	private final ImmutableList<TypeVariable<?>> typeVariables;
	private final ImmutableList<TypeRef> resolvedTypeVariables;

	private TypeRefBindings(
		ImmutableList<TypeVariable<?>> typeVariables,
		ImmutableList<TypeRef> resolvedTypeVariables
	)
	{
		this.typeVariables = typeVariables;
		this.resolvedTypeVariables = resolvedTypeVariables;
	}

	public boolean isEmpty()
	{
		return typeVariables.isEmpty();
	}

	public ListIterable<TypeRef> getResolvedTypeVariables()
	{
		return resolvedTypeVariables;
	}

	public ListIterable<TypeVariable<?>> getTypeVariables()
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

	public ListIterable<String> getNames()
	{
		return typeVariables.collect(TypeVariable::getName);
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

		MutableList<TypeRef> resolved = Lists.mutable.empty();
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
			new TypeRefBindings(typeVariables, resolved.toImmutable())
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
		MutableList<TypeVariable<?>> newTypeVariables = Lists.mutable.ofAll(this.typeVariables);
		for(TypeVariable<?> tv : typeVariables)
		{
			newTypeVariables.add(tv);
		}

		MutableList<TypeRef> newResolvedVariables = Lists.mutable.ofAll(this.resolvedTypeVariables);
		for(TypeRef tr : resolvedVariables)
		{
			newResolvedVariables.add(tr);
		}

		return new TypeRefBindings(
			newTypeVariables.toImmutable(),
			newResolvedVariables.toImmutable()
		);
	}

	public static TypeRefBindings createUnresolved(
		TypeVariable<?>[] typeVariables
	)
	{
		ImmutableList<TypeVariable<?>> newTypeVariables = Lists.immutable.of(typeVariables);
		ImmutableList<TypeRef> newResolvedVariables = newTypeVariables
			.collect(TypeHelperImpl::reference);

		return new TypeRefBindings(
			newTypeVariables,
			newResolvedVariables
		);
	}

	public static TypeRefBindings create(
		TypeVariable<?>[] typeVariables,
		TypeRef[] resolvedVariables
	)
	{
		return new TypeRefBindings(
			Lists.immutable.of(typeVariables),
			Lists.immutable.of(resolvedVariables)
		);
	}

	public static TypeRefBindings empty()
	{
		return EMPTY;
	}
}
