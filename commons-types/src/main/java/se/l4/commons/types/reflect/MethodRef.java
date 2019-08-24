package se.l4.commons.types.reflect;

import java.lang.reflect.Method;

/**
 * Reference to a {@link Method}. Method references are retrieved via
 * {@link TypeRef} and help with resolving generics.
 */
public interface MethodRef
	extends ExecutableRef
{
	/**
	 * Get the method this reference points to.
	 */
	Method getMethod();

	/**
	 * Get if this method is abstract.
	 *
	 * @return
	 */
	boolean isAbstract();

	/**
	 * Get if this method is static.
	 *
	 * @return
	 */
	boolean isStatic();

	/**
	 * Get if this method is final.
	 *
	 * @return
	 */
	boolean isFinal();

	/**
	 * Get if this method is synchronized.
	 *
	 * @return
	 */
	boolean isSynchronized();

	/**
	 * Get if this method is native.
	 *
	 * @return
	 */
	boolean isNative();

	/**
	 * Get if this method is strict.
	 *
	 * @return
	 */
	boolean isStrict();

	/**
	 * Get if this is a bridge method.
	 *
	 * @return
	 */
	boolean isBridge();

	/**
	 * Get if this is a default method.
	 *
	 * @return
	 */
	boolean isDefault();
}
