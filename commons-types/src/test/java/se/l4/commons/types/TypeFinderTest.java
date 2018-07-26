package se.l4.commons.types;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.junit.Test;

public class TypeFinderTest
{
	@Test
	public void testScanning()
	{
		TypeFinder finder = TypeFinder.builder()
			.addPackage("se.l4.commons.types")
			.build();


		Set<Class<? extends InstanceFactory>> factories = finder.getSubTypesOf(InstanceFactory.class);

		assertThat(factories, notNullValue());
		assertThat(factories.size(), is(1));

		Class<? extends InstanceFactory> c = factories.iterator().next();
		assertThat(c, is((Object) DefaultInstanceFactory.class));
	}
}
