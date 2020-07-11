package se.l4.commons.serialization.internal;

import java.util.Optional;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerOrResolver;
import se.l4.commons.serialization.Use;
import se.l4.commons.serialization.spi.SerializerResolver;
import se.l4.commons.serialization.spi.TypeEncounter;
import se.l4.commons.types.InstanceFactory;

public class UseSerializerResolver
	implements SerializerResolver<Object>
{
	private final InstanceFactory instanceFactory;

	public UseSerializerResolver(InstanceFactory factory)
	{
		this.instanceFactory = factory;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Optional<? extends SerializerOrResolver<Object>> find(TypeEncounter encounter)
	{
		if(encounter.getType().hasAnnotation(Use.class))
		{
			// A specific serializer should be used
			Use annotation = encounter.getType().getAnnotation(Use.class)
				.get();

			Class<? extends SerializerOrResolver> value = annotation.value();
			if(SerializerResolver.class.isAssignableFrom(value))
			{
				return Optional.of(instanceFactory.create((Class<? extends SerializerOrResolver<Object>>) value));
			}

			return Optional.of(instanceFactory.create((Class<? extends Serializer<Object>>) value));
		}

		return Optional.empty();
	}
}
