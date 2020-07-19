package se.l4.commons.serialization.reflection;

import org.junit.Test;

import se.l4.commons.serialization.Expose;
import se.l4.commons.serialization.Serializer;

public class ReflectionPrimitivesTest
	extends ReflectionTest
{
	@Test
	public void testBooleanUnwrapped()
	{
		Serializer<BooleanUnwrapped> serializer = resolve(BooleanUnwrapped.class);

		BooleanUnwrapped instance = new BooleanUnwrapped();
		instance.f1 = true;
		testSymmetry(serializer, instance);
	}

	public static class BooleanUnwrapped
	{
		@Expose
		public boolean f1;

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			BooleanUnwrapped other = (BooleanUnwrapped) obj;
			return f1 == other.f1;
		}
	}

	@Test
	public void testBooleanWrapped()
	{
		Serializer<BooleanWrapped> serializer = resolve(BooleanWrapped.class);

		BooleanWrapped instance = new BooleanWrapped();
		instance.f1 = true;
		testSymmetry(serializer, instance);
	}

	public static class BooleanWrapped
	{
		@Expose
		public Boolean f1;

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			BooleanWrapped other = (BooleanWrapped) obj;
			return f1.equals(other.f1);
		}
	}

	@Test
	public void testIntegerUnwrapped()
	{
		Serializer<IntegerUnwrapped> serializer = resolve(IntegerUnwrapped.class);

		IntegerUnwrapped instance = new IntegerUnwrapped();
		instance.f1 = 12;
		testSymmetry(serializer, instance);
	}

	public static class IntegerUnwrapped
	{
		@Expose
		public int f1;

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			IntegerUnwrapped other = (IntegerUnwrapped) obj;
			return f1 == other.f1;
		}
	}

	@Test
	public void testIntegerWrapped()
	{
		Serializer<IntegerWrapped> serializer = resolve(IntegerWrapped.class);

		IntegerWrapped instance = new IntegerWrapped();
		instance.f1 = 512;
		testSymmetry(serializer, instance);
	}

	public static class IntegerWrapped
	{
		@Expose
		public Integer f1;

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			IntegerWrapped other = (IntegerWrapped) obj;
			return f1.equals(other.f1);
		}
	}
}
