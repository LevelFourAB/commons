package se.l4.commons.serialization.collections;

import java.util.List;
import java.util.Optional;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerResolver;
import se.l4.commons.serialization.TypeEncounter;
import se.l4.commons.types.Types;
import se.l4.commons.types.reflect.TypeRef;

public class ListSerializerResolver
	implements SerializerResolver<List<?>>
{
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Optional<Serializer<List<?>>> find(TypeEncounter encounter)
	{
		if(! encounter.getType().isErasedType(List.class))
		{
			return Optional.empty();
		}

		TypeRef type = encounter.getType()
			.getTypeParameter(0)
			.orElseGet(() -> Types.reference(Object.class));

		return Optional.of(new ListSerializer(
			CollectionSerializers.resolveSerializer(encounter, type)
		));
	}
}
