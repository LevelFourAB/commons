package se.l4.commons.types.mapping;

import java.util.Optional;

/**
 * Resolver as used to resolve how a type is mapped. It is recommended to
 * create a subinterface for this resolver if making it part of the public API
 * of another module.
 *
 * @param <I>
 * @param <O>
 */
public interface Resolver<I extends ResolutionEncounter<O>, O>
{
	/**
	 * Attempt to resolve something.
	 *
	 * @param encounter
	 * @return
	 */
	Optional<O> resolve(I encounter);
}
