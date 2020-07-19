package se.l4.commons.serialization.reflection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.Test;

import se.l4.commons.serialization.AllowAny;
import se.l4.commons.serialization.Expose;
import se.l4.commons.serialization.Serializer;

public class ReflectionCollectionTest
	extends ReflectionTest
{
	@Test
	public void testList()
	{
		Serializer<WithList> serializer = resolve(WithList.class);

		WithList instance = new WithList();
		instance.f1 = new ArrayList<>();
		testSymmetry(serializer, instance);
	}

	public static class WithList
	{
		@Expose
		public List<String> f1;

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			WithList other = (WithList) obj;
			return Objects.equals(f1, other.f1);
		}
	}

	@Test
	public void testListAllowAnyItem()
	{
		Serializer<WithListAllowAnyItem> serializer = resolve(WithListAllowAnyItem.class);

		WithListAllowAnyItem instance = new WithListAllowAnyItem();
		instance.f1 = new ArrayList<>();
		instance.f1.add("test");
		testSymmetry(serializer, instance);
	}

	public static class WithListAllowAnyItem
	{
		@Expose
		public List<@AllowAny Object> f1;

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			WithListAllowAnyItem other = (WithListAllowAnyItem) obj;
			return Objects.equals(f1, other.f1);
		}
	}
}
