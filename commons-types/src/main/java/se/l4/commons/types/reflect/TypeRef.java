package se.l4.commons.types.reflect;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Reference to a {@link Type} or {@link AnnotatedType} to help simplify
 * working with generics.
 */
public interface TypeRef
	extends Annotated
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
	TypeRef withUsage(TypeUsage usage);

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
	Optional<TypeRef> getTypeParameter(String name);

	/**
	 * Get if this type is an interface.
	 *
	 * @return
	 */
	boolean isInterface();

	/**
	 * Get if this type is an array.
	 */
	boolean isArray();

	/**
	 * Get if this type is an enumeration.
	 */
	boolean isEnum();

	/**
	 * Get if this type is an annotation.
	 */
	boolean isAnnotation();

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
	 * Perform a search over this type hierarchy, returning the value of the
	 * first optional that is not empty.
	 *
	 * @param <T>
	 * @param finder
	 * @return
	 */
	<T> Optional<T> find(@NonNull Function<TypeRef, Optional<T>> finder);
}
