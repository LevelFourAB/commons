package se.l4.commons.types.reflect;

/**
 * Reference to a member of a {@link TypeRef}, such as a {@link FieldRef}
 * or {@link MethodRef}.
 */
public interface MemberRef
	extends Annotated
{
	/**
	 * Get the declaring type.
	 *
	 * @return
	 */
	TypeRef getDeclaringType();

	/**
	 * Get the name of the member.
	 */
	String getName();
}
