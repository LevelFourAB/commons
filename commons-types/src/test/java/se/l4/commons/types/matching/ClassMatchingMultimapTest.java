package se.l4.commons.types.matching;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.eclipse.collections.api.list.ListIterable;
import org.junit.Test;

public class ClassMatchingMultimapTest
{
	@Test
	public void testPutAndGet()
	{
		ClassMatchingFastListMultimap<Object, String> map = new ClassMatchingFastListMultimap<>();
		map.put(Object.class, "object1");
		map.put(Object.class, "object2");
		map.put(String.class, "string");

		ListIterable<String> matching = map.get(String.class);
		assertThat(matching, notNullValue());
		assertThat(matching.size(), is(1));
		assertThat(matching, hasItem("string"));

		matching = map.get(Object.class);
		assertThat(matching, notNullValue());
		assertThat(matching.size(), is(2));
		assertThat(matching, hasItems("object1", "object2"));

		matching = map.get(Boolean.class);
		assertThat(matching, notNullValue());
		assertThat(matching.size(), is(0));
	}

	@Test
	public void testSimpleHierarchyWithObjectRoot()
	{
		ClassMatchingFastListMultimap<Object, String> map = new ClassMatchingFastListMultimap<>();
		map.put(Object.class, "object");
		map.put(String.class, "string");

		ListIterable<String> matching = map.getBest(String.class);
		assertThat(matching, notNullValue());
		assertThat(matching.size(), is(1));
		assertThat(matching, hasItem("string"));

		matching = map.getBest(Object.class);
		assertThat(matching, notNullValue());
		assertThat(matching.size(), is(1));
		assertThat(matching, hasItem("object"));

		// Interfaces do not share a common root
		matching = map.getBest(Comparable.class);
		assertThat(matching, notNullValue());
		assertThat(matching.size(), is(0));
	}

	@Test
	public void testInterfaceAndConcreteClass()
	{
		ClassMatchingFastListMultimap<Object, String> map = new ClassMatchingFastListMultimap<>();
		map.put(Comparable.class, "comparable");
		map.put(String.class, "string");

		ListIterable<String> matching = map.getBest(String.class);
		assertThat(matching, notNullValue());
		assertThat(matching.size(), is(1));
		assertThat(matching, hasItem("string"));

		matching = map.getBest(Comparable.class);
		assertThat(matching, notNullValue());
		assertThat(matching.size(), is(1));
		assertThat(matching, hasItem("comparable"));

		matching = map.getBest(Long.class);
		assertThat(matching, notNullValue());
		assertThat(matching.size(), is(1));
		assertThat(matching, hasItem("comparable"));
	}

	@Test
	public void testMultipleConcreteClasses()
	{
		ClassMatchingFastListMultimap<Object, String> map = new ClassMatchingFastListMultimap<>();
		map.put(Comparable.class, "comparable");
		map.put(String.class, "string1");
		map.put(String.class, "string2");

		ListIterable<String> matching = map.getBest(String.class);
		assertThat(matching, notNullValue());
		assertThat(matching.size(), is(2));
		assertThat(matching, hasItems("string1", "string2"));

		matching = map.getBest(Comparable.class);
		assertThat(matching, notNullValue());
		assertThat(matching.size(), is(1));
		assertThat(matching, hasItem("comparable"));
	}

	@Test
	public void testInterfaceAndAbstractClass()
	{
		ClassMatchingFastListMultimap<Object, String> map = new ClassMatchingFastListMultimap<>();
		map.put(ClassMatchingMap.class, "map");
		map.put(AbstractClassMatchingMap.class, "abstract");

		ListIterable<String> matching = map.getBest(ClassMatchingUnifiedMap.class);
		assertThat(matching, notNullValue());
		assertThat(matching.size(), is(1));
		assertThat(matching, hasItem("abstract"));

		matching = map.getBest(FakeMatchingMap.class);
		assertThat(matching, notNullValue());
		assertThat(matching.size(), is(1));
		assertThat(matching, hasItem("map"));
	}

	@Test
	public void testAllMatching()
	{
		ClassMatchingFastListMultimap<Object, String> map = new ClassMatchingFastListMultimap<>();
		map.put(ClassMatchingMap.class, "map1");
		map.put(ClassMatchingMap.class, "map2");
		map.put(AbstractClassMatchingMap.class, "abstract");

		ListIterable<MatchedType<Object, String>> all = map.getAll(ClassMatchingUnifiedMap.class);
		assertThat(all, notNullValue());
		assertThat(all.size(), is(3));

		assertThat(all.get(0).getData(), is("abstract"));
		assertThat(all.get(1).getData(), anyOf(is("map1"), is("map2")));
		assertThat(all.get(2).getData(), anyOf(is("map1"), is("map2")));
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
