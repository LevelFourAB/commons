package se.l4.commons.serialization.collections;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import se.l4.commons.serialization.SerializerOrResolver;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD} )
@Documented
public @interface Item
{
	/**
	 * The class to use for serialization.
	 *
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	Class<? extends SerializerOrResolver> value();
}
