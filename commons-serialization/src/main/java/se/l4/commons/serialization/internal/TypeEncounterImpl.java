package se.l4.commons.serialization.internal;

import java.lang.annotation.Annotation;
import java.util.Optional;

import se.l4.commons.serialization.SerializationException;
import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerOrResolver;
import se.l4.commons.serialization.SerializerResolver;
import se.l4.commons.serialization.Serializers;
import se.l4.commons.serialization.TypeEncounter;
import se.l4.commons.types.mapping.OutputDeduplicator;
import se.l4.commons.types.reflect.AnnotationLocator;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Implementation of {@link TypeEncounter}.
 *
 * @author Andreas Holstenson
 *
 */
public class TypeEncounterImpl
	implements TypeEncounter
{
	private final Serializers collection;
	private final OutputDeduplicator<Serializer<?>> deduplicator;
	private final TypeRef type;

	public TypeEncounterImpl(
		Serializers collection,
		OutputDeduplicator<Serializer<?>> deduplicator,
		TypeRef type
	)
	{
		this.collection = collection;
		this.deduplicator = deduplicator;
		this.type = type;
	}

	@Override
	public Serializers getCollection()
	{
		return collection;
	}

	@Override
	public TypeRef getType()
	{
		return type;
	}

	@Override
	public <T extends Annotation> Optional<T> getHint(Class<T> annotationClass)
	{
		AnnotationLocator<T> locator = AnnotationLocator.meta(annotationClass);

		Optional<T> viaUsage = type.getUsage().findAnnotation(locator);
		if(viaUsage.isPresent())
		{
			return viaUsage;
		}

		return type.getAnnotation(locator);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Serializer<T> resolve(TypeRef type, Optional<? extends SerializerOrResolver<T>> optional)
	{
		if(! optional.isPresent())
		{
			throw new SerializationException("Could not resolve serializer for " + type.toTypeDescription());
		}

		SerializerOrResolver<?> result = optional.get();
		if(result instanceof Serializer)
		{
			return (Serializer<T>) deduplicator.deduplicate((Serializer<?>) result);
		}
		else if(result instanceof SerializerResolver)
		{
			return resolve(type, ((SerializerResolver<T>) result).find(this));
		}
		else
		{
			throw new SerializationException("Could not resolve serializer for " + type.toTypeDescription() + ", resolved to neither a Serializer nor a SerializerResolver. Was: " + result);
		}
	}

	@Override
	public <T> Serializer<T> find(Class<? extends SerializerOrResolver<T>> serializerOrResolver, TypeRef type)
	{
		SerializerOrResolver<T> instance = collection.getInstanceFactory()
			.create(serializerOrResolver);

		if(instance instanceof Serializer)
		{
			return (Serializer<T>) deduplicator.deduplicate((Serializer<?>) instance);
		}
		else if(instance instanceof SerializerResolver)
		{
			return resolve(type, ((SerializerResolver<T>) instance).find(this));
		}
		else
		{
			throw new SerializationException("The type " + serializerOrResolver + " does not implement Serializer or SerializerResolver");
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Serializer<T> find(TypeRef type)
	{
		return (Serializer<T>) collection.find(type);
	}
}
