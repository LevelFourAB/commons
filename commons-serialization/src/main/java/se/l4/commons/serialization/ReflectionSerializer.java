package se.l4.commons.serialization;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import se.l4.commons.serialization.internal.reflection.FactoryDefinition;
import se.l4.commons.serialization.internal.reflection.FieldDefinition;
import se.l4.commons.serialization.internal.reflection.ReflectionNonStreamingSerializer;
import se.l4.commons.serialization.internal.reflection.ReflectionOnlySingleFactorySerializer;
import se.l4.commons.serialization.internal.reflection.ReflectionStreamingSerializer;
import se.l4.commons.serialization.internal.reflection.TypeInfo;
import se.l4.commons.serialization.spi.SerializerResolver;
import se.l4.commons.serialization.spi.TypeEncounter;
import se.l4.commons.serialization.standard.CompactDynamicSerializer;
import se.l4.commons.serialization.standard.DynamicSerializer;
import se.l4.commons.serialization.standard.SimpleTypeSerializer;
import se.l4.commons.types.reflect.ConstructorRef;
import se.l4.commons.types.reflect.FieldRef;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Serializer that will use reflection to access fields and methods in a
 * class. Will export anything annotated with {@link Expose}.
 *
 * <p>
 * <ul>
 * 	<li>{@link Named} can be used if you want a field to have a specific name
 * 		in serialized form.
 * 	<li>If you need to use a custom serializer for a field annotate it with
 * 		{@link Use}.
 * 	<li>{@link AllowAny} will cause dynamic serialization to be used for a
 * 		field.
 * </ul>
 *
 * @author Andreas Holstenson
 */
public class ReflectionSerializer<T>
	implements SerializerResolver<T>
{
	public ReflectionSerializer()
	{
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Optional<Serializer<T>> find(TypeEncounter encounter)
	{
		TypeRef type = encounter.getType();
		Serializers collection = encounter.getCollection();

		ImmutableMap.Builder<String, FieldDefinition> builder = ImmutableMap.builder();
		ImmutableMap.Builder<String, FieldDefinition> nonRenamedFields = ImmutableMap.builder();

		for(FieldRef field : type.getDeclaredFields())
		{
			if(! field.hasAnnotation(Expose.class))
			{
				continue;
			}

			// Resolve the serializer to use for the field
			Serializer<?> serializer;
			if(field.hasAnnotation(Use.class))
			{
				// Serializer has been set to a specific type
				Use annotation = field.getAnnotation(Use.class).get();
				serializer = collection.findVia((Class<SerializerOrResolver<?>>) annotation.value(), field.getType(), field.getAnnotations())
					.orElseThrow(() -> new SerializationException("Could not locate serializer for " + field.getType().toTypeDescription()));
			}
			else if(field.hasAnnotation(AllowAny.class))
			{
				AllowAny allowAny = field.getAnnotation(AllowAny.class).get();
				serializer = allowAny.compact()
					? new CompactDynamicSerializer(collection)
					: new DynamicSerializer(collection);
			}
			else if(field.hasAnnotation(AllowSimpleTypes.class))
			{
				serializer = new SimpleTypeSerializer();
			}
			else
			{
				// Dynamically find a suitable type
				serializer = collection.find(field.getType(), field.getAnnotations())
					.orElseThrow(() -> new SerializationException("Could not resolve " + field.getName() + " for " + type.getErasedType()));
			}

			if(serializer == null)
			{
				throw new SerializationException("Could not resolve " + field.getName() + " for " + type.getErasedType() + "; No serializer found");
			}

			boolean skipIfDefault = field.hasAnnotation(SkipDefaultValue.class);

			// Force the field to be accessible
			Field reflectiveField = field.getField();
			reflectiveField.setAccessible(true);

			// Define how we access this field
			String name = getName(reflectiveField);
			FieldDefinition def = new FieldDefinition(reflectiveField, name, serializer, field.getType().getErasedType(), skipIfDefault);
			builder.put(name, def);
			nonRenamedFields.put(reflectiveField.getName(), def);
		}

		// Create field map and cache
		ImmutableMap<String, FieldDefinition> fields = builder.build();
		ImmutableMap<String, FieldDefinition> nonRenamed = nonRenamedFields.build();
		FieldDefinition[] fieldsCache = fields.values().toArray(new FieldDefinition[0]);

		// Get all of the factories
		boolean hasSerializerInFactory = false;
		List<FactoryDefinition<T>> factories = Lists.newArrayList();

		for(ConstructorRef constructor : type.getConstructors())
		{
			FactoryDefinition<T> def = FactoryDefinition.resolve(collection, type, fields, nonRenamed, constructor);
			if(def == null) continue;

			hasSerializerInFactory |= def.hasSerializedFields();
			factories.add(def);
		}

		if(factories.isEmpty())
		{
			throw new SerializationException("Unable to create any instance of " + type + ", at least a default constructor is needed");
		}

		FactoryDefinition<T>[] factoryCache = factories.toArray(new FactoryDefinition[factories.size()]);

		// Create the actual serializer to use
		TypeInfo<T> typeInfo = new TypeInfo<T>((Class) type.getErasedType(), factoryCache, fields, fieldsCache);

		if(hasSerializerInFactory)
		{
			FactoryDefinition<T> factoryWithEverything = typeInfo.findSingleFactoryWithEverything();
			if(factoryWithEverything == null)
			{
				// There is no factory that takes in every single field, use a non-streaming serializer
				return Optional.of(new ReflectionNonStreamingSerializer<>(typeInfo));
			}
			else
			{
				return Optional.of(new ReflectionOnlySingleFactorySerializer<>(typeInfo, factoryWithEverything));
			}
		}
		else
		{
			return Optional.of(new ReflectionStreamingSerializer<>(typeInfo));
		}
	}

	private static String getName(Field field)
	{
		if(field.isAnnotationPresent(Expose.class))
		{
			Expose annotation = field.getAnnotation(Expose.class);
			if(! "".equals(annotation.value()))
			{
				return annotation.value();
			}
		}

		return field.getName();
	}
}
