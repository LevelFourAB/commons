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

	private static final ImmutableMap<Class<?>, Object> DEFAULTS =
		Maps.mutable.<Class<?>, Object>empty()
			.withKeyValue(byte.class, Byte.valueOf((byte) 0))
			.withKeyValue(short.class, Short.valueOf((short) 0))
			.withKeyValue(int.class, Integer.valueOf(0))
			.withKeyValue(long.class, Long.valueOf(0))
			.withKeyValue(float.class, Float.valueOf(0f))
			.withKeyValue(double.class, Double.valueOf(0d))
			.withKeyValue(char.class, Character.valueOf('\0'))
			.withKeyValue(boolean.class, Boolean.FALSE)
			.toImmutable();

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

	public static Object defaultValue(Class<?> type)
	{
		return DEFAULTS.get(type);
	}
}
