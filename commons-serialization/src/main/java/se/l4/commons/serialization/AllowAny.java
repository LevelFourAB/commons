package se.l4.commons.serialization;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate that a field may contain any type that is compatible with the
 * declaration. This will cause the library to use dynamic serialization based
 * on names for the field. This annotation is handled when using
 * {@link ReflectionSerializer}.
 *
 * <p>
 * Example:
 *
 * <pre>
 * @Use(ReflectionSerializer.class)
 * public class Data {
 *   @Expose
 *   @AllowAny
 *   private Object anyObject;
 * }
 * </pre>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Documented
public @interface AllowAny
{
	/**
	 * Set if this should use the compact format or not. The compact format
	 * will write the container as a list, while the non-compact format is
	 * an object with the keys {@code namespace}, {@code name}, {@code value}.
	 *
	 * @return
	 */
	boolean compact() default false;
}
