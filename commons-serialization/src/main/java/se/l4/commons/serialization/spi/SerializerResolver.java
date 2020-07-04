package se.l4.commons.serialization.spi;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;
import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerOrResolver;

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
	Optional<Serializer<T>> find(@NonNull TypeEncounter encounter);

	/**
	 * Get the hints this resolver uses.
	 *
	 * @return
	 */
	@NonNull
	default Set<Class<? extends Annotation>> getHints()
	{
		return Collections.emptySet();
	}
}
