package se.l4.commons.config.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import se.l4.commons.config.internal.streaming.ConfigJsonInput;
import se.l4.commons.serialization.format.StreamingInput;

/**
 * Test of {@link RawFormatReader}.
 *
 * @author Andreas Holstenson
 *
 */
public class ConfigReaderTest
{
	@Test
	public void testWithBraces()
		throws IOException
	{
		StreamingInput in = createInput("{ key: value }");
		Map<String, Object> data = RawFormatReader.read(in);

		Map<String, Object> expected = new HashMap<String, Object>();
		expected.put("key", "value");

		assertThat(data, is(expected));
	}

	@Test
	public void testNoBraces()
		throws IOException
	{
		StreamingInput in = createInput("key: value");
		Map<String, Object> data = RawFormatReader.read(in);

		Map<String, Object> expected = new HashMap<String, Object>();
		expected.put("key", "value");

		assertThat(data, is(expected));
	}

	@Test
	public void testNoBracesList()
		throws IOException
	{
		StreamingInput in = createInput("key: [ value ]");
		Map<String, Object> data = RawFormatReader.read(in);

		List<Object> list = new ArrayList<Object>();
		list.add("value");

		Map<String, Object> expected = new HashMap<String, Object>();
		expected.put("key", list);

		assertThat(data, is(expected));
	}

	protected StreamingInput createInput(String in)
	{
		return new ConfigJsonInput(new StringReader(in));
	}

}
