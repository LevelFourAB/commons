package se.l4.commons.serialization.collections;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.collections.array.BooleanArraySerializer;
import se.l4.commons.serialization.collections.array.CharArraySerializer;
import se.l4.commons.serialization.collections.array.DoubleArraySerializer;
import se.l4.commons.serialization.collections.array.FloatArraySerializer;
import se.l4.commons.serialization.collections.array.IntArraySerializer;
import se.l4.commons.serialization.collections.array.LongArraySerializer;
import se.l4.commons.serialization.collections.array.ShortArraySerializer;
import se.l4.commons.serialization.spi.AbstractSerializerResolver;
import se.l4.commons.serialization.spi.TypeEncounter;
import se.l4.commons.serialization.spi.TypeViaClass;

/**
 * Resolver for array types.
 *
 * @author Andreas Holstenson
 *
 */
@SuppressWarnings("rawtypes")
public class ArraySerializerResolver
	extends AbstractSerializerResolver
{

	@Override
	public Serializer find(TypeEncounter encounter)
	{
		// TODO: Generics?

		Class<?> componentType = encounter.getType().getErasedType()
			.getComponentType();

		/*
		 * Resolve the serializer by first looking for serializers that handle
		 * primitive types. If the component type of the array is not primitive
		 * we fallback on a serializer that can handle objects.
		 */

		if(componentType == char.class)
		{
			return new CharArraySerializer();
		}
		else if(componentType == boolean.class)
		{
			return new BooleanArraySerializer();
		}
		else if(componentType == double.class)
		{
			return new DoubleArraySerializer();
		}
		else if(componentType == float.class)
		{
			return new FloatArraySerializer();
		}
		else if(componentType == int.class)
		{
			return new IntArraySerializer();
		}
		else if(componentType == long.class)
		{
			return new LongArraySerializer();
		}
		else if(componentType == short.class)
		{
			return new ShortArraySerializer();
		}

		Serializer<?> itemSerializer = encounter.getCollection()
			.find(new TypeViaClass(componentType));

		return new ArraySerializer(componentType, itemSerializer);
	}

}
