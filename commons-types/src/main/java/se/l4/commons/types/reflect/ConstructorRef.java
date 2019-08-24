package se.l4.commons.types.reflect;

import java.lang.reflect.Constructor;

/**
 * Reference to a {@link Constructor}.
 */
public interface ConstructorRef
	extends ExecutableRef
{
	/**
	 * Get the constructor this is a reference to.
	 *
	 * @return
	 */
	Constructor<?> getConstructor();
}
