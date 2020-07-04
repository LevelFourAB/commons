package se.l4.commons.serialization.spi;

import java.lang.annotation.Annotation;
import java.util.Optional;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import se.l4.commons.serialization.SerializerCollection;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Encounter with a specific type during serialization resolution.
 *
 * @author Andreas Holstenson
 *
 */
public interface TypeEncounter
{
	/**
	 * Get the collection this encounter is for.
	 *
	 * @return
	 */
	@NonNull
	SerializerCollection getCollection();

	/**
	 * Get the type encountered.
	 *
	 * @return
	 */
	@NonNull
	TypeRef getType();

	/**
	 * Fetch a hint of the specific type if available.
	 *
	 * @param type
	 * @return
	 * 	 the hint if found, or {@code null}
	 */
	@Nullable
	<T extends Annotation> Optional<T> getHint(@NonNull Class<T> type);
}
