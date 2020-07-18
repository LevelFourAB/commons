package se.l4.commons.serialization.collections;

import java.util.Optional;
import java.util.Set;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerResolver;
import se.l4.commons.serialization.TypeEncounter;
import se.l4.commons.types.Types;
import se.l4.commons.types.reflect.TypeRef;

public class SetSerializerResolver
	implements SerializerResolver<Set<?>>
{
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Optional<Serializer<Set<?>>> find(TypeEncounter encounter)
	{
		if(! encounter.getType().isErasedType(Set.class))
		{
			return Optional.empty();
		}

		TypeRef type = encounter.getType()
			.getTypeParameter(0)
			.orElseGet(() -> Types.reference(Object.class));

		return Optional.of(new SetSerializer(
			CollectionSerializers.resolveSerializer(encounter, type)
		));
	}
}
