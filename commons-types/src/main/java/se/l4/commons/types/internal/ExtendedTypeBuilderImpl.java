package se.l4.commons.types.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.members.ResolvedMethod;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.util.proxy.RuntimeSupport;
import se.l4.commons.types.Types;
import se.l4.commons.types.proxies.ExtendedTypeBuilder;
import se.l4.commons.types.proxies.ExtendedTypeCreator;
import se.l4.commons.types.proxies.MethodEncounter;
import se.l4.commons.types.proxies.MethodInvocationHandler;
import se.l4.commons.types.proxies.MethodResolver;
import se.l4.commons.types.proxies.ProxyException;

/**
 * Implementation of {@link ExtendedTypeBuilder} that uses JavaAssist to
 * generate classes at runtime.
 */
public class ExtendedTypeBuilderImpl<ContextType>
	implements ExtendedTypeBuilder<ContextType>
{
	private static final AtomicInteger compiled = new AtomicInteger();

	private Class<ContextType> contextType;
	private List<MethodResolver<ContextType>> resolvers;

	public ExtendedTypeBuilderImpl(Class<ContextType> contextType)
	{
		this.contextType = contextType;
		resolvers = new ArrayList<>();
	}

	@Override
	public ExtendedTypeBuilder<ContextType> with(MethodResolver<ContextType> resolver)
	{
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
		// Resolve generics
		ResolvedTypeWithMembers withMembers = Types.resolveMembers(typeToExtend);

		Map<Method, MethodInvocationHandler<CT>> handlers = new HashMap<>();
		try
		{
			// Go through methods and create invokers for each of them
			for(ResolvedMethod method : withMembers.getMemberMethods())
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
						handlers.put(method.getRawMember(), opt.get());
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
		ClassPool pool = ClassPool.getDefault();
		try
		{
			CtClass exprIf = pool.get(typeToExtend.getName());

			CtClass type = pool.makeClass(typeToExtend.getName() + "$$Proxy$$" + compiled.incrementAndGet());
			type.addInterface(exprIf);

			CtClass object = pool.get("java.lang.Object");
			CtClass invokerArrayType = pool.get(MethodInvocationHandler.class.getName() + "[]");

			CtField field = new CtField(invokerArrayType, "invokers", type);
			type.addField(field);

			field = new CtField(object, "ctx", type);
			type.addField(field);

			CtConstructor constructor = CtNewConstructor.make(new CtClass[] { invokerArrayType, object }, new CtClass[0], type);
			if(typeToExtend.isInterface())
			{
				constructor.setBody("{ this.invokers = $1; this.ctx = $2; }");
			}
			else
			{
				// TODO: This needs to check that the abstract class has a compatible constructor
				constructor.setBody("{ super($2); this.invokers = $1; this.ctx = $2; }");
			}
			type.addConstructor(constructor);

			// Create the invoker methods
			MethodInvocationHandler<CT>[] invokerArray = new MethodInvocationHandler[invokers.size()];
			int current = 0;
			for(CtMethod method : exprIf.getMethods())
			{
				if(method.getDeclaringClass() != exprIf) continue;
				Method reflectMethod = RuntimeSupport.findMethod(typeToExtend, method.getName(), method.getSignature());

				invokerArray[current] = invokers.get(reflectMethod);

				CtMethod invokingMethod = CtNewMethod.copy(method, type, null);
				invokingMethod.setBody("{ " + (reflectMethod.getReturnType() == void.class ? "" : "return ($r)")
					+ " this.invokers[" + current + "].handle(this.ctx, $args); }");

				type.addMethod(invokingMethod);
				current++;
			}

			Class createdClass = type.toClass();
			return createFactory(pool, createdClass, invokerArray);
		}
		catch(Throwable e)
		{
			if(e instanceof RuntimeException) throw (RuntimeException) e;
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <CT, I> Function<CT, I> createFactory(ClassPool pool, Class createdClass, MethodInvocationHandler<CT>[] invokerArray)
		throws NoSuchMethodException, SecurityException
	{
		Constructor ctr = createdClass.getConstructor(MethodInvocationHandler[].class, Object.class);
		return new Function<CT, I>() {
			@Override
			public I apply(CT input)
			{
				try
				{
					return (I) ctr.newInstance(invokerArray, input);
				}
				catch(Exception e)
				{
					if(e instanceof RuntimeException) throw (RuntimeException) e;
					throw new RuntimeException(e);
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
		private final ResolvedMethod method;
		private Annotation[][] parameterAnnotations;

		public MethodEncounterImpl(ResolvedMethod method)
		{
			this.method = method;
			parameterAnnotations = method.getRawMember().getParameterAnnotations();
		}

		@Override
		public Method getMethod()
		{
			return method.getRawMember();
		}

		@Override
		public String getName()
		{
			return method.getName();
		}

		@Override
		public ResolvedType getReturnType()
		{
			return method.getReturnType();
		}

		@Override
		public boolean hasAnnotation(Class<? extends Annotation> annotation)
		{
			return method.getRawMember().getAnnotation(annotation) != null;
		}

		@Override
		public <T extends Annotation> T getAnnotation(Class<T> annotation)
		{
			return method.getRawMember().getAnnotation(annotation);
		}

		@Override
		public int getArgumentCount()
		{
			return method.getArgumentCount();
		}

		@Override
		public ResolvedType getArgumentType(int argument)
		{
			return method.getArgumentType(argument);
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T extends Annotation> T findArgumentAnnotation(int argument, Class<T> type)
		{
			for(Annotation a : parameterAnnotations[argument])
			{
				if(a.annotationType() == type)
				{
					return (T) a;
				}
			}

			return null;
		}

		@Override
		public Annotation[] getAnnotations()
		{
			return method.getRawMember().getAnnotations();
		}

	}
}
