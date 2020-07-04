package se.l4.commons.types.proxies;

import se.l4.commons.types.reflect.MethodRef;

/**
 * An encounter with a method, contains methods to help with creating a
 * {@link MethodInvoker}.
 *
 * @author Andreas Holstenson
 *
 */
public interface MethodEncounter
{
	/**
	 * Get the method this generation is for.
	 *
	 * @return
	 */
	MethodRef getMethod();
}
