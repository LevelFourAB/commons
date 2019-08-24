package se.l4.commons.types.reflect;

import java.lang.reflect.Field;

/**
 * Reference to a {@link Field}. Field references are obtained using
 * {@link TypeRef} and helps with resolving generics.
 */
public interface FieldRef
	extends MemberRef
{
	/**
	 * Get the underlying field.
	 *
	 * @return
	 */
	Field getField();

	/**
	 * Get the type of the field.
	 */
	TypeRef getType();

	/**
	 * Get the name of the field.
	 *
	 * @return
	 */
	String getName();

	/**
	 * Get if this field is static.
	 *
	 * @return
	 */
	boolean isStatic();

	/**
	 * Get if this field is final.
	 *
	 * @return
	 */
	boolean isFinal();

	/**
	 * Get if this field is transient.
	 *
	 * @return
	 */
	boolean isTransient();

	/**
	 * Get if this field is volatile.
	 *
	 * @return
	 */
	boolean isVolatile();

}
