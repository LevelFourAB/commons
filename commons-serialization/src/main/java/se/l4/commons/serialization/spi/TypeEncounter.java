package se.l4.commons.serialization.spi;

import java.lang.annotation.Annotation;

import se.l4.commons.serialization.SerializerCollection;

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
	SerializerCollection getCollection();

	/**
	 * Get the type encountered.
	 *
	 * @return
	 */
	Type getType();

	/**
	 * Fetch a hint of the specific type if available.
	 *
	 * @param type
	 * @return
	 * 		the hint if found, or {@code null}
	 */
	<T extends Annotation> T getHint(Class<T> type);
}
