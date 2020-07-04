package se.l4.commons.serialization.spi;

import java.util.Optional;

import se.l4.commons.serialization.Serializer;

/**
 * Resolver for types that have only one serializer.
 *
 * @author Andreas Holstenson
 *
 * @param <T>
 */
public class StaticSerializerResolver<T>
	implements SerializerResolver<T>
{
	private final Serializer<T> serializer;

	public StaticSerializerResolver(Serializer<T> serializer)
	{
		this.serializer = serializer;
	}

	@Override
	public Optional<Serializer<T>> find(TypeEncounter encounter)
	{
		return Optional.of(serializer);
	}
}
