package se.l4.commons.serialization.collections;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.spi.Type;
import se.l4.commons.serialization.spi.TypeEncounter;
import se.l4.commons.serialization.standard.DynamicSerializer;

public class CollectionSerializers
{
	private CollectionSerializers()
	{
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Serializer<?> resolveSerializer(TypeEncounter encounter, Type type)
	{
		if(encounter.getHint(AllowAnyItem.class) != null)
		{
			return new DynamicSerializer(encounter.getCollection());
		}

		Item item = encounter.getHint(Item.class);
		if(item != null)
		{
			return encounter.getCollection().findVia((Class) item.value(), type);
		}

		return encounter.getCollection().find(type);
	}
}