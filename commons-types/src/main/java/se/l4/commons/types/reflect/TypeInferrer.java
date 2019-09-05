package se.l4.commons.types.reflect;

import java.util.Optional;

/**
 * Type inferrer that can infer a type by looking at another type. Used for
 * things such as figuring out how type parameters are used, or to create new
 * types.
 */
public interface TypeInferrer
{
	/**
	 * Infer our type by looking at the specified input types.
	 *
	 * @param types
	 *   the types to look at
	 * @return
	 *   the inferred type if available, empty optional otherwise
	 */
	Optional<TypeRef> infer(TypeRef... types);
}
