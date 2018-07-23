package se.l4.commons.serialization;

import java.lang.annotation.Annotation;

import se.l4.commons.serialization.spi.SerializerResolver;
import se.l4.commons.serialization.spi.Type;
import se.l4.commons.types.InstanceFactory;


/**
 * Collection of {@link Serializer}s and {@link SerializerResolver resolvers}.
 *
 * @author Andreas Holstenson
 *
 */
public interface SerializerCollection
{
	/**
	 * Get the current instance factory.
	 *
	 * @return
	 */
	InstanceFactory getInstanceFactory();

	/**
	 * Bind a certain type automatically discovering which serializer to
	 * use.
	 *
	 * @param type
	 */
	SerializerCollection bind(Class<?> type);

	/**
	 * Bind a given type to the specified serializer.
	 *
	 * @param <T>
	 * @param type
	 * @param serializer
	 */
	<T> SerializerCollection bind(Class<T> type, Serializer<T> serializer);

	/**
	 * Bind a given type to the specified resolver. The resolver will be
	 * asked to resolve a more specific serializer based on type parameters.
	 *
	 * @param <T>
	 * @param type
	 * @param resolver
	 */
	<T> SerializerCollection bind(Class<T> type, SerializerResolver<? extends T> resolver);

	/**
	 * Find a serializer suitable for the specific type.
	 *
	 * @param <T>
	 * @param type
	 * @return
	 */
	<T> Serializer<T> find(Class<T> type);

	/**
	 * Find a serializer suitable for the specific type.
	 *
	 * @param <T>
	 * @param type
	 * @return
	 */
	<T> Serializer<T> find(Class<T> type, Annotation... hints);

	/**
	 * Find a serializer suitable for the specified type.
	 *
	 * @param type
	 * @return
	 */
	Serializer<?> find(Type type);

	/**
	 * Find a serializer suitable for the specified type.
	 *
	 * @param type
	 * @return
	 */
	Serializer<?> find(Type type, Annotation... hints);

	/**
	 * Find a serializer based on its registered name.
	 *
	 * @param name
	 * @return
	 */
	Serializer<?> find(String name);

	/**
	 * Find a serializer based on its registered name.
	 *
	 * @param namespace
	 * @param name
	 * @return
	 */
	Serializer<?> find(String namespace, String name);

	/**
	 * Find a serializer using a specific {@link SerializerResolver}.
	 *
	 * @param sOrR
	 * @param type
	 * @return
	 */
	<T> Serializer<T> findVia(Class<? extends SerializerOrResolver<T>> resolver, Class<T> type, Annotation... hints);

	/**
	 * Find a serializer using a specific {@link SerializerResolver}.
	 *
	 * @param sOrR
	 * @param type
	 * @return
	 */
	<T> Serializer<T> findVia(Class<? extends SerializerOrResolver<T>> resolver, Type type, Annotation... hints);

	/**
	 * Get the resolver this collection would use to resolve a serializer
	 * for the given type.
	 *
	 * @param type
	 * @return
	 */
	SerializerResolver<?> getResolver(Class<?> type);

	/**
	 * Get if the given type can be serialized.
	 *
	 * @param type
	 * @return
	 */
	boolean isSupported(Class<?> type);

	/**
	 * Find the name of the given serializer (if any).
	 *
	 * @param serializer
	 * @return
	 */
	QualifiedName findName(Serializer<?> serializer);
}