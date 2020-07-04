package se.l4.commons.serialization.collections;

import java.util.Optional;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.spi.TypeEncounter;
import se.l4.commons.serialization.standard.DynamicSerializer;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Utilities that are useful when using or creating serializers that work on
 * collection types.
 */
public class CollectionSerializers
{
	private CollectionSerializers()
	{
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Optional<? extends Serializer<?>> resolveSerializer(TypeEncounter encounter, TypeRef type)
	{
		if(encounter.getHint(AllowAnyItem.class) != null)
		{
			return Optional.of(new DynamicSerializer(encounter.getCollection()));
		}

		Optional<Item> item = encounter.getHint(Item.class);
		if(item.isPresent())
		{
			return encounter.getCollection().findVia((Class) item.get().value(), type);
		}
		else
		{
			return encounter.getCollection().find(type);
		}
	}
}
