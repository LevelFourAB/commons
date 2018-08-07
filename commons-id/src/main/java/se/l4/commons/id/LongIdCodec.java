package se.l4.commons.id;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Codec for encoding and decoding an identifier.
 */
public interface LongIdCodec<T>
{
	/**
	 * Encode the given identifier.
	 *
	 * @param id
	 *   the identifier to encode
	 * @return
	 *   encoded version of the id, never {@code null}
	 * @throws NumberFormatException
	 *   if unable to format the given id
	 */
	@NonNull
	T encode(long id);

	/**
	 * Decode the given identifier.
	 *
	 * @param in
	 *   the input to decode
	 * @return
	 *   parsed identifier
	 * @throws NumberFormatException
	 *   if unable to decode the given input
	 */
	long decode(@NonNull T in);
}
