package se.l4.commons.types.reflect;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Test;

import se.l4.commons.types.Types;

public class MethodRefTest
{
	@Test
	public void testReturnType()
	{
		class Test
		{
			public String get() { return null; }
		}

		TypeRef type = Types.reference(Test.class);
		MethodRef method = type.getMethod("get").get();
		assertThat(method.getReturnType().getErasedType(), is((Object) String.class));
	}

	@Test
	public void testReturnTypeAnnotated()
	{
		class Test
		{
			@TestAnnotation
			public String get() { return null; }
		}

		TypeRef type = Types.reference(Test.class);
		MethodRef method = type.getMethod("get").get();
		assertThat("method has annotation", method.hasAnnotation(TestAnnotation.class), is(true));

		TypeRef returnType = method.getReturnType();
		assertThat("erased type is String", returnType.getErasedType(), is((Object) String.class));
		assertThat("return type has annotation", returnType.getUsage().hasAnnotation(TestAnnotation.class), is(true));
	}

	@Retention(RetentionPolicy.RUNTIME)
	public static @interface TestAnnotation
	{

	}
}
