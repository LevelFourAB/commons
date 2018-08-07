package se.l4.commons.serialization;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate that a field may contain any simple type, which is all primtive
 * types and {@link String}, the field may not contain any object.
 *
 * @author Andreas Holstenson
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Documented
public @interface AllowSimpleTypes
{
	/**
	 * Deprecated value for compact serialization that was never used.
	 *
	 * @deprecated
	 * @return
	 */
	@Deprecated
	boolean compact() default false;
}
