package se.l4.commons.serialization;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import se.l4.commons.serialization.collections.ArraySerializerResolver;
import se.l4.commons.serialization.internal.DelayedSerializer;
import se.l4.commons.serialization.internal.SerializerResolverAdapter;
import se.l4.commons.serialization.internal.TypeEncounterImpl;
import se.l4.commons.serialization.internal.UseSerializerResolver;
import se.l4.commons.types.Types;
import se.l4.commons.types.mapping.Mapped;
import se.l4.commons.types.mapping.MutableTypeMapper;
import se.l4.commons.types.mapping.OutputDeduplicator;
import se.l4.commons.types.reflect.TypeRef;
import se.l4.commons.types.reflect.TypeUsage;

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

	protected final Map<QualifiedName, Serializer<?>> nameToSerializer;
	protected final MutableTypeMapper<TypeEncounter, Serializer<?>> mapper;
	protected final OutputDeduplicator<Serializer<?>> deduplicator;

	public AbstractSerializers()
	{
		nameToSerializer = new ConcurrentHashMap<QualifiedName, Serializer<?>>();

		deduplicator = createDeduplicator();
		mapper = MutableTypeMapper.create(this::createEncounter)
			.withCaching(100)
			.withOutputDeduplication(deduplicator)
			.build();

		mapper.addAnnotationResolver(new SerializerResolverAdapter(new ArraySerializerResolver()));
		mapper.addAnnotationResolver(new SerializerResolverAdapter(new UseSerializerResolver()));
	}

	private OutputDeduplicator<Serializer<?>> createDeduplicator()
	{
		OutputDeduplicator<Serializer<?>> actual = OutputDeduplicator.weak();
		return o -> {
			Serializer<?> dedup = actual.deduplicate(o);
			if(dedup.getName().isPresent())
			{
				nameToSerializer.put(dedup.getName().get(), dedup);
			}

			return dedup;
		};
	}

	private TypeEncounter createEncounter(TypeRef type)
	{
		return new TypeEncounterImpl(this, deduplicator, type);
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
		mapper.addSpecific(type, serializer);
		return this;
	}

	@Override
	public <T> Serializers bind(Class<T> type, SerializerResolver<? extends T> resolver)
	{
		mapper.addHierarchyResolver(type, new SerializerResolverAdapter(resolver));
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
	public <T> Serializer<T> find(Class<T> type, Iterable<? extends Annotation> hints)
	{
		return (Serializer) find(Types.reference(type).withUsage(TypeUsage.forAnnotations(hints)));
	}

	@Override
	public Serializer<?> find(TypeRef type)
	{
		Set<TypeRef> s = stack.get();
		if(s != null && s.contains(type))
		{
			// Already trying to create this serializer, delay creation
			return new DelayedSerializer<>(this, type);
		}

		// Stack to keep track of circular dependencies
		if(s == null)
		{
			s = new HashSet<>();
			stack.set(s);
		}

		try
		{
			s.add(type);

			Mapped<Serializer<?>> mapped = resolve(type);

			return mapped.asOptional(() -> new SerializationException("Could not create serializer for " + type.toTypeDescription()))
				.orElseThrow(() -> new SerializationException("No serializer available for " + type.toTypeDescription()));
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

	protected Mapped<Serializer<?>> resolve(TypeRef type)
	{
		return mapper.get(type);
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
	public boolean isSupported(Class<?> type)
	{
		try
		{
			find(type);
			return true;
		}
		catch(SerializationException e)
		{
			return false;
		}
	}
}
