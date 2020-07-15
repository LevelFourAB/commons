package se.l4.commons.types.matching;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.eclipse.collections.api.list.ListIterable;
import org.junit.Test;

public class ClassMatchingMapTest
{
	@Test
	public void testPutAndGet()
	{
		ClassMatchingUnifiedMap<Object, String> map = new ClassMatchingUnifiedMap<>();
		map.put(Object.class, "object");
		map.put(String.class, "string");

		assertThat(map.get(String.class).get(), is("string"));
		assertThat(map.get(Object.class).get(), is("object"));
		assertThat(map.get(Boolean.class).isPresent(), is(false));
	}

	@Test
	public void testSimpleHierarchyWithObjectRoot()
	{
		ClassMatchingUnifiedMap<Object, String> map = new ClassMatchingUnifiedMap<>();
		map.put(Object.class, "object");
		map.put(String.class, "string");

		assertThat(map.getBest(String.class).get(), is("string"));
		assertThat(map.getBest(Boolean.class).get(), is("object"));

		// Interfaces do not share a common root
		assertThat(map.getBest(Comparable.class).isPresent(), is(false));
	}

	@Test
	public void testInterfaceAndConcreteClass()
	{
		ClassMatchingUnifiedMap<Object, String> map = new ClassMatchingUnifiedMap<>();
		map.put(Comparable.class, "comparable");
		map.put(String.class, "string");

		assertThat(map.getBest(String.class).get(), is("string"));
		assertThat(map.getBest(Comparable.class).get(), is("comparable"));
		assertThat(map.getBest(Long.class).get(), is("comparable"));
	}

	@Test
	public void testInterfaceAndAbstractClass()
	{
		ClassMatchingUnifiedMap<Object, String> map = new ClassMatchingUnifiedMap<>();
		map.put(ClassMatchingMap.class, "map");
		map.put(AbstractClassMatchingMap.class, "abstract");

		assertThat(map.getBest(ClassMatchingUnifiedMap.class).get(), is("abstract"));

		// The fake map declares the interface closer to the top
		assertThat(map.getBest(FakeMatchingMap.class).get(), is("map"));
	}

	@Test
	public void testAllMatching()
	{
		ClassMatchingUnifiedMap<Object, String> map = new ClassMatchingUnifiedMap<>();
		map.put(ClassMatchingMap.class, "map");
		map.put(AbstractClassMatchingMap.class, "abstract");

		ListIterable<MatchedType<Object, String>> all = map.getAll(ClassMatchingUnifiedMap.class);
		assertThat(all, notNullValue());
		assertThat(all.size(), is(2));

		assertThat(all.get(0).getData(), is("abstract"));
		assertThat(all.get(1).getData(), is("map"));
	}

	// Map only used for running tests against
	private static class FakeMatchingMap<D, T>
		extends AbstractClassMatchingMap<D, T>
		implements ClassMatchingMap<D, T>
	{
		public FakeMatchingMap()
		{
			super(null);
		}

		@Override
		public ClassMatchingMap<D, T> toImmutable()
		{
			return null;
		}

		@Override
		public MutableClassMatchingMap<D, T> toMutable()
		{
			return null;
		}
	}
}
