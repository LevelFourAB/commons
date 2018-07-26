package se.l4.commons.types.internal;

import java.util.HashSet;
import java.util.Set;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.ScanResult;
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
		ScanResult result = new FastClasspathScanner()
			.enableAnnotationInfo()
			.enableClassInfo()
			.enableAllInfo()
			.whitelistPackages(packages.toArray(new String[packages.size()]))
			.scan();

		return new TypeFinderOverScanResult(factory, result);
	}

}
