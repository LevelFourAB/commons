package se.l4.commons.id;

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
	 *   encoded version of the id
	 */
	T encode(long id);

	/**
	 * Decode the given identifier.
	 *
	 * @param in
	 *   the input to decode
	 * @return
	 *   parsed identifier
	 */
	long decode(T in);
}
