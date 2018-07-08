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
import se.l4.commons.serialization.spi.Type;
import se.l4.commons.serialization.spi.TypeEncounter;
import se.l4.commons.serialization.spi.TypeViaClass;

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
	public Serializer<Optional<?>> find(TypeEncounter encounter)
	{
		Type[] params = encounter.getType().getParameters();
		Type type = params.length == 0 ? new TypeViaClass(Object.class) : params[0];

		Serializer<?> itemSerializer = CollectionSerializers.resolveSerializer(encounter, type);

		return new OptionalSerializer(itemSerializer);
	}

	@Override
	public Set<Class<? extends Annotation>> getHints()
	{
		return HINTS;
	}

}
