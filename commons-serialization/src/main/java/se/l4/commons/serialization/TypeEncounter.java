package se.l4.commons.serialization;

import java.lang.annotation.Annotation;
import java.util.Optional;

import edu.umd.cs.findbugs.annotations.NonNull;
import se.l4.commons.types.mapping.ResolutionEncounter;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Encounter with a specific type during serialization resolution.
 *
 * @author Andreas Holstenson
 *
 */
public interface TypeEncounter
	extends ResolutionEncounter<Serializer<?>>
{
	/**
	 * Get the collection this encounter is for.
	 *
	 * @return
	 */
	@NonNull
	Serializers getCollection();

	/**
	 * Get the type encountered.
	 *
	 * @return
	 */
	@NonNull
	@Override
	TypeRef getType();

	/**
	 * Get a hint if it is present. Hints are either present in how a type is
	 * being used or on the type.
	 *
	 * @param <T>
	 * @param annotationClass
	 * @return
	 */
	<T extends Annotation> Optional<T> getHint(Class<T> annotationClass);

	/**
	 * Perform a recursive resolution of the given optional. If it represents
	 * a {@link Serializer} it will be returned and if it represents a
	 * {@link SerializerResolver} this method will continue to resolve until a
	 * {@link Serializer} can be found.
	 *
	 * @param <T>
	 * @param optional
	 * @return
	 */
	<T> Serializer<T> resolve(TypeRef type, Optional<? extends SerializerOrResolver<T>> optional);

	/**
	 * Find a serializer for the given type.
	 *
	 * @param type
	 * @return
	 */
	<T> Serializer<T> find(TypeRef type);

	/**
	 * Find or create a serializer for the given type using the given serializer
	 * or resolver. Internally this will attempt to create an instance of the
	 * given serializer or resolver class, and in the case of a serializer
	 * return it or for a resolver it will attempt to resolve a serializer
	 * using the instance.
	 *
	 * @param serializerOrResolver
	 *   a resolver
	 */
	<T> Serializer<T> find(Class<? extends SerializerOrResolver<T>> serializerOrResolver, TypeRef type);
}
