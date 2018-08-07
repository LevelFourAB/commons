package se.l4.commons.types.proxies;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Handler for a method invocation in a proxied object as constructed via
 * {@link ExtendedTypeBuilder}.
 *
 * @param <ContextType>
 *	 the type of context this handler expects
 */
@FunctionalInterface
public interface MethodInvocationHandler<ContextType>
{
	/**
	 * Invoke this handler over the given context with the given arguments.
	 *
	 * @param self
	 *	 the context of the proxied object
	 * @param arguments
	 *	 the arguments of the method
	 * @return
	 *	 result that the method invocation should return
	 */
	@Nullable
	Object handle(@NonNull ContextType self, @NonNull Object[] arguments)
		throws Exception;
}
