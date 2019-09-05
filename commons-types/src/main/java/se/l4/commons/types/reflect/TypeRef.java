package se.l4.commons.types.reflect;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Reference to a {@link Type} or {@link AnnotatedType} to help simplify
 * working with generics.
 */
public interface TypeRef
	extends Annotated, Modifiers
{
	/**
	 * Get the underlying type.
	 */
	Type getType();

	/**
	 * Get the full type name of this reference, including any generic types.
	 *
	 * @return
	 */
	@NonNull
	String toTypeName();

	/**
	 * Get a type description for this type, including information about the
	 * usage of the type.
	 */
	@NonNull
	String toTypeDescription();

	/**
	 * Get information about how this type was used. Contains information about
	 * annotations present at the location the type was resolved from.
	 *
	 * @return
	 */
	@NonNull
	TypeUsage getUsage();

	/**
	 * Get this type but without usage information.
	 *
	 * @return
	 */
	@NonNull
	TypeRef withoutUsage();

	/**
	 * Get this type with usage from the specified instance.
	 *
	 * @param usage
	 * @return
	 */
	@NonNull
	TypeRef withUsage(@NonNull TypeUsage usage);

	/**
	 * Merge with usage from the specified instance.
	 *
	 * @param usage
	 * @return
	 */
	@NonNull
	TypeRef mergeWithUsage(@NonNull TypeUsage usage);

	/**
	 * Get if this reference is resolved. A reference is resolved if the
	 * actual type is known, which is when the underlying type is a
	 * {@link Class}, a {@link java.lang.reflect.GenericArrayType}
	 * or a {@link ParameterizedType}.
	 *
	 * Use {@link #isFullyResolved()} to check if all the type parameters are
	 * known.
	 */
	boolean isResolved();

	/**
	 * Get if this reference and all of its type arguments are resolved.
	 * Similar to {@link #isResolved()} but also checks that any type
	 * parameters are resolved.
	 */
	boolean isFullyResolved();

	/**
	 * Get the parameters as raw {@link TypeVariable}s.
	 *
	 * @return
	 *   list of type variables
	 */
	@NonNull
	List<TypeVariable<?>> getTypeVariables();

	/**
	 * Get the number of type parameters available in this type.
	 *
	 * @return
	 */
	int getTypeParameterCount();

	/**
	 * Get all of the parameter names that are available for this type.
	 */
	@NonNull
	List<String> getTypeParameterNames();

	/**
	 * Get the type parameters. If this type is {@link #isFullyResolved()}
	 * all of these will point to a resolved type.
	 *
	 * @return
	 */
	@NonNull
	List<TypeRef> getTypeParameters();

	/**
	 * Get a type parameter using an index.
	 *
	 * @param index
	 * @return
	 */
	@NonNull
	Optional<TypeRef> getTypeParameter(int index);

	/**
	 * Get a type parameter using a name.
	 *
	 * @param name
	 * @return
	 */
	@NonNull
	Optional<TypeRef> getTypeParameter(@NonNull String name);

	/**
	 * Get a type where the specified type parameter has been changed to the
	 * specified type.
	 *
	 * @param index
	 *   index of the parameter to change
	 * @param type
	 *   the new type for the parameter
	 * @return
	 *   optional containing this type with the new parameter. If the optional
	 *   is empty the parameter either does not exist or the type is not
	 *   assignable
	 */
	@NonNull
	Optional<TypeRef> withTypeParameter(int index, @NonNull TypeRef type);

	/**
	 * Get a type where the specified type parameter has been changed to the
	 * specified type.
	 *
	 * @param name
	 *   the name of the parameter to update
	 * @param type
	 *   the new type for the parameter
	 * @return
	 *   optional containing this type with the new parameter. If the optional
	 *   is empty the parameter either does not exist or the type is not
	 *   assignable
	 */
	@NonNull
	Optional<TypeRef> withTypeParameter(@NonNull String name, @NonNull TypeRef type);

	/**
	 * Get a {@link TypeInferrer} that can infer how a type parameter is used
	 * in a concrete type. This method takes a pattern type where the type
	 * variable may be used, when inferring another this is matched against
	 * the pattern and the usage of the type variable is calculated.
	 *
	 * <p>
	 * Type inferring is commonly used for scenarios where type conversion is
	 * needed. It helps where you have signatures like:
	 *
	 * <p>
	 * <pre>
	 * class Example<T> implements Conversion<List<T>, Set<T>> {
	 * }
	 * </pre>
	 *
	 * <p>
	 * In which case a type inferrer can be used to extract how {@code T} is
	 * used in {@code Example<T>}:
	 *
	 * <p>
	 * <pre>
	 * TypeRef type = Types.reference(Example.class);
	 * TypeRef conversionType = type.findInterface(Conversion.class).get();
	 * TypeRef inType = type.getTypeParameter(0).get();
	 *
	 * TypeInferrer inferrer = type.getTypeParameterUsageInferrer(0, inType);
	 *
	 * // Infer that String is used as T
	 * Optional<TypeRef> ref = inferrer.infer(Types.reference(List.class, String.class));
	 * </pre>
	 *
	 * @param index
	 *   the index of the type variable
	 * @param patternType
	 *   the pattern type
	 * @return
	 */
	TypeInferrer getTypeParameterUsageInferrer(int index, @NonNull TypeRef patternType);

	/**
	 * Get a {@link TypeInferrer} that can infer how a type parameter is used
	 * in a concrete type. See {@link #getTypeParameterUsageInferrer(int, TypeRef)}
	 * for details.
	 *
	 * @param name
	 *   the name of the type variable
	 * @param patternType
	 *   the pattern type
	 * @return
	 */
	TypeInferrer getTypeParameterUsageInferrer(String name, @NonNull TypeRef patternType);

	/**
	 * Get a {@link TypeInferrer} that can modify the given pattern type using
	 * the type parameters of this type. This inferrer needs all of the type
	 * parameters as its input to resolve a type.
	 *
	 * <p>
	 * <pre>
	 * class Example<T> implements Conversion<List<T>, Set<T>> {
	 * }
	 * </pre>
	 *
	 * <p>
	 * <pre>
	 * TypeRef type = Types.reference(Example.class);
	 * TypeRef conversionType = type.findInterface(Conversion.class).get();
	 * TypeRef outType = type.getTypeParameter(1).get();
	 *
	 * TypeInferrer inferrer = type.getTypeParameterInferrer(outType);
	 *
	 * // Get a Set with T bound to String
	 * Optional<TypeRef> ref = inferrer.infer(Types.reference(String.class));
	 * </pre>
	 *
	 * @param patternType
	 * @return
	 */
	TypeInferrer getTypeParameterInferrer(@NonNull TypeRef patternType);

	/**
	 * Get if this type is abstract.
	 *
	 * @return
	 */
	boolean isAbstract();

	/**
	 * Get if this type is static. Interfaces and classes may be static when
	 * they are declared inside another class.
	 *
	 * @return
	 */
	boolean isStatic();

	/**
	 * Get if the type is final.
	 *
	 * @return
	 */
	boolean isFinal();

	/**
	 * Get if this type uses strict floating point math.
	 */
	boolean isStrict();

	/**
	 * Get if this type is an interface.
	 *
	 * @return
	 */
	boolean isInterface();

	/**
	 * Get if this type is an array.
	 *
	 * @return
	 */
	boolean isArray();

	/**
	 * Get if this type is an enumeration.
	 *
	 * @return
	 */
	boolean isEnum();

	/**
	 * Get if this type is an annotation.
	 *
	 * @return
	 */
	boolean isAnnotation();

	/**
	 * Get if this type is an anonymous class.
	 *
	 * @return
	 */
	boolean isAnonymousClass();

	/**
	 * Get if this type is a local class.
	 *
	 * @return
	 */
	boolean isLocalClass();

	/**
	 * Get if this type is a member class.
	 *
	 * @return
	 */
	boolean isMemberClass();

	/**
	 * Get if this type is a primitive class.
	 *
	 * @return
	 */
	boolean isPrimitive();

	/**
	 * Get if this type is a synthetic class.
	 *
	 * @return
	 */
	boolean isSynthetic();

	/**
	 * Get if this type is assignable from another type.
	 *
	 * @param other
	 * @return
	 */
	boolean isAssignableFrom(TypeRef other);

	/**
	 * Get the {@link Class} this type is when type information has been erased.
	 *
	 * @return
	 */
	@NonNull
	Class<?> getErasedType();

	/**
	 * Get the super class of this type.
	 *
	 * @return
	 */
	@NonNull
	Optional<TypeRef> getSuperclass();

	/**
	 * Find a super class of this type. This will look up the hierarchy and
	 * try to find the given superclass.
	 *
	 * @param superclass
	 * @return
	 */
	@NonNull
	Optional<TypeRef> findSuperclass(@NonNull Class<?> superclass);

	/**
	 * If the type refers to an array this will return the element type of
	 * the array.
	 *
	 * @return
	 */
	@NonNull
	Optional<TypeRef> getComponentType();

	/**
	 * Get the interfaces that the type directly implements.
	 *
	 * @return
	 */
	@NonNull
	List<TypeRef> getInterfaces();

	/**
	 * Find an interface that this type directly implements.
	 *
	 * @return
	 */
	@NonNull
	Optional<TypeRef> getInterface(@NonNull Class<?> type);

	/**
	 * Perform a search for the specified interface.
	 *
	 * @param type
	 * @return
	 */
	@NonNull
	Optional<TypeRef> findInterface(@NonNull Class<?> type);

	/**
	 * Perform a search for the given interface or superclass.
	 */
	@NonNull
	Optional<TypeRef> findSuperclassOrInterface(@NonNull Class<?> type);

	/**
	 * Perform a search over this type hierarchy, returning the value of the
	 * first optional that is not empty.
	 *
	 * @param <T>
	 * @param finder
	 * @return
	 */
	@NonNull
	<T> Optional<T> find(@NonNull Function<TypeRef, Optional<T>> finder);

	/**
	 * Visit the type hierarchy of this type. Will visit superclasses and
	 * superinterfaces in a breadth-first fashion. If the visitor returns
	 * {@code false} the visit will abort.
	 *
	 * @param visitor
	 */
	void visitHierarchy(Predicate<TypeRef> visitor);

	/**
	 * Get the public fields in this type.
	 *
	 * @return
	 */
	@NonNull
	List<FieldRef> getFields();

	/**
	 * Get a public field with the given name.
	 *
	 * @param name
	 * @return
	 */
	@NonNull
	Optional<FieldRef> getField(@NonNull String name);

	/**
	 * Get all the public methods in this type.
	 *
	 * @return
	 */
	@NonNull
	List<MethodRef> getMethods();

	/**
	 * Get a public method with the given name and parameter types.
	 *
	 * @param name
	 * @param parameterTypes
	 * @return
	 */
	@NonNull
	Optional<MethodRef> getMethodViaClassParameters(@NonNull String name, @NonNull Class<?>... parameterTypes);

	/**
	 * Get a public method with the given name and parameter types. This
	 * method will use the {@link TypeRef#getErasedType()} to find the method.
	 *
	 * @param name
	 * @param parameterTypes
	 * @return
	 */
	@NonNull
	Optional<MethodRef> getMethod(@NonNull String name, @NonNull TypeRef... parameterTypes);

	/**
	 * Get all the public constructors in this type.
	 *
	 * @return
	 */
	@NonNull
	List<ConstructorRef> getConstructors();

	/**
	 * Get a public constructor with the given parameter types.
	 *
	 * @param name
	 * @param parameterTypes
	 * @return
	 */
	@NonNull
	Optional<ConstructorRef> getConstructorViaClassParameters(@NonNull Class<?>... parameterTypes);

	/**
	 * Get a public constructor with the given parameter types. This method
	 * will use the {@link TypeRef#getErasedType()} to find the constructor.
	 *
	 * @param name
	 * @param parameterTypes
	 * @return
	 */
	@NonNull
	Optional<ConstructorRef> getConstructor(@NonNull TypeRef... parameterTypes);

	/**
	 * Get the all fields declared by this type.
	 *
	 * @return
	 */
	@NonNull
	List<FieldRef> getDeclaredFields();

	/**
	 * Get a specific field declared by this type.
	 *
	 * @param name
	 * @return
	 */
	@NonNull
	Optional<FieldRef> getDeclaredField(@NonNull String name);

	/**
	 * Get all methods declared by this type.
	 *
	 * @return
	 */
	@NonNull
	List<MethodRef> getDeclaredMethods();

	/**
	 * Get a specific method declared by this type with the given name and
	 * parameter types.
	 *
	 * @param name
	 * @param parameterTypes
	 * @return
	 */
	@NonNull
	Optional<MethodRef> getDeclaredMethodViaClassParameters(@NonNull String name, @NonNull Class<?>... parameterTypes);

	/**
	 * Get a specific method declared by this type with the given name and
	 * parameter types. This method will use the {@link TypeRef#getErasedType()}
	 * to find the method.
	 *
	 * @param name
	 * @param parameterTypes
	 * @return
	 */
	@NonNull
	Optional<MethodRef> getDeclaredMethod(@NonNull String name, @NonNull TypeRef... parameterTypes);

	/**
	 * Get all the constructors declared by this type.
	 *
	 * @return
	 */
	@NonNull
	List<ConstructorRef> getDeclaredConstructors();

	/**
	 * Get a constructor declared by this this type with the given type
	 * parameters.
	 *
	 * @param name
	 * @param parameterTypes
	 * @return
	 */
	@NonNull
	Optional<ConstructorRef> getDeclaredConstructorViaClassParameters(@NonNull Class<?>... parameterTypes);

	/**
	 * Get a specific constructor declared by this type with the given
	 * parameter types. This method will use the {@link TypeRef#getErasedType()}
	 * to find the constructor.
	 *
	 * @param name
	 * @param parameterTypes
	 * @return
	 */
	@NonNull
	Optional<ConstructorRef> getDeclaredConstructor(@NonNull TypeRef... parameterTypes);
}
