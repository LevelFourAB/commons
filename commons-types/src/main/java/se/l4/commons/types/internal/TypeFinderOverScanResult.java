package se.l4.commons.types.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import se.l4.commons.types.InstanceFactory;
import se.l4.commons.types.TypeFinder;

/**
 * Implementation of {@link TypeFinder} that uses {@link Reflections}.
 *
 * @author Andreas Holstenson
 *
 */
public class TypeFinderOverScanResult
	implements TypeFinder
{
	private final ScanResult scanResult;
	private final InstanceFactory factory;

	public TypeFinderOverScanResult(InstanceFactory factory, ScanResult scanResult)
	{
		this.factory = factory;
		this.scanResult = scanResult;
	}

	/**
	 * Convert a {@link ClassInfoList} into a set of classes.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Set<Class<?>> toClasses(ClassInfoList list)
	{
		Set<Class> result = new HashSet<Class>();
		for(ClassInfo s : list)
		{
			result.add(s.loadClass(false));
		}

		return Collections.unmodifiableSet((Set) result);
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotationType)
	{
		return (Set) toClasses(scanResult.getClassesWithAnnotation(annotationType.getName()));
	}

	@Override
	public Set<? extends Object> getTypesAnnotatedWithAsInstances(Class<? extends Annotation> annotationType)
	{
		return create(getTypesAnnotatedWith(annotationType));
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> Set<Class<? extends T>> getSubTypesOf(Class<T> type)
	{
		ClassInfoList list;
		if(type.isInterface())
		{
			list = scanResult.getClassesImplementing(type.getName());
		}
		else
		{
			list = scanResult.getSubclasses(type.getName());
		}
		return (Set) toClasses(list);
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
		Set<T> result = new HashSet<>();

		for(Class<? extends T> t : types)
		{
			if(! Modifier.isAbstract(t.getModifiers()) && ! t.isInterface())
			{
				T instance = factory.create(t);
				result.add(instance);
			}
		}

		return Collections.unmodifiableSet(result);
	}
}
