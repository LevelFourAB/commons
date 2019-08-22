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
}
