package se.l4.commons.serialization.spi;

import java.util.Optional;

import com.google.common.primitives.Primitives;

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
	private final Class<T> type;
	private final Serializer<T> serializer;

	public StaticSerializerResolver(Class<T> type, Serializer<T> serializer)
	{
		this.type = type;
		this.serializer = serializer;
	}

	@Override
	public Optional<Serializer<T>> find(TypeEncounter encounter)
	{
		if(encounter.getType().isErasedType(type) || encounter.getType().isErasedType(Primitives.unwrap(type)))
		{
			return Optional.of(serializer);
		}
		else
		{
			return Optional.empty();
		}
	}
}
