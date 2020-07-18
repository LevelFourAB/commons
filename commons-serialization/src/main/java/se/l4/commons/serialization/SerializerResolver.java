package se.l4.commons.serialization;

import java.util.Optional;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Resolver for a specific {@link Serializer}. This is used to support
 * generics and other semi-dynamic features.
 *
 * <p>
 * Resolvers that use extra annotations to determine the serializer to use
 * should override {@link #getHints()} to return an array of the annotations
 * it uses.
 *
 * @author Andreas Holstenson
 *
 * @param <T>
 */
public interface SerializerResolver<T>
	extends SerializerOrResolver<T>
{
	/**
	 * Attempt to find a suitable serializer.
	 *
	 * @param encounter
	 * @return
	 */
	@NonNull
	Optional<? extends SerializerOrResolver<T>> find(@NonNull TypeEncounter encounter);
}
