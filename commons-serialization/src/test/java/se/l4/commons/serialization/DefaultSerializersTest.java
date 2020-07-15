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
		serializers.find(ClassWithUse.class);
	}

	@Test
	public void testUseAnnotationExtension()
	{
		try
		{
			serializers.find(ClassExtendingUse.class);
		}
		catch(SerializationException e)
		{
			return;
		}

		throw new AssertionError("Should not be able to resolve sub-class serializer without @Use");
	}


	@Use(ReflectionSerializer.class)
	public static class ClassWithUse
	{
	}

	public static class ClassExtendingUse
		extends ClassWithUse
	{

	}
}
