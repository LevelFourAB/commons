package se.l4.commons.serialization;

import java.lang.annotation.Annotation;
import java.util.Optional;

import edu.umd.cs.findbugs.annotations.NonNull;
import se.l4.commons.types.InstanceFactory;
import se.l4.commons.types.reflect.TypeRef;


/**
 * Collection of {@link Serializer}s and {@link SerializerResolver resolvers}.
 *
 * @author Andreas Holstenson
 *
 */
public interface Serializers
{
	/**
	 * Get the current instance factory.
	 *
	 * @return
	 */
	@NonNull
	InstanceFactory getInstanceFactory();

	/**
	 * Bind a certain type automatically discovering which serializer to
	 * use.
	 *
	 * @param type
	 */
	@NonNull
	Serializers bind(@NonNull Class<?> type);

	/**
	 * Bind a given type to the specified serializer.
	 *
	 * @param <T>
	 * @param type
	 * @param serializer
	 */
	@NonNull
	<T> Serializers bind(@NonNull Class<T> type, @NonNull Serializer<T> serializer);

	/**
	 * Bind a given type to the specified resolver. The resolver will be
	 * asked to resolve a more specific serializer based on type parameters.
	 *
	 * @param <T>
	 * @param type
	 * @param resolver
	 */
	@NonNull
	<T> Serializers bind(@NonNull Class<T> type, @NonNull SerializerResolver<? extends T> resolver);

	/**
	 * Find a serializer suitable for the specific type.
	 *
	 * @param <T>
	 * @param type
	 * @return
	 */
	@NonNull
	<T> Serializer<T> find(@NonNull Class<T> type);

	/**
	 * Find a serializer suitable for the specific type.
	 *
	 * @param <T>
	 * @param type
	 * @return
	 */
	@NonNull
	<T> Serializer<T> find(@NonNull Class<T> type, @NonNull Iterable<? extends Annotation> hints);

	/**
	 * Find a serializer suitable for the specified type.
	 *
	 * @param type
	 * @return
	 */
	@NonNull
	Serializer<?> find(@NonNull TypeRef type);

	/**
	 * Find a serializer based on its registered name.
	 *
	 * @param name
	 * @return
	 */
	@NonNull
	Optional<? extends Serializer<?>> find(String name);

	/**
	 * Find a serializer based on its registered name.
	 *
	 * @param name
	 * @return
	 */
	@NonNull
	Optional<? extends Serializer<?>> find(QualifiedName name);

	/**
	 * Find a serializer based on its registered name.
	 *
	 * @param namespace
	 * @param name
	 * @return
	 */
	@NonNull
	Optional<? extends Serializer<?>> find(@NonNull String namespace, @NonNull String name);

	/**
	 * Get if the given type can be serialized.
	 *
	 * @param type
	 * @return
	 */
	boolean isSupported(@NonNull Class<?> type);
}
