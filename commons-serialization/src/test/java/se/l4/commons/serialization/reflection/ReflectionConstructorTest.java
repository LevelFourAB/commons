package se.l4.commons.serialization.reflection;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import se.l4.commons.serialization.DefaultSerializerCollection;
import se.l4.commons.serialization.Expose;
import se.l4.commons.serialization.ReflectionSerializer;
import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerCollection;
import se.l4.commons.serialization.format.JsonInput;
import se.l4.commons.serialization.format.JsonOutput;
import se.l4.commons.serialization.internal.TypeEncounterImpl;
import se.l4.commons.serialization.spi.TypeViaClass;

public class ReflectionConstructorTest
{
	private SerializerCollection collection;

	@Before
	public void beforeTests()
	{
		collection = new DefaultSerializerCollection();
	}

	@Test
	public void testDefaultConstructor()
	{
		Serializer<A> serializer = new ReflectionSerializer<A>()
			.find(new TypeEncounterImpl(collection, new TypeViaClass(A.class), Collections.<Annotation>emptyList()));

		A instance = new A();
		instance.field = "test value";
		testSymmetry(serializer, instance);
	}

	@Test
	public void testMultipleConstructors()
	{
		Serializer<B> serializer = new ReflectionSerializer<B>()
			.find(new TypeEncounterImpl(collection, new TypeViaClass(B.class), Collections.<Annotation>emptyList()));

		B instance = new B("test", "value");
		testSymmetry(serializer, instance);
	}

	@Test
	public void testSingleConstructor()
	{
		Serializer<C> serializer = new ReflectionSerializer<C>()
			.find(new TypeEncounterImpl(collection, new TypeViaClass(C.class), Collections.<Annotation>emptyList()));

		C instance = new C("test value");
		testSymmetry(serializer, instance);
	}

	@Test
	public void testSingleConstructorMixedTypes()
	{
		Serializer<D> serializer = new ReflectionSerializer<D>()
			.find(new TypeEncounterImpl(collection, new TypeViaClass(D.class), Collections.<Annotation>emptyList()));

		D instance = new D(2, "test value");
		testSymmetry(serializer, instance);
	}


	public static class A
	{
		@Expose
		private String field;

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((field == null) ? 0 : field.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if(this == obj)
				return true;
			if(obj == null)
				return false;
			if(getClass() != obj.getClass())
				return false;
			A other = (A) obj;
			if(field == null)
			{
				if(other.field != null)
					return false;
			}
			else if(!field.equals(other.field))
				return false;
			return true;
		}
	}

	public static class B
	{
		@Expose
		private final String field;

		@Expose
		private final String field2;

		public B(@Expose("field") String field, @Expose("field2") String field2)
		{
			this.field = field;
			this.field2 = field2;
		}

		public B(@Expose("field") String field)
		{
			this.field = field;
			this.field2 = null;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((field == null) ? 0 : field.hashCode());
			result = prime * result + ((field2 == null) ? 0 : field2.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if(this == obj)
				return true;
			if(obj == null)
				return false;
			if(getClass() != obj.getClass())
				return false;
			B other = (B) obj;
			if(field == null)
			{
				if(other.field != null)
					return false;
			}
			else if(!field.equals(other.field))
				return false;
			if(field2 == null)
			{
				if(other.field2 != null)
					return false;
			}
			else if(!field2.equals(other.field2))
				return false;
			return true;
		}
	}

	public static class C
	{
		@Expose
		private final String field;

		public C(@Expose("field") String field)
		{
			this.field = field;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((field == null) ? 0 : field.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if(this == obj)
				return true;
			if(obj == null)
				return false;
			if(getClass() != obj.getClass())
				return false;
			C other = (C) obj;
			if(field == null)
			{
				if(other.field != null)
					return false;
			}
			else if(!field.equals(other.field))
				return false;
			return true;
		}
	}

	public static class D
	{
		@Expose
		private final String field;

		@Expose
		private final int field2;

		public D(@Expose("field2") int field2, @Expose("field") String field)
		{
			this.field2 = field2;
			this.field = field;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((field == null) ? 0 : field.hashCode());
			result = prime * result + field2;
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if(this == obj)
				return true;
			if(obj == null)
				return false;
			if(getClass() != obj.getClass())
				return false;
			D other = (D) obj;
			if(field == null)
			{
				if(other.field != null)
					return false;
			}
			else if(!field.equals(other.field))
				return false;
			if(field2 != other.field2)
				return false;
			return true;
		}
	}

	private <T> void testSymmetry(Serializer<T> serializer, T instance)
	{
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			JsonOutput jsonOut = new JsonOutput(out);
			serializer.write(instance, "", jsonOut);
			jsonOut.flush();

			ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
			JsonInput jsonIn = new JsonInput(new InputStreamReader(in));
			T read = serializer.read(jsonIn);

			assertEquals("Deserialized instance does not match", instance, read);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
