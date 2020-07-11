package se.l4.commons.serialization;

import org.junit.Before;
import org.junit.Test;

public class DefaultSerializersTest
{
	private Serializers serializers;

	@Before
	public void before()
	{
		serializers = new DefaultSerializers();
	}

	@Test
	public void testUseAnnotation()
	{
		serializers.find(ClassWithUse.class)
			.orElseThrow(() -> new AssertionError("Could not find serializer"));
	}

	@Use(ReflectionSerializer.class)
	public static class ClassWithUse
	{
	}
}
