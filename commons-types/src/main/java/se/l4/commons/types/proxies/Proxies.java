package se.l4.commons.types.proxies;

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
	 */
	<ContextType> ExtendedTypeBuilder<ContextType> newExtendedType(Class<ContextType> type);
}
