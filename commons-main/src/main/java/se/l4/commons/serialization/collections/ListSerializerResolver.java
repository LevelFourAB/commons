package se.l4.commons.serialization.collections;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import se.l4.commons.serialization.SerializationException;
import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.spi.AbstractSerializerResolver;
import se.l4.commons.serialization.spi.Type;
import se.l4.commons.serialization.spi.TypeEncounter;
import se.l4.commons.serialization.spi.TypeViaClass;

public class ListSerializerResolver
	extends AbstractSerializerResolver<List<?>>
{
	private static final Set<Class<? extends Annotation>> HINTS =
		ImmutableSet.<Class<? extends Annotation>>of(AllowAnyItem.class, Item.class);
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Serializer<List<?>> find(TypeEncounter encounter)
	{
		Type[] params = encounter.getType().getParameters();
		Type type = params.length == 0 ? new TypeViaClass(Object.class) : params[0];  
		
		// Check that we can create the type of list requested
		Class<?> erasedType = encounter.getType().getErasedType();
		if(erasedType != List.class)
		{
			throw new SerializationException("Lists can only be serialized if they are declared as the interface List");
		}
		
		Serializer<?> itemSerializer = CollectionSerializers.resolveSerializer(encounter, type);
			
		return new ListSerializer(itemSerializer);
	}

	@Override
	public Set<Class<? extends Annotation>> getHints()
	{
		return HINTS;
	}
}
