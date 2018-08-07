package se.l4.commons.types.proxies;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Proxying support for extending interfaces and classes during runtime.
 */
public interface Proxies
{
	/**
	 * Start building a new extended type, an extended type may
	 * {@link MethodResolver resolve methods} in a type and create custom
	 * {@link MethodInvocationHandler method handlers} to extend certain
	 * method calls.
	 *
	 * @param type
	 *   the type of context data that the type will work on
	 */
	@NonNull
	<ContextType> ExtendedTypeBuilder<ContextType> newExtendedType(@NonNull Class<ContextType> type);
}
