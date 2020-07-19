package se.l4.commons.serialization.reflection;

import org.junit.Test;

import se.l4.commons.serialization.Expose;
import se.l4.commons.serialization.Serializer;

public class ReflectionConstructorTest
	extends ReflectionTest
{
	@Test
	public void testDefaultConstructor()
	{
		Serializer<A> serializer = resolve(A.class);

		A instance = new A();
		instance.field = "test value";
		testSymmetry(serializer, instance);
	}

	@Test
	public void testMultipleConstructors()
	{
		Serializer<B> serializer =  resolve(B.class);

		B instance = new B("test", "value");
		testSymmetry(serializer, instance);
	}

	@Test
	public void testSingleConstructor()
	{
		Serializer<C> serializer =  resolve(C.class);

		C instance = new C("test value");
		testSymmetry(serializer, instance);
	}

	@Test
	public void testSingleConstructorMixedTypes()
	{
		Serializer<D> serializer =  resolve(D.class);

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
}
