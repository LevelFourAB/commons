package se.l4.commons.config.internal;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static se.l4.commons.serialization.format.Token.KEY;
import static se.l4.commons.serialization.format.Token.LIST_END;
import static se.l4.commons.serialization.format.Token.LIST_START;
import static se.l4.commons.serialization.format.Token.OBJECT_END;
import static se.l4.commons.serialization.format.Token.OBJECT_START;
import static se.l4.commons.serialization.format.Token.VALUE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.collections.api.factory.Maps;
import org.junit.Test;

import se.l4.commons.config.internal.streaming.MapInput;
import se.l4.commons.config.sources.ConfigSource;
import se.l4.commons.config.sources.MapBasedConfigSource;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.Token;

/**
 * Tests for {@link MapInput}.
 */
public class ObjectStreamsTest
{
	protected ConfigSource resolve(Map<String, Object> map)
	{
		return new MapBasedConfigSource(Maps.mutable.ofMap(map));
	}

	protected Map<String, Object> createMap()
	{
		return new LinkedHashMap<String, Object>();
	}

	protected StreamingInput createInput(ConfigSource source)
	{
		return MapInput.resolveInput(source, "");
	}

	@Test
	public void testSingleString()
		throws Exception
	{
		Map<String, Object> data = createMap();
		data.put("key", "value");

		ConfigSource source = resolve(data);

		assertStream(createInput(source), OBJECT_START, KEY, VALUE, OBJECT_END);
		assertStreamValues(createInput(source), "key", "value");
	}

	@Test
	public void testSingleNumber()
		throws Exception
	{
		Map<String, Object> data = createMap();
		data.put("key", 12.0);

		ConfigSource source = resolve(data);

		assertStream(createInput(source), OBJECT_START, KEY, VALUE, OBJECT_END);
		assertStreamValues(createInput(source), "key", 12.0);
	}

	@Test
	public void testSubObject()
		throws Exception
	{
		Map<String, Object> data = createMap();
		data.put("key.sub", "value1");

		ConfigSource source = resolve(data);

		assertStream(createInput(source), OBJECT_START, KEY, OBJECT_START, KEY, VALUE, OBJECT_END, OBJECT_END);
		assertStreamValues(createInput(source), "key", "sub", "value1");
	}

	@Test
	public void testList()
		throws Exception
	{
		Map<String, Object> data = createMap();
		data.put("sub.0", "value1");

		ConfigSource source = resolve(data);

		assertStream(createInput(source), OBJECT_START, KEY, LIST_START, VALUE, LIST_END, OBJECT_END);
		assertStreamValues(createInput(source), "sub", "value1");
	}

	/**
	 * Assert that the stream contains the specified tokens.
	 *
	 * @param in
	 * @param tokens
	 * @throws IOException
	 */
	protected void assertStream(StreamingInput in, Token... tokens)
		throws IOException
	{
		int i = 0;
		List<Token> history = new ArrayList<Token>();
		while(in.peek() != Token.END_OF_STREAM)
		{
			Token t = in.next();
			history.add(t);
			if(i == tokens.length)
			{
				fail("Did not expect more tokens, but got " + t);
			}
			else if(t != tokens[i++])
			{
				fail("Token at " + (i-1) + " was expected to be " + tokens[i-1] + ", but found " + t);
			}
		}

		if(i < tokens.length)
		{
			fail("Did not read all tokens, expected " + tokens.length + " but only read " + i + ".\nTokens were " + history.toString());
		}
	}

	/**
	 * Assert that KEY and VALUE tokens contain the specified values.
	 *
	 * @param in
	 * @param values
	 * @throws IOException
	 */
	protected void assertStreamValues(StreamingInput in, Object... values)
		throws IOException
	{
		int i = 0;
		while(in.peek() != Token.END_OF_STREAM)
		{
			Token t = in.next();
			switch(t)
			{
				case KEY:
				case VALUE:
					if(i == values.length)
					{
						fail("Did not expect more values, but got " + in.readDynamic());
					}

					assertEquals(values[i++], in.readDynamic());
					break;
			}
		}

		if(i < values.length)
		{
			fail("Did not read all values, expected " + values.length + " but only read " + i);
		}
	}
}
