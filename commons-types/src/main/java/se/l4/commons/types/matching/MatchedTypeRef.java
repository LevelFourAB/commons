package se.l4.commons.types.matching;

import edu.umd.cs.findbugs.annotations.NonNull;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Information about a type that has been matched.
 *
 * @param <D>
 */
public interface MatchedTypeRef<D>
{
	/**
	 * Get the type reference.
	 *
	 * @return
	 */
	@NonNull
	TypeRef getType();

	/**
	 * Get the data.
	 *
	 * @return
	 */
	@NonNull
	D getData();
}
