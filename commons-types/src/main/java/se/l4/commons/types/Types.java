package se.l4.commons.types;

import java.lang.reflect.Type;
import java.util.function.Predicate;

import com.fasterxml.classmate.AnnotationConfiguration;
import com.fasterxml.classmate.AnnotationOverrides;
import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.types.ResolvedArrayType;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import se.l4.commons.types.internal.TypeHierarchy;
import se.l4.commons.types.internal.reflect.TypeHelperImpl;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Helpers and utilities related to working with types. This class provides
 * static methods to turn types into {@link ResolvedType}s, provided by
 * the Classmate {@link TypeResolver}.
 */
public class Types
{
	private static final TypeResolver typeResolver = new TypeResolver();
	private static final MemberResolver memberResolver = new MemberResolver(typeResolver);

	private Types()
	{
	}

	@NonNull
	public static TypeRef reference(@NonNull Type type, @NonNull Type... typeParameters)
	{
		return TypeHelperImpl.reference(type, typeParameters);
	}

	/**
	 * Resolve the given base type using the specified types as type
	 * parameters.
	 *
	 * @param type
	 *   the base type to resolve
	 * @param typeParameters
	 *   the type parameters
	 * @return
	 *   resolve type instance
	 */
	@NonNull
	public static ResolvedType resolve(@NonNull Type type, @NonNull Type... typeParameters)
	{
		return typeResolver.resolve(type, typeParameters);
	}

	/**
	 * Resolve an array type of the given type.
	 *
	 * @param type
	 *   the type of the array
	 * @return
	 *   resolve type instance
	 */
	@NonNull
	public static ResolvedArrayType resolveArrayType(@NonNull Type elementType)
	{
		return typeResolver.arrayType(elementType);
	}

	/**
	 * Resolve a sub type of the given super type.
	 *
	 * @param superType
	 *   the super type
	 * @param subType
	 *   the sub type to resolve
	 * @return
	 *   resolved type
	 * @see TypeResolver#resolveSubtype(ResolvedType, Class)
	 */
	@NonNull
	public static ResolvedType resolveSubtype(@NonNull ResolvedType superType, @NonNull Class<?> subType)
	{
		return typeResolver.resolveSubtype(superType, subType);
	}

	/**
	 * Resolve the given base type and its members.
	 *
	 * @param type
	 *   the base type to resolve
	 * @param typeParameters
	 *   the type parameters
	 * @return
	 *   resolve type instance
	 */
	@NonNull
	public static ResolvedTypeWithMembers resolveMembers(@NonNull Type type, @NonNull Type... typeParameters)
	{
		ResolvedType rt = typeResolver.resolve(type, typeParameters);
		return memberResolver.resolve(rt, null, null);
	}

	/**
	 * Resolve the members of the given type.
	 *
	 * @param mainType
	 *   the main type to resolve members for
	 * @return
	 *   resolved type with members
	 */
	@NonNull
	public static ResolvedTypeWithMembers resolveMembers(@NonNull ResolvedType mainType)
	{
		return memberResolver.resolve(mainType, null, null);
	}

	/**
	 * Resolve the members of the given type, using a specific configuration
	 * for annotations.
	 *
	 * @param mainType
	 *   the main type
	 * @param annotationConfig
	 *   configuration for annotation types
	 * @param annotaitonOverrides
	 *   custom annotation overrides to use
	 * @return
	 *   resolved type with members
	 */
	@NonNull
	public static ResolvedTypeWithMembers resolveMembers(
		@NonNull ResolvedType mainType,
		@Nullable AnnotationConfiguration annotationConfig,
		@Nullable AnnotationOverrides annotationOverrides
	)
	{
		return memberResolver.resolve(mainType, annotationConfig, annotationOverrides);
	}

	/**
	 * Visit the hierarchy of the specified type. Will visit all interfaces,
	 * directly or indirectly present, and the superclasses.
	 *
	 * @param visitor
	 *   the visitor to apply to each item, should return {@code true} to
	 *   continue visiting other parts of the hierarchy and {@code false} to
	 *   abort the visiting
	 */
	public void visitHierarchy(Class<?> type, Predicate<Class<?>> visitor)
	{
		TypeHierarchy.visitHierarchy(type, visitor);
	}
}
