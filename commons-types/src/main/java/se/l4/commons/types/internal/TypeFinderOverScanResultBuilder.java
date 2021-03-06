package se.l4.commons.types.internal;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
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
public class TypeFinderOverScanResultBuilder
	implements TypeFinderBuilder
{
	private InstanceFactory factory;
	private Set<String> packages;

	public TypeFinderOverScanResultBuilder()
	{
		factory = new DefaultInstanceFactory();
		packages = new HashSet<>();
	}

	@Override
	public TypeFinderBuilder setInstanceFactory(InstanceFactory factory)
	{
		Objects.requireNonNull(factory);

		this.factory = factory;
		return this;
	}

	@Override
	public TypeFinderBuilder addPackage(String pkgName)
	{
		Objects.requireNonNull(pkgName);

		packages.add(pkgName);
		return this;
	}

	@Override
	public TypeFinderBuilder addPackages(Iterable<String> pkgs)
	{
		Objects.requireNonNull(pkgs);

		for(String pkg : pkgs)
		{
			packages.add(pkg);
		}
		return this;
	}

	@Override
	public TypeFinder build()
	{
		ScanResult result = new ClassGraph()
			.enableAnnotationInfo()
			.enableClassInfo()
			.acceptPackages(packages.toArray(new String[packages.size()]))
			.scan();

		return new TypeFinderOverScanResult(factory, result);
	}

}
