package se.l4.commons.types.internal.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import se.l4.commons.types.reflect.FieldRef;
import se.l4.commons.types.reflect.MethodRef;
import se.l4.commons.types.reflect.TypeRef;
import se.l4.commons.types.reflect.TypeUsage;

/**
 * Implementation of {@link TypeRef}.
 */
public class TypeRefImpl
	implements TypeRef
{
	protected final Type type;
	protected final Class<?> erasedType;

	protected final TypeRefBindings typeBindings;
	protected final TypeUsage usage;

	TypeRefImpl(
		Type type,
		TypeRefBindings typeBindings,
		TypeUsage typeUsage
	)
	{
		this.type = type;
		this.erasedType = TypeHelperImpl.getErasedType(type);
		this.typeBindings = typeBindings;
		this.usage = typeUsage;
	}

	@Override
	public Type getType()
	{
		return type;
	}

	@Override
	public TypeUsage getUsage()
	{
		return usage;
	}

	@Override
	public TypeRef withoutUsage()
	{
		return withUsage(TypeUsageImpl.empty());
	}

	@Override
	public TypeRef withUsage(TypeUsage usage)
	{
		return new TypeRefImpl(type, typeBindings, usage);
	}

	@Override
	public boolean equals(Object obj)
	{
		if(! (obj instanceof TypeRefImpl))
		{
			return false;
		}

		TypeRefImpl other = (TypeRefImpl) obj;
		return TypeHelperImpl.typeEquals(type, other.type)
			&& typeBindings.equals(other.typeBindings)
			&& usage.equals(other.usage);
	}

	@Override
	public int hashCode()
	{
		return TypeHelperImpl.typeHashCode(type)
			^ typeBindings.hashCode()
			^ usage.hashCode();
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
	public Optional<TypeRef> getTypeParameter(int index)
	{
		return typeBindings.getBinding(index);
	}

	@Override
	public Optional<TypeRef> getTypeParameter(String name)
	{
		return typeBindings.getBinding(name);
	}

	@Override
	public Annotation[] getAnnotations()
	{
		return erasedType.getAnnotations();
	}

	@Override
	public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotationClass)
	{
		return find(tr -> tr.getAnnotation(annotationClass));
	}

	@Override
	public boolean isResolved()
	{
		return type instanceof Class
			|| type instanceof GenericArrayType
			|| type instanceof ParameterizedType;
	}

	@Override
	public boolean isFullyResolved()
	{
		if(! isResolved())
		{
			return false;
		}

		for(TypeRef type : typeBindings.getResolvedTypeVariables())
		{
			if(! type.isResolved())
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean isAnnotation()
	{
		return erasedType.isAnnotation();
	}

	@Override
	public boolean isArray()
	{
		return erasedType.isArray();
	}

	@Override
	public boolean isEnum()
	{
		return erasedType.isEnum();
	}

	@Override
	public boolean isInterface()
	{
		return erasedType.isInterface();
	}

	@Override
	public Class<?> getErasedType()
	{
		return erasedType;
	}

	@Override
	public Optional<TypeRef> getComponentType()
	{
		if(! erasedType.isArray())
		{
			return Optional.empty();
		}

		return Optional.of(
			TypeHelperImpl.reference(erasedType.getComponentType())
		);
	}

	@Override
	public Optional<TypeRef> getSuperclass()
	{
		if(erasedType.isInterface())
		{
			return Optional.empty();
		}

		AnnotatedType parent = erasedType.getAnnotatedSuperclass();
		if(parent == null)
		{
			return Optional.empty();
		}

		return Optional.of(TypeHelperImpl.resolve(
			parent,
			typeBindings
		));
	}

	@Override
	public Optional<TypeRef> findSuperclass(Class<?> superclass)
	{
		Objects.requireNonNull(superclass);

		Optional<TypeRef> parent = getSuperclass();

		while(parent.isPresent() && parent.get().getErasedType() != superclass)
		{
			parent = parent.get().getSuperclass();
		}

		return parent;
	}

	@Override
	public List<TypeRef> getInterfaces()
	{
		return Arrays.stream(erasedType.getAnnotatedInterfaces())
			.map(t -> TypeHelperImpl.resolve(t, typeBindings))
			.collect(ImmutableList.toImmutableList());
	}

	@Override
	public Optional<TypeRef> getInterface(Class<?> type)
	{
		Objects.requireNonNull(type);

		for(AnnotatedType interfaceType : erasedType.getAnnotatedInterfaces())
		{
			if(TypeHelperImpl.getErasedType(interfaceType.getType()) == type)
			{
				return Optional.of(
					TypeHelperImpl.resolve(interfaceType, typeBindings)
				);
			}
		}

		return Optional.empty();
	}

	@Override
	public Optional<TypeRef> findInterface(Class<?> type)
	{
		Objects.requireNonNull(type);

		return find(tr -> tr.isInterface() && tr.getErasedType() == type ? Optional.of(tr) : Optional.empty());
	}

	@Override
	public <T> Optional<T> find(Function<TypeRef, Optional<T>> finder)
	{
		Objects.requireNonNull(finder);

		Set<Class<?>> visited = new HashSet<>();
		return find(visited, finder, this);
	}

	private <T> Optional<T> find(
		Set<Class<?>> visited,
		Function<TypeRef, Optional<T>> finder,
		TypeRefImpl type
	)
	{
		if(visited.contains(type.getErasedType()))
		{
			return Optional.empty();
		}

		Optional<T> matches = finder.apply(type);
		if(matches.isPresent())
		{
			return matches;
		}

		visited.add(type.getErasedType());

		for(AnnotatedType at : type.getErasedType().getAnnotatedInterfaces())
		{
			TypeRefImpl tr = (TypeRefImpl) TypeHelperImpl.resolve(at, type.typeBindings);
			matches = find(visited, finder, tr);
			if(matches.isPresent())
			{
				return matches;
			}
		}

		Optional<TypeRef> superclass = type.getSuperclass();
		if(superclass.isPresent())
		{
			return find(visited, finder, (TypeRefImpl) superclass.get());
		}

		return Optional.empty();
	}

	@Override
	public List<FieldRef> getFields()
	{
		return Arrays.stream(erasedType.getFields())
			.map(f -> TypeHelperImpl.resolveField(this, f, typeBindings))
			.collect(ImmutableList.toImmutableList());
	}

	@Override
	public List<MethodRef> getMethods()
	{
		return Arrays.stream(erasedType.getMethods())
			.map(m -> TypeHelperImpl.resolveMethod(this, m, typeBindings))
			.collect(ImmutableList.toImmutableList());
	}

	@Override
	public String toTypeName()
	{
		StringBuilder builder = new StringBuilder();

		toTypeName(builder);

		return builder.toString();
	}

	private void toTypeName(StringBuilder builder)
	{
		builder.append(erasedType.getName());

		if(! typeBindings.isEmpty())
		{
			builder.append('<');

			boolean first = true;
			for(TypeRef ref : typeBindings.getResolvedTypeVariables())
			{
				if(! first)
				{
					builder.append(", ");
				}
				else
				{
					first = false;
				}

				builder.append(ref.toString());
			}

			builder.append('>');
		}
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(usage.toString());

		if(builder.length() > 0)
		{
			builder.append(' ');
		}

		toTypeName(builder);

		return builder.toString();
	}
}
