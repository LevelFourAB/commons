package se.l4.commons.types.proxies;

import java.util.Optional;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Resolver that receives a {@link MethodEncounter} and is expected to resolve
 * a {@link MethodInvoker} or return an empty optional.
 */
@FunctionalInterface
public interface MethodResolver<T>
{
	/**
	 * Resolve an invocation handler for the given {@link MethodEncounter}.
	 *
	 * @param encounter
	 *	 the encounter with the method, contains information about things
	 *	 such as the name, return type, parameters and annotations of the
	 *	 method
	 * @return
	 *	 optional with invocation handler if this resolver wants to handle
	 *	 the invocation of the method, empty optional otherwise
	 */
	@NonNull
	Optional<MethodInvocationHandler<T>> create(@NonNull MethodEncounter encounter);
}
