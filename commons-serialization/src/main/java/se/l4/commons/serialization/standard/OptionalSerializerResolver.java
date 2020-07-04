package se.l4.commons.serialization.standard;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.collections.AllowAnyItem;
import se.l4.commons.serialization.collections.CollectionSerializers;
import se.l4.commons.serialization.collections.Item;
import se.l4.commons.serialization.spi.SerializerResolver;
import se.l4.commons.serialization.spi.TypeEncounter;
import se.l4.commons.types.Types;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Resolver that resolves a suitable {@link OptionalSerializer} based on
 * the type declared.
 */
public class OptionalSerializerResolver
	implements SerializerResolver<Optional<?>>
{

	private static final Set<Class<? extends Annotation>> HINTS =
			ImmutableSet.<Class<? extends Annotation>>of(AllowAnyItem.class, Item.class);

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Optional<Serializer<Optional<?>>> find(TypeEncounter encounter)
	{
		TypeRef type = encounter.getType()
			.getTypeParameter(0)
			.orElseGet(() -> Types.reference(Object.class));

		return CollectionSerializers.resolveSerializer(encounter, type)
			.map(itemSerializer -> new OptionalSerializer(itemSerializer));
	}

	@Override
	public Set<Class<? extends Annotation>> getHints()
	{
		return HINTS;
	}

}
