package se.l4.commons.types;

import java.lang.reflect.Type;
import java.util.function.Predicate;

import edu.umd.cs.findbugs.annotations.NonNull;
import se.l4.commons.types.internal.TypeHierarchy;
import se.l4.commons.types.internal.reflect.TypeHelperImpl;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Helpers and utilities related to working with types.
 */
public class Types
{
	private Types()
	{
	}

	@NonNull
	public static TypeRef reference(@NonNull Type type, @NonNull Type... typeParameters)
	{
		return TypeHelperImpl.reference(type, typeParameters);
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
	public static void visitHierarchy(Class<?> type, Predicate<Class<?>> visitor)
	{
		TypeHierarchy.visitHierarchy(type, visitor);
	}
}
