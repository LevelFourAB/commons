package se.l4.commons.serialization.enums;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.Set;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;

import se.l4.commons.serialization.SerializationException;
import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.spi.SerializerResolver;
import se.l4.commons.serialization.spi.TypeEncounter;
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
	private static final Set<Class<? extends Annotation>> HINTS =
		ImmutableSet.<Class<? extends Annotation>>of(MapEnumVia.class);

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

	@Override
	public Set<Class<? extends Annotation>> getHints()
	{
		return HINTS;
	}
}
