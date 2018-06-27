package se.l4.commons.guice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.function.Supplier;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.internal.Annotations;

import se.l4.commons.types.InstanceException;
import se.l4.commons.types.InstanceFactory;

/**
 * Implementation of {@link InstanceFactory} that delegates all work to an
 * {@link Injector}.
 */
@Singleton
public class GuiceInstanceFactory
	implements InstanceFactory
{
	private final Injector injector;

	@Inject
	public GuiceInstanceFactory(Injector injector)
	{
		this.injector = injector;
	}

	@Override
	public <T> T create(Class<T> type)
	{
		return injector.getInstance(type);
	}

	@Override
	public <T> Supplier<T> supplier(Class<T> type)
	{
		Provider<T> provider = injector.getProvider(type);
		return provider::get;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <T> T create(Type type)
	{
		return (T) injector.getInstance(Key.get(type));
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	public <T> Supplier<T> supplier(Type type)
	{
		Provider<T> provider = injector.getProvider((Key<T>) Key.get(type));
		return provider::get;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T create(Type type, Annotation[] annotations)
	{
		Annotation bindingAnnotation = findBindingAnnotation(annotations);
		Key key = bindingAnnotation == null ? Key.get(type) : Key.get(type, bindingAnnotation);

		return (T) injector.getInstance(key);
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> Supplier<T> supplier(Type type, Annotation[] annotations)
	{
		Annotation bindingAnnotation = findBindingAnnotation(annotations);
		Key key = bindingAnnotation == null ? Key.get(type) : Key.get(type, bindingAnnotation);
		Provider<T> provider = injector.getProvider(key);

		return provider::get;
	}

	private Annotation findBindingAnnotation(Annotation[] annotations)
	{
		Annotation result = null;
		for(Annotation a : annotations)
		{
			if(Annotations.isBindingAnnotation(a.annotationType()))
			{
				if(result != null)
				{
					throw new InstanceException(
						"Duplicate binding annotations found; Both "
						+ result.annotationType() + " and "
						+ a.annotationType() + " is present"
					);
				}
				else
				{
					result = a;
				}
			}
		}

		return result;
	}
}
