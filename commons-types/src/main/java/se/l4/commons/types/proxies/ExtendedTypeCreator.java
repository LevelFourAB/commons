package se.l4.commons.types.proxies;

import java.util.function.Function;

/**
 * Creator of functions that creates instances of a certain type. Instances
 * of this method can be created via a {@link ExtendedTypeBuilder}.
 *
 * @param <ContextType>
 *	 the type of context that the built instances require to function
 */
public interface ExtendedTypeCreator<ContextType>
{
	/**
	 * Create a function that creates instances of the given interface or
	 * abstract class.
	 *
	 * @param interfaceOrAbstractClass
	 *	 the interface or abstract class to create a function for
	 * @return
	 *	 the function that creates instances
	 * @throws ProxyException
	 *	 if the type is not an interface or not abstract
	 */
	<I> Function<ContextType, I> create(Class<I> interfaceOrAbstractClass);
}
