package se.l4.commons.serialization.collections;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.spi.SerializerResolver;
import se.l4.commons.serialization.spi.TypeEncounter;
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
	private static final Set<Class<? extends Annotation>> HINTS =
		ImmutableSet.of(AllowAnyItem.class, Item.class, StringKey.class);

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

	@Override
	public Set<Class<? extends Annotation>> getHints()
	{
		return HINTS;
	}
}
