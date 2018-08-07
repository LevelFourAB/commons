package se.l4.commons.serialization;

import se.l4.commons.serialization.spi.SerializerResolver;

/**
 * Either a {@link Serializer} or a {@link SerializerResolver}. Used to support
 * both picking a specific serializer and to resolve one when using {@link Use}
 * on classes.
 */
public interface SerializerOrResolver<T>
{

}
