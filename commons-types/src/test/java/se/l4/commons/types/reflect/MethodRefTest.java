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

	@Test
	public void testFindIn()
	{
		class A
		{
			Object m(String a)
			{
				return "";
			}
		}

		class B extends A
		{
			@Override
			String m(String a)
			{
				return "";
			}
		}

		TypeRef type = Types.reference(A.class);
		MethodRef inA = type.getDeclaredMethodViaClassParameters("m", String.class).get();

		MethodRef inB = inA.findIn(Types.reference(B.class), TypeSpecificity.MORE).get();

		MethodRef inA2 = inB.findIn(type, TypeSpecificity.LESS).get();
	}

	@Retention(RetentionPolicy.RUNTIME)
	public static @interface TestAnnotation
	{

	}

}
