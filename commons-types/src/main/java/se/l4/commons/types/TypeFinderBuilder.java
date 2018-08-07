package se.l4.commons.types;

import edu.umd.cs.findbugs.annotations.NonNull;

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
	 *   self
	 */
	@NonNull
	TypeFinderBuilder setInstanceFactory(@NonNull InstanceFactory factory);

	/**
	 * Add a package to scan for types. This will activate scanning for
	 * the package and any subpackages it has.
	 *
	 * @return
	 *   self
	 */
	@NonNull
	TypeFinderBuilder addPackage(@NonNull String pkgName);

	/**
	 * Add several packages to scan for types.
	 *
	 * @param pkgs
	 * @return
	 *   self
	 */
	@NonNull
	TypeFinderBuilder addPackages(@NonNull Iterable<String> pkgs);

	/**
	 * Create the instance.
	 *
	 * @return
	 */
	@NonNull
	TypeFinder build();
}
