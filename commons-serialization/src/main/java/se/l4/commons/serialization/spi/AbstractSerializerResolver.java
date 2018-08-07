package se.l4.commons.serialization.spi;

/**
 * Abstract implementation of {@link SerializerResolver}.
 *
 * @author Andreas Holstenson
 *
 * @deprecated
 *   it is recommended to implement {@link SerializerResolver} directly instead
 *   of using this class
 * @param <T>
 */
@Deprecated
public abstract class AbstractSerializerResolver<T>
	implements SerializerResolver<T>
{
}
