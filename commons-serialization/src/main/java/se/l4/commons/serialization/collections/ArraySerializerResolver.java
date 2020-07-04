package se.l4.commons.serialization.collections;

import java.util.Optional;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.collections.array.BooleanArraySerializer;
import se.l4.commons.serialization.collections.array.CharArraySerializer;
import se.l4.commons.serialization.collections.array.DoubleArraySerializer;
import se.l4.commons.serialization.collections.array.FloatArraySerializer;
import se.l4.commons.serialization.collections.array.IntArraySerializer;
import se.l4.commons.serialization.collections.array.LongArraySerializer;
import se.l4.commons.serialization.collections.array.ShortArraySerializer;
import se.l4.commons.serialization.spi.SerializerResolver;
import se.l4.commons.serialization.spi.TypeEncounter;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Resolver for array types.
 *
 * @author Andreas Holstenson
 *
 */
@SuppressWarnings("rawtypes")
public class ArraySerializerResolver
	implements SerializerResolver
{

	@Override
	public Optional<Serializer<?>> find(TypeEncounter encounter)
	{
		TypeRef componentType = encounter.getType().getComponentType()
			.get();

		/*
		 * Resolve the serializer by first looking for serializers that handle
		 * primitive types. If the component type of the array is not primitive
		 * we fallback on a serializer that can handle objects.
		 */

		if(componentType.isErasedType(char.class))
		{
			return Optional.of(new CharArraySerializer());
		}
		else if(componentType.isErasedType(boolean.class))
		{
			return Optional.of(new BooleanArraySerializer());
		}
		else if(componentType.isErasedType(double.class))
		{
			return Optional.of(new DoubleArraySerializer());
		}
		else if(componentType.isErasedType(float.class))
		{
			return Optional.of(new FloatArraySerializer());
		}
		else if(componentType.isErasedType(int.class))
		{
			return Optional.of(new IntArraySerializer());
		}
		else if(componentType.isErasedType(long.class))
		{
			return Optional.of(new LongArraySerializer());
		}
		else if(componentType.isErasedType(short.class))
		{
			return Optional.of(new ShortArraySerializer());
		}

		return encounter.getCollection()
			.find(componentType)
			.map(itemSerializer -> new ArraySerializer(componentType.getErasedType(), itemSerializer));
	}
}
