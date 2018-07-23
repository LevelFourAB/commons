package se.l4.commons.serialization;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate that a certain field should be exposed in the serialized form.
 *
 * @author Andreas Holstenson
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Documented
public @interface Expose
{
	/**
	 * Get the name of the exposed value. Default is to automatically resolve
	 * this.
	 *
	 * @return
	 */
	String value() default "";
}