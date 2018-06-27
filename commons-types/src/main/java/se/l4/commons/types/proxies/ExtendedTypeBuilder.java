package se.l4.commons.types.proxies;

import java.util.function.Function;

/**
 * Builder for extending types with custom invocation handlers for methods.
 * This can be used to resolve
 */
public interface ExtendedTypeBuilder<ContextType>
{
	/**
	 * Add a new {@link MethodResolver method resolver} that is used to
	 * resolve {@link MethodInvoker invokers} for methods.
	 *
	 * @param resolver
	 *	 resolver to use
	 * @return
	 *	 self
	 */
	ExtendedTypeBuilder<ContextType> with(MethodResolver<ContextType> resolver);

	/**
	 * Create an instance of {@link ExtendedTypeCreator} that can be used to
	 * create function that construct actual object instances
	 *
	 * @return
	 *	 creator for the resolvers in this type
	 */
	ExtendedTypeCreator<ContextType> toCreator();

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
