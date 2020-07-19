package se.l4.commons.serialization.collections;

import java.util.Optional;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.TypeEncounter;
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
	public static Serializer<?> resolveSerializer(TypeEncounter encounter, TypeRef type)
	{
		if(encounter.getHint(AllowAnyItem.class) != null)
		{
			return new DynamicSerializer.Impl(encounter.getCollection());
		}

		Optional<Item> item = encounter.getHint(Item.class);
		if(item.isPresent())
		{
			return encounter.find((Class) item.get().value(), type);
		}
		else
		{
			return encounter.find(type);
		}
	}
}
