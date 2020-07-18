package se.l4.commons.types.mapping;

import edu.umd.cs.findbugs.annotations.NonNull;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Utility for helping with mapping any type into a specific type of result.
 * Type mapping is a common task for things like serialization libraries,
 * GraphQL mappings or RPC libraries.
 *
 * <p>
 * This type of mapper uses {@link Resolver}s that can are asked to resolve
 * the output type. These are used in a hierarchy, so a resolver tied to
 * {@code Object} will be asked for every type and a resolver tied to {@code List}
 * asked for any type that implements list. It is up to the resolver to
 * determine if it can resolve the type and it may look at things such as the
 * annotations and the exact type.
 *
 * <pre>
 * MutableTypeMapper<SerializerEncounter, Serializer<?>> mapper = MutableTypeMapper
 *  .create(type -> new SerializerEncounter(...))
 * 	.build();
 *
 * mapper.addSpecific(String.class, new StringSerializer());
 * mapper.addAnnotationResolver(new UseAnnotationResolver());
 *
 * Mapped<Serializer<?>> result = mapper.get(String.class);
 * </pre>
 */
public interface TypeMapper<I extends ResolutionEncounter<O>, O>
{
	/**
	 * Attempt to map the given type. This will create a {@link TypeRef} and
	 * pass this to {@link #get(TypeRef)}.
	 *
	 * @param type
	 * @return
	 */
	@NonNull
	Mapped<O> get(@NonNull Class<?> type);

	/**
	 * Attempt to map the given type. This will ask all of the resolvers if
	 * they can resolve the output for this type and return the first match.
	 * If any errors occur those will be reported in the returned result.
	 *
	 * @param type
	 * @return
	 */
	@NonNull
	Mapped<O> get(@NonNull TypeRef type);

	/**
	 * Strategy used for error handling in {@link Resolver}s.
	 */
	enum ErrorStrategy
	{
		/**
		 * Break whenever an error is encountered in any matching resolver.
		 */
		BREAK,

		/**
		 * Continue invoking the next resolver until all matching resolvers
		 * have been asked or one returns a result.
		 */
		CONTINUE;
	}
}
