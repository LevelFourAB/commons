package se.l4.commons.serialization.internal;

import java.util.Optional;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerOrResolver;
import se.l4.commons.serialization.SerializerResolver;
import se.l4.commons.serialization.TypeEncounter;
import se.l4.commons.types.mapping.Resolver;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class SerializerResolverAdapter
	implements Resolver<TypeEncounter, Serializer<?>>
{
	private final SerializerResolver resolver;

	public SerializerResolverAdapter(SerializerResolver resolver)
	{
		this.resolver = resolver;
	}

	@Override
	public Optional<Serializer<?>> resolve(TypeEncounter encounter)
	{
		Optional<SerializerOrResolver<?>> resolved = resolver.find(encounter);
		if(! resolved.isPresent())
		{
			return Optional.empty();
		}

		SerializerOrResolver<?> result = resolved.get();
		if(result instanceof Serializer)
		{
			return (Optional) resolved;
		}

		return ((SerializerResolver) result).find(encounter);
	}
}
