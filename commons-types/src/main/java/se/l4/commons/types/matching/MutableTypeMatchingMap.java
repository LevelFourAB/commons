package se.l4.commons.types.matching;

import edu.umd.cs.findbugs.annotations.NonNull;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Version of {@link TypeMatchingMap} that can be mutated.
 */
public interface MutableTypeMatchingMap<D>
	extends TypeMatchingMap<D>
{
	/**
	 * Associated some data with a type. This will allow matching against
	 * the type and its subclasses or in the case of interfaces its
	 * implementors.
	 *
	 * @param type
	 *   the type to associate data with
	 * @param data
	 *   the data to store, never {@code null}
	 */
	void put(@NonNull TypeRef type, @NonNull D data);
}
