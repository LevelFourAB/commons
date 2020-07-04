package se.l4.commons.types.internal.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;

import se.l4.commons.types.reflect.ConstructorRef;
import se.l4.commons.types.reflect.FieldRef;
import se.l4.commons.types.reflect.MethodRef;
import se.l4.commons.types.reflect.TypeInferrer;
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

	/**
	 * Get the bindings of this type.
	 */
	public TypeRefBindings getTypeBindings()
	{
		return typeBindings;
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
	public TypeRef mergeWithUsage(TypeUsage usage)
	{
		return new TypeRefImpl(type, typeBindings, TypeUsageImpl.merge(this.usage, usage));
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
	public List<TypeVariable<?>> getTypeVariables()
	{
		return typeBindings.getTypeVariables();
	}

	@Override
	public int getTypeParameterCount()
	{
		return typeBindings.getTypeVariables().size();
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
	public TypeInferrer getTypeParameterUsageInferrer(int index, TypeRef patternType)
	{
		Optional<TypeVariable<?>> variable = typeBindings.getTypeVariable(index);
		if(! variable.isPresent())
		{
			return EmptyTypeInferrer.INSTANCE;
		}

		return new TypeParameterUsageInferrer(variable.get(), patternType);
	}

	@Override
	public TypeInferrer getTypeParameterUsageInferrer(String name, TypeRef patternType)
	{
		Optional<TypeVariable<?>> variable = typeBindings.getTypeVariable(name);
		if(! variable.isPresent())
		{
			return EmptyTypeInferrer.INSTANCE;
		}

		return new TypeParameterUsageInferrer(variable.get(), patternType);
	}

	@Override
	public TypeInferrer getTypeParameterInferrer(TypeRef patternType)
	{
		return new TypeParameterInferrer(
			patternType,
			typeBindings.getTypeVariables()
		);
	}

	@Override
	public Optional<TypeRef> withTypeParameter(String name, TypeRef type)
	{
		return typeBindings.withParameter(name, type)
			.map(bindings -> new TypeRefImpl(this.type, bindings, usage));
	}

	@Override
	public Optional<TypeRef> withTypeParameter(int index, TypeRef type)
	{
		return typeBindings.withParameter(index, type)
			.map(bindings -> new TypeRefImpl(this.type, bindings, usage));
	}

	@Override
	public Annotation[] getAnnotations()
	{
		return erasedType.getAnnotations();
	}

	@Override
	public boolean hasAnnotation(Class<? extends Annotation> annotationClass)
	{
		return erasedType.isAnnotationPresent(annotationClass);
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
	public int getModifiers()
	{
		return erasedType.getModifiers();
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
	public boolean isStrict()
	{
		return Modifier.isStrict(getModifiers());
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
	public boolean isAnonymousClass()
	{
		return erasedType.isAnonymousClass();
	}

	@Override
	public boolean isLocalClass()
	{
		return erasedType.isLocalClass();
	}

	@Override
	public boolean isMemberClass()
	{
		return erasedType.isMemberClass();
	}

	@Override
	public boolean isPrimitive()
	{
		return erasedType.isPrimitive();
	}

	@Override
	public boolean isSynthetic()
	{
		return erasedType.isSynthetic();
	}

	@Override
	public boolean isAssignableFrom(TypeRef other)
	{
		List<TypeRef> thisParameters;
		List<TypeRef> otherParameters;

		if(this.getErasedType() == other.getErasedType())
		{
			// If the types are the same
			otherParameters = other.getTypeParameters();
		}
		else if(this.getErasedType() == Object.class)
		{
			// Object are always assignable
			otherParameters = Collections.emptyList();
		}
		else if(! this.getErasedType().isAssignableFrom(other.getErasedType()))
		{
			// Not assignable to us, return false
			return false;
		}
		else
		{
			TypeRef type = other.findSuperclassOrInterface(this.getErasedType())
				.orElseThrow(() -> new RuntimeException("Could not find " + this + " in " + other));

			otherParameters = type.getTypeParameters();
		}

		thisParameters = this.getTypeParameters();

		for(int i=0, n=Math.min(otherParameters.size(), thisParameters.size()); i<n; i++)
		{
			if(! thisParameters.get(i).isAssignableFrom(otherParameters.get(i)))
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public Class<?> getErasedType()
	{
		return erasedType;
	}

	@Override
	public boolean isErasedType(Class<?> type)
	{
		return erasedType == type;
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
	public Optional<TypeRef> findSuperclassOrInterface(Class<?> type)
	{
		Objects.requireNonNull(type);

		return find(tr -> tr.getErasedType() == type ? Optional.of(tr) : Optional.empty());
	}

	@Override
	public <T> Optional<T> find(Function<TypeRef, Optional<T>> finder)
	{
		Objects.requireNonNull(finder);

		AtomicReference<Optional<T>> result = new AtomicReference<>(Optional.empty());
		visitHierarchy(type -> {
			Optional<T> found = finder.apply(type);
			if(found.isPresent())
			{
				result.set(found);

				// Stop the search
				return false;
			}

			// Continue the search
			return true;
		});

		return result.get();
	}

	@Override
	public void visitHierarchy(Predicate<TypeRef> visitor)
	{
		Queue<TypeRef> queue = new LinkedList<>();
		Set<Class<?>> visited = new HashSet<>();

		queue.add(this);

		while(! queue.isEmpty())
		{
			TypeRef type = queue.poll();
			if(! visitor.test(type))
			{
				return;
			}

			for(TypeRef interfaceRef : type.getInterfaces())
			{
				if(! visited.add(interfaceRef.getErasedType())) continue;

				queue.add(interfaceRef);
			}

			Optional<TypeRef> superclass = type.getSuperclass();
			if(superclass.isPresent() && visited.add(superclass.get().getErasedType()))
			{
				queue.add(superclass.get());
			}
		}
	}

	@Override
	public List<FieldRef> getFields()
	{
		return Arrays.stream(erasedType.getFields())
			.map(f -> TypeHelperImpl.resolveField(this, f))
			.collect(ImmutableList.toImmutableList());
	}

	@Override
	public Optional<FieldRef> getField(String name)
	{
		try
		{
			Field field = erasedType.getField(name);
			return Optional.of(TypeHelperImpl.resolveField(this, field));
		}
		catch(NoSuchFieldException e)
		{
			return Optional.empty();
		}
	}

	@Override
	public List<MethodRef> getMethods()
	{
		return Arrays.stream(erasedType.getMethods())
			.map(m -> TypeHelperImpl.resolveMethod(this, m))
			.collect(ImmutableList.toImmutableList());
	}

	@Override
	public Optional<MethodRef> getMethodViaClassParameters(String name, Class<?>... parameterTypes)
	{
		try
		{
			Method method = erasedType.getMethod(name, parameterTypes);
			return Optional.of(TypeHelperImpl.resolveMethod(this, method));
		}
		catch(NoSuchMethodException e)
		{
			return Optional.empty();
		}
	}

	@Override
	public Optional<MethodRef> getMethod(String name, TypeRef... parameterTypes)
	{
		return getMethodViaClassParameters(name, Arrays.stream(parameterTypes)
			.map(TypeRef::getErasedType)
			.toArray(Class[]::new)
		);
	}

	@Override
	public List<ConstructorRef> getConstructors()
	{
		return Arrays.stream(erasedType.getConstructors())
			.map(m -> TypeHelperImpl.resolveConstructor(this, m))
			.collect(ImmutableList.toImmutableList());
	}

	@Override
	public Optional<ConstructorRef> getConstructorViaClassParameters(Class<?>... parameterTypes)
	{
		try
		{
			Constructor<?> constructor = erasedType.getConstructor(parameterTypes);
			return Optional.of(TypeHelperImpl.resolveConstructor(this, constructor));
		}
		catch(NoSuchMethodException e)
		{
			return Optional.empty();
		}
	}

	@Override
	public Optional<ConstructorRef> getConstructor(TypeRef... parameterTypes)
	{
		return getConstructorViaClassParameters(Arrays.stream(parameterTypes)
			.map(TypeRef::getErasedType)
			.toArray(Class[]::new)
		);
	}

	@Override
	public List<FieldRef> getDeclaredFields()
	{
		return Arrays.stream(erasedType.getDeclaredFields())
			.map(f -> TypeHelperImpl.resolveField(this, f))
			.collect(ImmutableList.toImmutableList());
	}

	@Override
	public Optional<FieldRef> getDeclaredField(String name)
	{
		try
		{
			Field field = erasedType.getDeclaredField(name);
			return Optional.of(TypeHelperImpl.resolveField(this, field));
		}
		catch(NoSuchFieldException e)
		{
			return Optional.empty();
		}
	}

	@Override
	public List<MethodRef> getDeclaredMethods()
	{
		return Arrays.stream(erasedType.getDeclaredMethods())
			.map(m -> TypeHelperImpl.resolveMethod(this, m))
			.collect(ImmutableList.toImmutableList());
	}

	@Override
	public Optional<MethodRef> getDeclaredMethodViaClassParameters(String name, Class<?>... parameterTypes)
	{
		try
		{
			Method method = erasedType.getDeclaredMethod(name, parameterTypes);
			return Optional.of(TypeHelperImpl.resolveMethod(this, method));
		}
		catch(NoSuchMethodException e)
		{
			return Optional.empty();
		}
	}

	@Override
	public Optional<MethodRef> getDeclaredMethod(String name, TypeRef... parameterTypes)
	{
		return getMethodViaClassParameters(name, Arrays.stream(parameterTypes)
			.map(TypeRef::getErasedType)
			.toArray(Class[]::new)
		);
	}

	@Override
	public List<ConstructorRef> getDeclaredConstructors()
	{
		return Arrays.stream(erasedType.getDeclaredConstructors())
			.map(m -> TypeHelperImpl.resolveConstructor(this, m))
			.collect(ImmutableList.toImmutableList());
	}

	@Override
	public Optional<ConstructorRef> getDeclaredConstructorViaClassParameters(Class<?>... parameterTypes)
	{
		try
		{
			Constructor<?> constructor = erasedType.getDeclaredConstructor(parameterTypes);
			return Optional.of(TypeHelperImpl.resolveConstructor(this, constructor));
		}
		catch(NoSuchMethodException e)
		{
			return Optional.empty();
		}
	}

	@Override
	public Optional<ConstructorRef> getDeclaredConstructor(TypeRef... parameterTypes)
	{
		return getConstructorViaClassParameters(Arrays.stream(parameterTypes)
			.map(TypeRef::getErasedType)
			.toArray(Class[]::new)
		);
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

				builder.append(ref.toTypeName());
			}

			builder.append('>');
		}
	}

	@Override
	public String toTypeDescription()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(usage.toString());

		if(builder.length() > 0)
		{
			builder.append(' ');
		}

		toTypeDescription(builder);

		return builder.toString();
	}

	private void toTypeDescription(StringBuilder builder)
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

				builder.append(ref.toTypeDescription());
			}

			builder.append('>');
		}
	}

	@Override
	public String toString()
	{
		return "TypeRef{" + toTypeDescription() + "}";
	}
}
