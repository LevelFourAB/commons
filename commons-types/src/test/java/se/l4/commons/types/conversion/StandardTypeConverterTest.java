package se.l4.commons.types.conversion;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class StandardTypeConverterTest
{

	@Test
	public void testLongToString()
	{
		TypeConverter tc = new StandardTypeConverter();
		String out = tc.convert(10l, String.class);
		assertThat(out, is("10"));
	}
}
