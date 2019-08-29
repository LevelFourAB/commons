package se.l4.commons.types.reflect;

import java.lang.reflect.Modifier;

public interface Modifiers
{
	/**
	 * Get the modifiers of this item.
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
