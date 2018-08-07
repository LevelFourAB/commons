package se.l4.commons.serialization;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate that a field should not be written to the output if it is the
 * types default value. This annotation is handled correctly if a class uses
 * {@link ReflectionSerializer}.
 *
 * <p>
 * Example:
 *
 * <pre>
 * @Use(ReflectionSerializer.class)
 * public class PersonData {
 *   @Expose
 *   private final String name;
 *
 *   @Expose
 *   @SkipDefaultValue
 *   private final String title;
 *
 *   public PersonData(@Expose("name") String name, @Expose("title") String title) {
 *     this.name = name;
 *     this.title = title;
 *   }
 *
 *   // ... getters and other code here ...
 * }
 *
 * // This object will write the key `title` with the value `Engineer`
 * new PersonData("Emma Smith", "Engineer");
 *
 * // This object will skip the key `title` entirely
 * new PersonData("John Smith", null);
 * </pre>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Documented
public @interface SkipDefaultValue
{

}
