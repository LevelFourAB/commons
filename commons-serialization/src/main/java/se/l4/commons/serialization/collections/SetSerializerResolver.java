package se.l4.commons.serialization.collections;

import java.lang.annotation.Annotation;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import se.l4.commons.serialization.SerializationException;
import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.spi.AbstractSerializerResolver;
import se.l4.commons.serialization.spi.Type;
import se.l4.commons.serialization.spi.TypeEncounter;
import se.l4.commons.serialization.spi.TypeViaClass;

public class SetSerializerResolver
	extends AbstractSerializerResolver<Set<?>>
{
	private static final Set<Class<? extends Annotation>> HINTS =
		ImmutableSet.<Class<? extends Annotation>>of(AllowAnyItem.class, Item.class);

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Serializer<Set<?>> find(TypeEncounter encounter)
	{
		Type[] params = encounter.getType().getParameters();
		Type type = params.length == 0 ? new TypeViaClass(Object.class) : params[0];

		// Check that we can create the type of list requested
		Class<?> erasedType = encounter.getType().getErasedType();
		if(erasedType != Set.class)
		{
			throw new SerializationException("Sets can only be serialized if they are declared as the interface Set");
		}

		Serializer<?> itemSerializer = CollectionSerializers.resolveSerializer(encounter, type);

		return new SetSerializer(itemSerializer);
	}

	@Override
	public Set<Class<? extends Annotation>> getHints()
	{
		return HINTS;
	}
}