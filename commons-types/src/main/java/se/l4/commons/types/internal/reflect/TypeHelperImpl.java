package se.l4.commons.types.internal.reflect;

import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.AnnotatedWildcardType;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import se.l4.commons.types.reflect.TypeRef;

/**
 * Helper that creates type references.
 */
public class TypeHelperImpl
{
	private static final AnnotatedType[] EMPTY = new AnnotatedType[0];

	private TypeHelperImpl()
	{
	}

	/**
	 * Create a reference to the given type using the given type parameters
	 * to resolve generic parameters.
	 *
	 * @param type
	 * @param parameters
	 * @return
	 */
	public static TypeRef reference(Type type, Type... parameters)
	{
		/*
		 * Create emulated AnnotatedType instances for the type parameters
		 * provided and use those for further resolving.
		 */
		return reference(
			AnnotatedTypeEmulation.annotate(type),
			Arrays.stream(parameters)
				.map(AnnotatedTypeEmulation::annotate)
				.toArray(AnnotatedType[]::new)
		);
	}

	public static TypeRef reference(AnnotatedType type)
	{
		return reference(type, EMPTY);
	}

	public static TypeRef reference(AnnotatedType type, AnnotatedType... parameters)
	{
		TypeRefBindings bindings;
		if(type.getType() instanceof Class)
		{
			Class<?> c = (Class<?>) type.getType();

			TypeVariable<?>[] typeVariables = c.getTypeParameters();
			TypeRef[] resolved = new TypeRef[typeVariables.length];
			for(int i=0, n=typeVariables.length; i<n; i++)
			{
				TypeVariable<?> tv = typeVariables[i];

				if(i < parameters.length)
				{
					// There are still parameters available
					resolved[i] = reference(parameters[i]);
				}
				else
				{
					resolved[i] = reference(tv);
				}
			}

			bindings = TypeRefBindings.create(typeVariables, resolved);
		}
		else
		{
			bindings = TypeRefBindings.empty();
		}

		return new TypeRefImpl(
			type.getType(),
			bindings,
			TypeUsageImpl.forAnnotatedType(type)
		);
	}

	public static TypeRef resolve(AnnotatedType type, TypeRefBindings bindings)
	{
		if(type instanceof AnnotatedTypeVariable)
		{
			TypeVariable<?> tv = (TypeVariable<?>) type.getType();

			Optional<TypeRef> binding = bindings.getBinding(tv.getName());
			if(binding.isPresent())
			{
				TypeRef ref = binding.get();
				return ref
					.withUsage(TypeUsageImpl.merge(
						ref.getUsage(),
						TypeUsageImpl.forAnnotatedType(type)
					));
			}
			else
			{
				return new TypeRefImpl(
					type.getType(),
					TypeRefBindings.empty(),
					TypeUsageImpl.forAnnotatedType(type)
				);
			}
		}
		else if(type instanceof AnnotatedParameterizedType)
		{
			AnnotatedParameterizedType apt = (AnnotatedParameterizedType) type;
			ParameterizedType pt = (ParameterizedType) apt.getType();

			TypeVariable<?>[] variables = ((Class<?>) pt.getRawType()).getTypeParameters();

			AnnotatedType[] annotatedTypeArguments = apt.getAnnotatedActualTypeArguments();
			TypeRef[] typeArguments = resolveAll(annotatedTypeArguments, bindings);

			return new TypeRefImpl(
				type.getType(),
				TypeRefBindings.create(variables, typeArguments),
				TypeUsageImpl.forAnnotatedType(type)
			);
		}
		else if(type instanceof AnnotatedWildcardType)
		{
			return new TypeRefImpl(
				type.getType(),
				TypeRefBindings.empty(),
				TypeUsageImpl.forAnnotatedType(type)
			);
		}
		else if(type instanceof AnnotatedArrayType)
		{
			AnnotatedArrayType aat = (AnnotatedArrayType) type;

			TypeRef componentType = resolve(
				aat.getAnnotatedGenericComponentType(),
				bindings
			);

			return new ArrayTypeRef(
				type.getType(),
				TypeRefBindings.empty(),
				TypeUsageImpl.forAnnotatedType(type),
				componentType
			);
		}
		else if(type.getType() instanceof Class)
		{
			Class<?> c = (Class<?>) type.getType();

			return new TypeRefImpl(
				type.getType(),
				TypeRefBindings.createUnresolved(c.getTypeParameters()),
				TypeUsageImpl.forAnnotatedType(type)
			);
		}
		else
		{
			throw new IllegalArgumentException("Can not resolve a suitable TypeRef for the type " + type);
		}
	}

	private static TypeRef[] resolveAll(AnnotatedType[] types, TypeRefBindings bindings)
	{
		return Arrays.stream(types)
			.map(at -> resolve(at, bindings))
			.toArray(TypeRef[]::new);
	}

	/**
	 * Advanced: Find the common type for the given types. This currently only
	 * returns the first type, but is provided as a way if the JVM in the
	 * future supports multiple types in {@link java.lang.reflect.WildcardType}
	 * or {@link java.lang.reflect.TypeVariable}.
	 *
	 *
	 * @param types
	 * @return
	 */
	public static Optional<AnnotatedType> findCommon(AnnotatedType[] types)
	{
		if(types.length == 0)
		{
			return Optional.empty();
		}

		return Optional.of(types[0]);
	}

	/**
	 * Advanced: Find the common type for the given types. This currently only
	 * returns the first type, but is provided as a way if the JVM in the
	 * future supports multiple types in {@link java.lang.reflect.WildcardType}
	 * or {@link java.lang.reflect.TypeVariable}.
	 *
	 *
	 * @param types
	 * @return
	 */
	public static Optional<Type> findCommon(Type[] types)
	{
		if(types.length == 0)
		{
			return Optional.empty();
		}

		return Optional.of(types[0]);
	}

	/**
	 * Get the class the given type erases to.
	 */
	public static Class<?> getErasedType(Type type)
	{
		if(type instanceof Class)
		{
			return (Class<?>) type;
		}
		else if(type instanceof GenericArrayType)
		{
			Class<?> componentType = getErasedType(
				((GenericArrayType) type).getGenericComponentType()
			);

			return (Class<?>) Array.newInstance(componentType, 0);
		}
		else if(type instanceof ParameterizedType)
		{
			return getErasedType(
				((ParameterizedType) type).getRawType()
			);
		}
		else if(type instanceof WildcardType)
		{
			WildcardType wc = (WildcardType) type;
			Optional<Type> common = findCommon(wc.getUpperBounds());
			if(common.isPresent())
			{
				return getErasedType(common.get());
			}
			else
			{
				return Object.class;
			}
		}
		else if(type instanceof TypeVariable)
		{
			TypeVariable<?> tv = (TypeVariable<?>) type;
			Optional<Type> common = findCommon(tv.getBounds());
			if(common.isPresent())
			{
				return getErasedType(common.get());
			}
			else
			{
				return Object.class;
			}
		}

		throw new IllegalArgumentException("Unsupported type: " + type);
	}

	/**
	 * Calculate a stable hash code for the given type. This ignores the hash
	 * code returned by the type and instead calculates a custom one. This is
	 * to support custom implementations of {@link Type} that do not share the
	 * hashcode generated by the JVM.
	 *
	 * @param type
	 * @return
	 */
	public static int typeHashCode(Type type)
	{
		if(type == null)
		{
			return 0;
		}
		else if(type instanceof Class)
		{
			// Classes resolve their direct hash code
			return type.hashCode();
		}
		else if(type instanceof GenericArrayType)
		{
			// Generic arrays, just pull out the component type hashcode
			return typeHashCode(
				((GenericArrayType) type).getGenericComponentType()
			);
		}
		else if(type instanceof ParameterizedType)
		{
			// Parameterized types mix arguments, the owner and the raw type
			ParameterizedType pt = (ParameterizedType) type;
			return typeHashCodeArray(pt.getActualTypeArguments())
				^ typeHashCode(pt.getOwnerType())
				^ typeHashCode(pt.getRawType());
		}
		else if(type instanceof TypeVariable)
		{
			// Type variables mix the generic declaration and the name
			TypeVariable<?> tv = (TypeVariable<?>) type;

			GenericDeclaration declaration = tv.getGenericDeclaration();
			int result;
			if(declaration instanceof Type)
			{
				// If the declaration is a type, pass into our hashcode function
				result = typeHashCode((Type) declaration);
			}
			else
			{
				// Fallback to calling hashCode
				result = declaration.hashCode();
			}

			return result ^ tv.getName().hashCode();
		}
		else if(type instanceof WildcardType)
		{
			// Wildcards mix their lower and upper bounds
			WildcardType wc = (WildcardType) type;
			return typeHashCodeArray(wc.getLowerBounds())
				^ typeHashCodeArray(wc.getUpperBounds());
		}
		else
		{
			// Fallback to use the hashCode of the incoming type
			return type.hashCode();
		}
	}

	/**
	 * Create a hash code for an array of types. Uses the same calculation as
	 * {@link Arrays#hashCode(Object[])} but calls {@link #typeHashCode(Type)}
	 * for each element in the array.
	 */
	private static int typeHashCodeArray(Type[] types)
	{
		int result = 1;
		for(Type type : types)
		{
            result = 31 * result + typeHashCode(type);
		}
		return result;
	}

	/**
	 * Get if two types equal each other.
	 *
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static boolean typeEquals(Type t1, Type t2)
	{
		if(t1 == null || t2 == null)
		{
			// Handle on of the types being null
			return t1 == t2;
		}
		else if(t1 instanceof Class)
		{
			return t1.equals(t2);
		}
		else if(t1 instanceof GenericArrayType)
		{
			if(! (t2 instanceof GenericArrayType))
			{
				return false;
			}

			return typeEquals(
				((GenericArrayType) t1).getGenericComponentType(),
				((GenericArrayType) t2).getGenericComponentType()
			);
		}
		else if(t1 instanceof ParameterizedType)
		{
			if(! (t2 instanceof ParameterizedType))
			{
				return false;
			}

			ParameterizedType pt1 = (ParameterizedType) t1;
			ParameterizedType pt2 = (ParameterizedType) t2;

			return typeEqualsArray(pt1.getActualTypeArguments(), pt2.getActualTypeArguments())
				&& typeEquals(pt1.getOwnerType(), pt2.getOwnerType())
				&& typeEquals(pt1.getRawType(), pt2.getRawType());
		}
		else if(t1 instanceof TypeVariable)
		{
			if(! (t2 instanceof TypeVariable))
			{
				return false;
			}

			TypeVariable<?> tv1 = (TypeVariable<?>) t1;
			TypeVariable<?> tv2 = (TypeVariable<?>) t2;

			if(! Objects.equals(tv1.getName(), tv2.getName()))
			{
				return false;
			}

			GenericDeclaration gd1 =  tv1.getGenericDeclaration();
			GenericDeclaration gd2 =  tv2.getGenericDeclaration();
			if(gd1 instanceof Type)
			{
				if(! (gd2 instanceof Type))
				{
					return false;
				}

				return typeEquals((Type) gd1, (Type) gd2);
			}
			else
			{
				return Objects.equals(gd1, gd2);
			}
		}
		else if(t1 instanceof WildcardType)
		{
			if(! (t2 instanceof WildcardType))
			{
				return false;
			}

			WildcardType wc1 = (WildcardType) t1;
			WildcardType wc2 = (WildcardType) t2;

			return typeEqualsArray(wc1.getLowerBounds(), wc2.getLowerBounds())
				&& typeEqualsArray(wc2.getUpperBounds(), wc2.getUpperBounds());
		}
		else
		{
			return t1.equals(t2);
		}
	}

	/**
	 * Check if two arrays contain the same types.
	 */
	private static boolean typeEqualsArray(Type[] types1, Type[] types2)
	{
		if(types1.length != types2.length)
		{
			return false;
		}

		for(int i=0, n=types1.length; i<n; i++)
		{
			if(! typeEquals(types1[i], types2[i]))
			{
				return false;
			}
		}

		return true;
	}
}
