package se.l4.commons.types.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Set;

import org.reflections.Reflections;

import com.google.common.collect.ImmutableSet;

import se.l4.commons.types.InstanceFactory;
import se.l4.commons.types.TypeFinder;

/**
 * Implementation of {@link TypeFinder} that uses {@link Reflections}.
 * 
 * @author Andreas Holstenson
 *
 */
public class TypeFinderOverReflections
	implements TypeFinder
{
	private final Reflections reflections;
	private final InstanceFactory factory;

	public TypeFinderOverReflections(InstanceFactory factory, Reflections reflections)
	{
		this.factory = factory;
		this.reflections = reflections;
	}

	@Override
	public Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotationType)
	{
		return reflections.getTypesAnnotatedWith(annotationType);
	}
	
	@Override
	public Set<? extends Object> getTypesAnnotatedWithAsInstances(Class<? extends Annotation> annotationType)
	{
		return create(getTypesAnnotatedWith(annotationType));
	}

	@Override
	public <T> Set<Class<? extends T>> getSubTypesOf(Class<T> type)
	{
		return reflections.getSubTypesOf(type);
	}

	@Override
	public <T> Set<? extends T> getSubTypesAsInstances(Class<T> type)
	{
		return create(getSubTypesOf(type));
	}

	/**
	 * Create the given types.
	 * 
	 * @param types
	 * @return
	 */
	private <T> Set<? extends T> create(Iterable<Class<? extends T>> types)
	{
		ImmutableSet.Builder<T> builder = ImmutableSet.builder();
		
		for(Class<? extends T> t : types)
		{
			if(! Modifier.isAbstract(t.getModifiers()) && ! t.isInterface())
			{
				T instance = factory.create(t);
				builder.add(instance);
			}
		}
		
		return builder.build();
	}
}
