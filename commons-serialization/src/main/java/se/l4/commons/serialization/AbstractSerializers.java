package se.l4.commons.serialization;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import se.l4.commons.serialization.internal.DelayedSerializer;
import se.l4.commons.serialization.internal.TypeEncounterImpl;
import se.l4.commons.serialization.spi.SerializerResolver;
import se.l4.commons.serialization.spi.StaticSerializerResolver;
import se.l4.commons.types.Types;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Default implementation of {@link Serializers}.
 *
 * @author Andreas Holstenson
 *
 */
public abstract class AbstractSerializers
	implements Serializers
{
	private static final ThreadLocal<Set<TypeRef>> stack = new ThreadLocal<Set<TypeRef>>();

	private final Map<QualifiedName, Serializer<?>> nameToSerializer;
	private final Map<Serializer<?>, QualifiedName> serializerToName;
	private final Map<CacheKey, Serializer<?>> serializers;

	public AbstractSerializers()
	{
		nameToSerializer = new ConcurrentHashMap<QualifiedName, Serializer<?>>();
		serializerToName = new ConcurrentHashMap<Serializer<?>, QualifiedName>();
		serializers = new ConcurrentHashMap<>();
	}

	protected <T> void bind(Class<T> type, Serializer<T> serializer, String ns, String name)
	{
		bind(type, new StaticSerializerResolver<T>(type, serializer));

		QualifiedName qname = new QualifiedName(ns, name);
		nameToSerializer.put(qname, serializer);
		serializerToName.put(serializer, qname);
	}

	@Override
	public Serializers bind(Class<?> type)
	{
		find(type);

		return this;
	}

	@Override
	public <T> Serializers bind(Class<T> type, Serializer<T> serializer)
	{
		bind(type, new StaticSerializerResolver<T>(type, serializer));

		registerIfNamed(type, serializer);

		return this;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> Serializer<T> find(Class<T> type)
	{
		return (Serializer) find(Types.reference(type));
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> Serializer<T> find(Class<T> type, Annotation... hints)
	{
		return (Serializer) find(Types.reference(type), hints);
	}

	@Override
	public Serializer<?> find(TypeRef type)
	{
		return find(type, (Annotation[]) null);
	}

	@Override
	public Serializer<?> find(TypeRef type, Annotation... hints)
	{
		Set<TypeRef> s = stack.get();
		if(s != null && s.contains(type))
		{
			// Already trying to create this serializer, delay creation
			return new DelayedSerializer<>(this, type, hints);
		}

		// Locate the resolver to use
		return getResolver(type.getErasedType())
			.map(r -> createVia(r, type, hints))
			.orElseThrow(() -> new SerializationException("No serializer found for " + type.toTypeDescription()));
	}

	@Override
	public Optional<? extends Serializer<?>> find(String name)
	{
		return find("", name);
	}

	@Override
	public Optional<? extends Serializer<?>> find(QualifiedName name)
	{
		return find(name.getNamespace(), name.getName());
	}

	@Override
	public Optional<? extends Serializer<?>> find(String namespace, String name)
	{
		return Optional.ofNullable(nameToSerializer.get(new QualifiedName(namespace, name)));
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> Serializer<T> findVia(Class<? extends SerializerOrResolver<T>> resolver, Class<T> type, Annotation... hints)
	{
		return (Serializer) findVia(resolver, Types.reference(type), hints);
	}

	@Override
	@SuppressWarnings({ "rawtypes" })
	public Serializer<?> findVia(Class<? extends SerializerOrResolver<?>> resolver, TypeRef type, Annotation... hints)
	{
		SerializerOrResolver<?> instance = getInstanceFactory().create(resolver);
		if(instance instanceof Serializer)
		{
			return (Serializer<?>) instance;
		}
		else
		{
			return (Serializer) createVia((SerializerResolver) instance, type, hints);
		}
	}

	/**
	 * Create a new {@link Serializer} for the given type and hints via a
	 * specific {@link SerializerResolver resolver} instance.
	 *
	 * @param resolver
	 * @param type
	 * @param hints
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	protected Serializer createVia(SerializerResolver<?> resolver, TypeRef type, Annotation... hints)
	{
		// Only expose the hints that the resolver has declared
		Set<Class<? extends Annotation>> hintsUsed = resolver.getHints();
		List<Annotation> hintsActive;
		if(hintsUsed == null || hints == null || hints.length == 0)
		{
			hintsActive = Collections.emptyList();
		}
		else
		{
			hintsActive = new ArrayList<>();
			for(Annotation a : hints)
			{
				if(hintsUsed.contains(a.annotationType()))
				{
					hintsActive.add(a);
				}
			}
		}

		// Check if we have already built a serializer for this type
		CacheKey key = new CacheKey(type, hintsActive.toArray());
		Serializer<?> serializer = serializers.get(key);
		if(serializer != null)
		{
			return serializer;
		}

		// Stack to keep track of circular dependencies
		Set<TypeRef> s = stack.get();
		if(s == null)
		{
			s = new HashSet<>();
			stack.set(s);
		}

		try
		{
			s.add(type);

			// Find a serializer to use
			TypeEncounterImpl encounter = new TypeEncounterImpl(this, type, hintsActive);

			SerializerOrResolver<?> serializerOrResolver = resolver.find(encounter)
				.orElseThrow(() -> new SerializationException("Unable to find serializer for " + type.toTypeDescription() + " using " + resolver.getClass()));

			if(serializerOrResolver instanceof Serializer)
			{
				serializer = (Serializer) serializerOrResolver;

				registerIfNamed(type.getErasedType(), serializer);

				// Store the found serializer in the cache
				serializers.put(key, serializer);
				return serializer;
			}
			else
			{
				return createVia((SerializerResolver) serializerOrResolver, type, hints);
			}
		}
		finally
		{
			s.remove(type);

			if(s.isEmpty())
			{
				stack.remove();
			}
		}
	}

	@Override
	public Optional<QualifiedName> findName(Serializer<?> serializer)
	{
		return Optional.ofNullable(serializerToName.get(serializer));
	}

	@Override
	public boolean isSupported(Class<?> type)
	{
		Optional<? extends SerializerResolver<?>> resolver = getResolver(type);
		if(! resolver.isPresent())
		{
			return false;
		}

		if(resolver.get() instanceof StaticSerializerResolver)
		{
			return true;
		}

		return resolver.get()
			.find(new TypeEncounterImpl(this, Types.reference(type), null))
			.isPresent();
	}

	/**
	 * Register the given serializer if it has a name.
	 *
	 * @param from
	 * @param serializer
	 */
	protected void registerIfNamed(Class<?> from, Serializer<?> serializer)
	{
		if(from.isAnnotationPresent(Named.class))
		{
			Named named = from.getAnnotation(Named.class);
			QualifiedName key = new QualifiedName(named.namespace(), named.name());
			nameToSerializer.put(key, serializer);
			serializerToName.put(serializer, key);
		}
	}

	private static class CacheKey
	{
		private final TypeRef type;
		private final Object[] hints;

		public CacheKey(TypeRef type, Object[] hints)
		{
			this.type = type;
			this.hints = hints;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(hints);
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if(this == obj)
				return true;
			if(obj == null)
				return false;
			if(getClass() != obj.getClass())
				return false;
			CacheKey other = (CacheKey) obj;
			if(!Arrays.equals(hints, other.hints))
				return false;
			if(type == null)
			{
				if(other.type != null)
					return false;
			}
			else if(!type.equals(other.type))
				return false;
			return true;
		}
	}
}
