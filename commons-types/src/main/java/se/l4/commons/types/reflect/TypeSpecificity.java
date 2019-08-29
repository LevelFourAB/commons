package se.l4.commons.types.reflect;

/**
 * Define how type specificity should be compared.
 */
public enum TypeSpecificity
{
	/**
	 * Allow only the exact same type.
	 */
	IDENTICAL,

	/**
	 * Allow types that are less specific, that is that they are the superclass
	 * or an interface of the type being checked.
	 */
	LESS,

	/**
	 * Allow types that are more specific, that is that they are a subclass
	 * or implemented interface for the type being checked.
	 */
	MORE;
}
