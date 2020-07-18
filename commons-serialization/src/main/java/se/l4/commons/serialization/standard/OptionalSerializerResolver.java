package se.l4.commons.serialization.standard;

import java.util.Optional;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerResolver;
import se.l4.commons.serialization.TypeEncounter;
import se.l4.commons.serialization.collections.CollectionSerializers;
import se.l4.commons.types.Types;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Resolver that resolves a suitable {@link OptionalSerializer} based on
 * the type declared.
 */
public class OptionalSerializerResolver
	implements SerializerResolver<Optional<?>>
{
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Optional<Serializer<Optional<?>>> find(TypeEncounter encounter)
	{
		TypeRef type = encounter.getType()
			.getTypeParameter(0)
			.orElseGet(() -> Types.reference(Object.class));

		return Optional.ofNullable(new OptionalSerializer(
			CollectionSerializers.resolveSerializer(encounter, type)
		));
	}
}
