package se.l4.commons.types.matching;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import edu.umd.cs.findbugs.annotations.NonNull;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Specialized map to associated classes and interfaces with data. This map
 * supports matching against the type hierarchy to fetch data.
 */
public interface TypeMatchingMap<D>
{
	/**
	 * Associated some data with a type. This will allow matching against
	 * the type and its subclasses or in the case of interfaces its
	 * implementors.
	 *
	 * @param type
	 *   the type to associate data with
	 * @param data
	 *   the data to store, never {@code null}
	 */
	void put(@NonNull TypeRef type, @NonNull D data);

	/**
	 * Get data directly associated with the given type.
	 *
	 * @param type
	 *   the type get data for
	 * @return
	 *   optional containing data if there is direct match or empty optional
	 */
	@NonNull
	Optional<D> get(@NonNull TypeRef type);

	/**
	 * Get data directly associated with the given type or create it if it
	 * there is no value present.
	 *
	 * @param type
	 *   the type get data for
	 * @return
	 *   optional containing data if there is direct match or empty optional
	 */
	@NonNull
	default Optional<D> get(@NonNull TypeRef type, Function<TypeRef, D> creator)
	{
		Optional<D> result = get(type);
		if(! result.isPresent())
		{
			D created = creator.apply(type);
			put(type, created);

			return Optional.of(created);
		}

		return result;
	}

	/**
	 * Get the best matching data for the given type.
	 *
	 * @param type
	 *   class to match against
	 * @return
	 *   optional with data or empty optional
	 */
	@NonNull
	Optional<D> getBest(@NonNull TypeRef type);

	/**
	 * Get all types and their associated data matching the given type.
	 *
	 * @param type
	 *   the type to match against
	 * @return
	 *   list of matching entries. The list will be empty if no matches are
	 *   available.
	 */
	@NonNull
	List<MatchedTypeRef<D>> getAll(@NonNull TypeRef type);

	/**
	 * Get all of the entries in this map without doing any matching.
	 *
	 * @return
	 *   immutable list of all entries registered
	 */
	List<MatchedTypeRef<D>> entries();
}