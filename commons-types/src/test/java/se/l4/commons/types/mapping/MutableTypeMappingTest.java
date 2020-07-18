package se.l4.commons.types.mapping;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import se.l4.commons.types.reflect.TypeRef;

public class MutableTypeMappingTest
{
	@Test
	public void testEmpty()
	{
		MutableTypeMapper<TestEncounter, String> mapper = MutableTypeMapper.create(TestEncounter::new)
			.build();

		Mapped<String> mapped = mapper.get(Object.class);
		assertThat(mapped, notNullValue());
		assertThat(mapped.isPresent(), is(false));
	}

	@Test
	public void testSpecific()
	{
		MutableTypeMapper<TestEncounter, String> mapper = MutableTypeMapper.create(TestEncounter::new)
			.build();

		mapper.addSpecific(Map.class, "map");

		Mapped<String> m1 = mapper.get(Object.class);
		assertThat(m1, notNullValue());
		assertThat(m1.isPresent(), is(false));

		Mapped<String> m2 = mapper.get(Map.class);
		assertThat(m2, notNullValue());
		assertThat(m2.isPresent(), is(true));
		assertThat(m2.get(), is("map"));

		Mapped<String> m3 = mapper.get(HashMap.class);
		assertThat(m3, notNullValue());
		assertThat(m3.isPresent(), is(false));
	}

	private static class TestEncounter
		implements ResolutionEncounter<String>
	{
		private final TypeRef type;

		public TestEncounter(TypeRef type)
		{
			this.type = type;
		}

		@Override
		public TypeRef getType()
		{
			return type;
		}
	}

}
