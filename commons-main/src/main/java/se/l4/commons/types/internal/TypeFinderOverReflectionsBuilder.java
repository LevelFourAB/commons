package se.l4.commons.types.internal;

import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;

import se.l4.commons.types.DefaultInstanceFactory;
import se.l4.commons.types.InstanceFactory;
import se.l4.commons.types.TypeFinder;
import se.l4.commons.types.TypeFinderBuilder;

/**
 * Builder for {@link TypeFinderOverReflections}.
 * 
 * @author Andreas Holstenson
 *
 */
public class TypeFinderOverReflectionsBuilder
	implements TypeFinderBuilder
{
	private InstanceFactory factory;
	private Set<String> packages;
	
	public TypeFinderOverReflectionsBuilder()
	{
		factory = new DefaultInstanceFactory();
		packages = new HashSet<>();
	}

	@Override
	public TypeFinderBuilder setInstanceFactory(InstanceFactory factory)
	{
		this.factory = factory;
		return this;
	}

	@Override
	public TypeFinderBuilder addPackage(String pkgName)
	{
		packages.add(pkgName);
		return this;
	}

	@Override
	public TypeFinderBuilder addPackages(Iterable<String> pkgs)
	{
		for(String pkg : pkgs)
		{
			packages.add(pkg);
		}
		return this;
	}

	@Override
	public TypeFinder build()
	{
		Reflections reflections = new Reflections(packages.toArray());
		return new TypeFinderOverReflections(factory, reflections);
	}

}
