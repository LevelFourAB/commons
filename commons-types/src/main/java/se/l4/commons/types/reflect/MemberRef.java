package se.l4.commons.types.reflect;

/**
 * Reference to a member of a {@link TypeRef}, such as a {@link FieldRef}
 * or {@link MethodRef}.
 */
public interface MemberRef
	extends Annotated, Modifiers
{
	/**
	 * Get the declaring type.
	 *
	 * @return
	 */
	TypeRef getDeclaringType();

	/**
	 * Get the name of the member.
	 *
	 * @return
	 */
	String getName();

	/**
	 * Get if this member is synthetic, in which case it was introduced by
	 * the compiler.
	 *
	 * @return
	 */
	boolean isSynthetic();
}
