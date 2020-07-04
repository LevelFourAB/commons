package se.l4.commons.serialization.internal;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

import se.l4.commons.serialization.SerializerCollection;
import se.l4.commons.serialization.spi.TypeEncounter;
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
	private final SerializerCollection collection;
	private final TypeRef type;
	private final List<Annotation> annotations;

	public TypeEncounterImpl(SerializerCollection collection,
			TypeRef type,
			List<Annotation> annotations)
	{
		this.collection = collection;
		this.type = type;
		this.annotations = annotations;
	}

	@Override
	public SerializerCollection getCollection()
	{
		return collection;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Annotation> Optional<T> getHint(Class<T> type)
	{
		if(annotations == null) return Optional.empty();

		for(Annotation a : annotations)
		{
			if(type.isAssignableFrom(a.annotationType()))
			{
				return Optional.of((T) a);
			}
		}

		return Optional.empty();
	}

	@Override
	public TypeRef getType()
	{
		return type;
	}
}
