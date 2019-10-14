package se.l4.commons.types.matching;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import se.l4.commons.types.Types;

public class TypeMatchingMapTest
{
	@Test
	public void testPutAndGet()
	{
		TypeMatchingMap<String> map = new TypeMatchingHashMap<>();
		map.put(Types.reference(Object.class), "object");
		map.put(Types.reference(String.class), "string");

		assertThat(map.get(Types.reference(String.class)).get(), is("string"));
		assertThat(map.get(Types.reference(Object.class)).get(), is("object"));
		assertThat(map.get(Types.reference(Boolean.class)).isPresent(), is(false));
	}

	@Test
	public void testSimpleHierarchyWithObjectRoot()
	{
		TypeMatchingMap<String> map = new TypeMatchingHashMap<>();
		map.put(Types.reference(Object.class), "object");
		map.put(Types.reference(String.class), "string");

		assertThat(map.getBest(Types.reference(String.class)).get(), is("string"));
		assertThat(map.getBest(Types.reference(Boolean.class)).get(), is("object"));

		// Interfaces do not share a common root
		assertThat(map.getBest(Types.reference(Comparable.class)).isPresent(), is(false));
	}

	@Test
	public void testInterfaceAndConcreteClass()
	{
		TypeMatchingMap<String> map = new TypeMatchingHashMap<>();
		map.put(Types.reference(Comparable.class), "comparable");
		map.put(Types.reference(String.class), "string");

		assertThat(map.getBest(Types.reference(String.class)).get(), is("string"));
		assertThat(map.getBest(Types.reference(Comparable.class)).get(), is("comparable"));
		assertThat(map.getBest(Types.reference(Long.class)).get(), is("comparable"));
	}

	@Test
	public void testInterfaceAndAbstractClass()
	{
		TypeMatchingMap<String> map = new TypeMatchingHashMap<>();
		map.put(Types.reference(ClassMatchingMap.class), "map");
		map.put(Types.reference(AbstractClassMatchingMap.class), "abstract");

		assertThat(map.getBest(Types.reference(ClassMatchingHashMap.class)).get(), is("abstract"));

		// The fake map declares the interface closer to the top
		assertThat(map.getBest(Types.reference(FakeMatchingMap.class)).get(), is("map"));
	}

	@Test
	public void testAllMatching()
	{
		TypeMatchingMap<String> map = new TypeMatchingHashMap<>();
		map.put(Types.reference(ClassMatchingMap.class), "map");
		map.put(Types.reference(AbstractClassMatchingMap.class), "abstract");

		List<MatchedTypeRef<String>> all = map.getAll(Types.reference(ClassMatchingHashMap.class));
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
	}
}