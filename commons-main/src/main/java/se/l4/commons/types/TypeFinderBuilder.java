package se.l4.commons.types;

/**
 * Builder for instances of {@link TypeFinder}.
 * 
 * @author Andreas Holstenson
 *
 */
public interface TypeFinderBuilder
{
	/**
	 * Set the instance factory to use.
	 * 
	 * @param factory
	 * @return
	 */
	TypeFinderBuilder setInstanceFactory(InstanceFactory factory);
	
	/**
	 * Add a package to scan for types. This will activate scanning for
	 * the package and any subpackages it has.
	 * 
	 * @return
	 */
	TypeFinderBuilder addPackage(String pkgName);
	
	/**
	 * Add several packages to scan for types.
	 * 
	 * @param pkgs
	 * @return
	 */
	TypeFinderBuilder addPackages(Iterable<String> pkgs);
	
	/**
	 * Create the instance.
	 * 
	 * @return
	 */
	TypeFinder build();
}
