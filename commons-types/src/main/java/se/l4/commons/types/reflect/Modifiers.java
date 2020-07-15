package se.l4.commons.types.reflect;

import java.lang.reflect.Modifier;

/**
 * Interface used for any element that has modifiers. Provides easier access
 */
public interface Modifiers
{
	/**
	 * Get the modifiers of this item. These modifiers can also be checked via
	 * methods. If using this directly {@link Modifier} provides static methods
	 * that can be used to make sense of this value.
	 */
	int getModifiers();

	/**
	 * Get if this item is public.
	 *
	 * @return
	 */
	default boolean isPublic()
	{
		return Modifier.isPublic(getModifiers());
	}

	/**
	 * Get if this item is protected.
	 *
	 * @return
	 */
	default boolean isProtected()
	{
		return Modifier.isProtected(getModifiers());
	}

	/**
	 * Get if this item is private.
	 *
	 * @return
	 */
	default boolean isPrivate()
	{
		return Modifier.isPrivate(getModifiers());
	}
}
