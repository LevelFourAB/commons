package se.l4.commons.types.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.FieldValue;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.matcher.ElementMatchers;
import se.l4.commons.types.Types;
import se.l4.commons.types.proxies.ExtendedTypeBuilder;
import se.l4.commons.types.proxies.ExtendedTypeCreator;
import se.l4.commons.types.proxies.MethodEncounter;
import se.l4.commons.types.proxies.MethodInvocationHandler;
import se.l4.commons.types.proxies.MethodResolver;
import se.l4.commons.types.proxies.NonPublicByteBuddyRunner;
import se.l4.commons.types.proxies.ProxyException;
import se.l4.commons.types.reflect.MethodRef;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Implementation of {@link ExtendedTypeBuilder} that uses JavaAssist to
 * generate classes at runtime.
 */
public class ExtendedTypeBuilderImpl<ContextType>
	implements ExtendedTypeBuilder<ContextType>
{
	private Class<ContextType> contextType;
	private List<MethodResolver<ContextType>> resolvers;

	public ExtendedTypeBuilderImpl(Class<ContextType> contextType)
	{
		Objects.requireNonNull(contextType);

		this.contextType = contextType;
		resolvers = new ArrayList<>();
	}

	@Override
	public ExtendedTypeBuilder<ContextType> with(MethodResolver<ContextType> resolver)
	{
		Objects.requireNonNull(contextType);

		resolvers.add(resolver);
		return this;
	}

	@Override
	public <I> Function<ContextType, I> create(Class<I> interfaceOrAbstractClass)
	{
		return resolveAndExtend(contextType, resolvers, interfaceOrAbstractClass);
	}

	@Override
	public ExtendedTypeCreator<ContextType> toCreator()
	{
		return new CreatorImpl<>(contextType, resolvers);
	}

	/**
	 * Internal helper that resolves all the invokers using the given resolvers.
	 *
	 * @param contextType
	 *	 the type of context the invokers use
	 * @param resolvers
	 *	 list of resolvers
	 * @param typeToExtend
	 *	 interface or abstract class to extend
	 */
	private static <CT, I> Function<CT, I> resolveAndExtend(
		Class<CT> contextType,
		List<MethodResolver<CT>> resolvers,
		Class<I> typeToExtend)
	{
		TypeRef type = Types.reference(typeToExtend);

		Map<Method, MethodInvocationHandler<CT>> handlers = new HashMap<>();
		try
		{
			// Go through methods and create invokers for each of them
			for(MethodRef method : type.getMethods())
			{
				// Only try to implement abstract methods
				if(! method.isAbstract()) continue;

				// Create an encounter and let the first method resolver that matches handle the method
				boolean foundInvoker = false;
				MethodEncounter encounter = new MethodEncounterImpl(method);
				for(MethodResolver<CT> resolver : resolvers)
				{
					Optional<MethodInvocationHandler<CT>> opt = resolver.create(encounter);
					if(opt.isPresent())
					{
						// This resolver created an invoker to use
						foundInvoker = true;
						handlers.put(method.getMethod(), opt.get());
						break;
					}
				}

				if(! foundInvoker)
				{
					// No invoker was found, can not handle the method
					throw new ProxyException("The method " + method.getName() + " could not be handled");
				}
			}
		}
		catch(ProxyException e)
		{
			throw new ProxyException(typeToExtend.getName() + ":\n" + e.getMessage());
		}
		catch(Exception e)
		{
			throw new ProxyException(typeToExtend.getName() + ":\n" + e.getMessage(), e);
		}

		return createFunction(contextType, typeToExtend, Collections.unmodifiableMap(handlers));
	}

	/**
	 * Create the actual function that creates instances.
	 *
	 * @param contextType
	 *	 the context type being used
	 * @param typeToExtend
	 *	 the type being extended
	 * @param invokers
	 *	 the resolved invokers
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <CT, I> Function<CT, I> createFunction(
		Class<CT> contextType,
		Class<I> typeToExtend,
		Map<Method, MethodInvocationHandler<CT>> invokers
	)
	{
		try
		{
			DynamicType.Builder builder = new ByteBuddy()
				.subclass(typeToExtend);

			// Add the context field
			builder = builder.defineField("context", contextType, Visibility.PRIVATE);

			// Add the constructor
			builder = builder.defineConstructor(Visibility.PUBLIC)
				.withParameter(contextType)
				.intercept(
					MethodCall.invoke(Object.class.getDeclaredConstructor())
						.onSuper()
						.andThen(FieldAccessor.ofField("context").setsArgumentAt(0))
				);

			// TODO: The constructor needs to support abstract base classes and injection via InstanceFactory

			for(Map.Entry<Method, MethodInvocationHandler<CT>> e : invokers.entrySet())
			{
				builder = builder.method(ElementMatchers.is(e.getKey()))
					.intercept(
						MethodDelegation.to(new NonPublicByteBuddyRunner(e.getValue()))
					);
			}

			Class createdClass = builder.make()
				.load(typeToExtend.getClassLoader())
				.getLoaded();

			return createFactory(createdClass, contextType);
		}
		catch(Throwable e)
		{
			if(e instanceof ProxyException) throw (ProxyException) e;
			throw new ProxyException(e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <CT, I> Function<CT, I> createFactory(Class<I> createdClass, Class<CT> contextClass)
		throws NoSuchMethodException, SecurityException
	{
		Constructor ctr = createdClass.getConstructor(contextClass);
		return new Function<CT, I>() {
			@Override
			public I apply(CT input)
			{
				try
				{
					return (I) ctr.newInstance(input);
				}
				catch(Exception e)
				{
					if(e instanceof ProxyException) throw (ProxyException) e;
					throw new ProxyException(e);
				}
			}
		};
	}

	private static class CreatorImpl<CT>
		implements ExtendedTypeCreator<CT>
	{
		private final Class<CT> contextType;
		private final List<MethodResolver<CT>> resolvers;

		public CreatorImpl(Class<CT> contextType, List<MethodResolver<CT>> resolvers)
		{
			this.contextType = contextType;
			this.resolvers = Collections.unmodifiableList(resolvers);
		}

		@Override
		public <I> Function<CT, I> create(Class<I> interfaceOrAbstractClass)
		{
			return resolveAndExtend(contextType, resolvers, interfaceOrAbstractClass);
		}

	}

	private static class MethodEncounterImpl
		implements MethodEncounter
	{
		private final MethodRef method;

		public MethodEncounterImpl(MethodRef method)
		{
			this.method = method;
		}

		@Override
		public MethodRef getMethod()
		{
			return method;
		}
	}
}
