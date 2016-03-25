package se.l4.commons.serialization.collections;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import se.l4.commons.serialization.SerializationException;
import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.spi.SerializerResolver;
import se.l4.commons.serialization.spi.Type;
import se.l4.commons.serialization.spi.TypeEncounter;
import se.l4.commons.serialization.spi.TypeViaClass;

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
	public Serializer<Map<?, ?>> find(TypeEncounter encounter)
	{
		Type[] params = encounter.getType().getParameters();
		Type type = params.length < 2 ? new TypeViaClass(Object.class) : params[1];  
		
		Class<?> erasedType = encounter.getType().getErasedType();
		if(erasedType != Map.class)
		{
			throw new SerializationException("Maps can only be serialized if they are declared as the interface Map");
		}
		
		StringKey key = encounter.getHint(StringKey.class);
		if(key != null)
		{
			Serializer<?> itemSerializer = CollectionSerializers.resolveSerializer(encounter, type);
			return new MapAsObjectSerializer(itemSerializer);
		}
		
		return null;
	}

	@Override
	public Set<Class<? extends Annotation>> getHints()
	{
		return HINTS;
	}
}
