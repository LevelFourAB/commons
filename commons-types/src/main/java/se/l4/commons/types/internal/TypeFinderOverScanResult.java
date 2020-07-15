package se.l4.commons.types.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Objects;

import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.SetIterable;

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

	@Override
	public SetIterable<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotationType)
	{
		Objects.requireNonNull(annotationType);

		return toClasses(scanResult.getClassesWithAnnotation(annotationType.getName()));
	}

	@Override
	public SetIterable<? extends Object> getTypesAnnotatedWithAsInstances(Class<? extends Annotation> annotationType)
	{
		return create(getTypesAnnotatedWith(annotationType));
	}

	@Override
	public <T> SetIterable<Class<? extends T>> getSubTypesOf(Class<T> type)
	{
		Objects.requireNonNull(type);

		ClassInfoList list;
		if(type.isInterface())
		{
			list = scanResult.getClassesImplementing(type.getName());
		}
		else
		{
			list = scanResult.getSubclasses(type.getName());
		}
		return toClasses(list);
	}

	@Override
	public <T> SetIterable<? extends T> getSubTypesAsInstances(Class<T> type)
	{
		return create(getSubTypesOf(type));
	}

	/**
	 * Convert a {@link ClassInfoList} into a set of classes.
	 */
	@SuppressWarnings("unchecked")
	private <T> SetIterable<Class<? extends T>> toClasses(ClassInfoList list)
	{
		MutableSet<Class<? extends T>> result = Sets.mutable.empty();
		for(ClassInfo s : list)
		{
			result.add((Class<? extends T>) s.loadClass(false));
		}

		return result;
	}

	/**
	 * Create the given types.
	 *
	 * @param types
	 * @return
	 */
	private <T> SetIterable<? extends T> create(SetIterable<Class<? extends T>> types)
	{
		MutableSet<T> result = Sets.mutable.empty();

		for(Class<? extends T> t : types)
		{
			if(! Modifier.isAbstract(t.getModifiers()) && ! t.isInterface())
			{
				T instance = factory.create(t);
				result.add(instance);
			}
		}

		return result;
	}
}
