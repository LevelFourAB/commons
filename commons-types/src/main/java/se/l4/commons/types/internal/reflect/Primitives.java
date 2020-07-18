package se.l4.commons.types.internal.reflect;

import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.ImmutableMap;

/**
 * Helper for wrapping and unwrapping primitives.
 */
public class Primitives
{
	private static final ImmutableMap<Class<?>, Class<?>> PRIM_TO_WRAP =
		Maps.mutable.<Class<?>, Class<?>>empty()
			.withKeyValue(byte.class, Byte.class)
			.withKeyValue(short.class, Short.class)
			.withKeyValue(int.class, Integer.class)
			.withKeyValue(long.class, Long.class)
			.withKeyValue(float.class, Float.class)
			.withKeyValue(double.class, Double.class)
			.withKeyValue(char.class, Character.class)
			.withKeyValue(boolean.class, Boolean.class)
			.toImmutable();

	private static final ImmutableMap<Class<?>, Class<?>> WRAP_TO_PRIM = PRIM_TO_WRAP.flipUniqueValues();

	private Primitives()
	{
	}

	public static Class<?> wrap(Class<?> type)
	{
		return type.isPrimitive() ? PRIM_TO_WRAP.get(type) : type;
	}

	public static Class<?> unwrap(Class<?> type)
	{
		return WRAP_TO_PRIM.getIfAbsentValue(type, type);
	}
}
