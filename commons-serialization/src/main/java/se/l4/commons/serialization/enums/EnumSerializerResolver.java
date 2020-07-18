package se.l4.commons.serialization.enums;

import java.lang.reflect.Constructor;
import java.util.Optional;

import com.google.common.base.Throwables;

import se.l4.commons.serialization.SerializationException;
import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerResolver;
import se.l4.commons.serialization.TypeEncounter;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Resolver for {@link Enum enums}, can handle any enum type and supports
 * different translators between serialized and object form.
 *
 * @author Andreas Holstenson
 *
 */
public class EnumSerializerResolver
	implements SerializerResolver<Enum<?>>
{
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Optional<Serializer<Enum<?>>> find(TypeEncounter encounter)
	{
		TypeRef type = encounter.getType();

		Optional<MapEnumVia> hint = encounter.getHint(MapEnumVia.class);
		ValueTranslator translator;
		if(hint.isPresent())
		{
			translator = create(hint.get().value(), type.getErasedType());
		}
		else if(type.hasAnnotation(MapEnumVia.class))
		{
			MapEnumVia mv = type.getAnnotation(MapEnumVia.class).get();
			translator = create(mv.value(), type.getErasedType());
		}
		else
		{
			translator = new NameTranslator((Class) type.getErasedType());
		}

		return Optional.of(new EnumSerializer(translator));
	}

	@SuppressWarnings("rawtypes")
	private ValueTranslator create(
		Class<? extends ValueTranslator> translator,
		Class<?> type
	)
	{
		for(Constructor c : translator.getConstructors())
		{
			Class[] types = c.getParameterTypes();
			if(types.length != 1) continue;

			Class<?> t = types[0];
			if(t.isAssignableFrom(Class.class))
			{
				try
				{
					return (ValueTranslator) c.newInstance(type);
				}
				catch(InstantiationException e)
				{
					Throwables.throwIfInstanceOf(e.getCause(), SerializationException.class);

					throw new SerializationException("Unable to create; " + e.getCause().getMessage(), e.getCause());
				}
				catch(Exception e)
				{
					throw new SerializationException("Unable to create; " + e.getMessage(), e);
				}
			}
		}

		throw new SerializationException("Constructor that takes Enum is required (for " + translator + ")");
	}
}
