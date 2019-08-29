package se.l4.commons.types.reflect;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Reference to a {@link Method}. Method references are retrieved via
 * {@link TypeRef} and help with resolving generics.
 */
public interface MethodRef
	extends ExecutableRef
{
	/**
	 * Get the method this reference points to.
	 */
	Method getMethod();

	/**
	 * Get if this method is abstract.
	 *
	 * @return
	 */
	boolean isAbstract();

	/**
	 * Get if this method is static.
	 *
	 * @return
	 */
	boolean isStatic();

	/**
	 * Get if this method is final.
	 *
	 * @return
	 */
	boolean isFinal();

	/**
	 * Get if this method is synchronized.
	 *
	 * @return
	 */
	boolean isSynchronized();

	/**
	 * Get if this method is native.
	 *
	 * @return
	 */
	boolean isNative();

	/**
	 * Get if this method is strict.
	 *
	 * @return
	 */
	boolean isStrict();

	/**
	 * Get if this is a bridge method.
	 *
	 * @return
	 */
	boolean isBridge();

	/**
	 * Get if this is a default method.
	 *
	 * @return
	 */
	boolean isDefault();

	/**
	 * Find this method in the specified type. This will look for a method
	 * with the same name and parameters in the type, with an additional check
	 * for specificity and visibility.
	 *
	 * The return return type specificity can be be controlled, so that its
	 * possible to find types in both superclasses and subclasses. If you
	 * are looking for a method in a superclass or superinterface of the
	 * declaring class use {@link TypeSpecificity#LESS}. If you are looking for
	 * how a method is implemented in a subclass or implementor of an interface
	 * you can use {@link TypeSpecificity#MORE}.
	 *
	 * @param type
	 * @return
	 */
	Optional<MethodRef> findIn(TypeRef type, TypeSpecificity returnTypeSpecificity);
}
