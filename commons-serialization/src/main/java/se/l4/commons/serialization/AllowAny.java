package se.l4.commons.serialization;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import se.l4.commons.serialization.standard.DynamicSerializer;

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
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.TYPE_USE })
@Documented
@Use(DynamicSerializer.class)
public @interface AllowAny
{
}
