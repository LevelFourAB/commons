package se.l4.commons.serialization.collections;

import java.util.Map;
import java.util.Optional;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerResolver;
import se.l4.commons.serialization.TypeEncounter;
import se.l4.commons.types.Types;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Resolver for serializer of {@link Map}.
 *
 * @author Andreas Holstenson
 *
 */
public class MapSerializerResolver
	implements SerializerResolver<Map<?, ?>>
{
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Optional<Serializer<Map<?, ?>>> find(TypeEncounter encounter)
	{
		if(! encounter.getType().isErasedType(Map.class))
		{
			return Optional.empty();
		}

		TypeRef type = encounter.getType()
			.getTypeParameter(1)
			.orElseGet(() -> Types.reference(Object.class));

		Optional<StringKey> key = encounter.getHint(StringKey.class);
		if(key.isPresent())
		{
			return Optional.of(new MapAsObjectSerializer(
				CollectionSerializers.resolveSerializer(encounter, type)
			));
		}

		return Optional.empty();
	}
}
