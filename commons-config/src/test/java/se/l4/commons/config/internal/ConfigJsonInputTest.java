package se.l4.commons.config.internal;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static se.l4.commons.serialization.format.Token.KEY;
import static se.l4.commons.serialization.format.Token.NULL;
import static se.l4.commons.serialization.format.Token.OBJECT_END;
import static se.l4.commons.serialization.format.Token.OBJECT_START;
import static se.l4.commons.serialization.format.Token.VALUE;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import se.l4.commons.config.internal.streaming.ConfigJsonInput;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.Token;

/**
 * Test for {@link ConfigJsonInput}.
 *
 * @author Andreas Holstenson
 *
 */
public class ConfigJsonInputTest
{
	/**
	 * Test reading without any object braces. Optional.
	 *
	 * @throws Exception
	 */
	@Test
	public void testObjectValuesWithoutBraces()
		throws Exception
	{
		String v = "\"key1\": \"value1\", \"key2\": \"value2\"";
		StreamingInput input = createInput(v);
		assertStream(input, KEY, VALUE, KEY, VALUE);

		input = createInput(v);
		assertStreamValues(input, "\"key1\"", "value1", "\"key2\"", "value2");
	}

	@Test
	public void testObjectValuesWithNull()
		throws Exception
	{
		String v = "\"key1\": null, \"key2\": normal";
		StreamingInput input = createInput(v);
		assertStream(input, KEY, NULL, KEY, VALUE);

		input = createInput(v);
		assertStreamValues(input, "\"key1\"", null, "\"key2\"", "normal");
	}

	/**
	 * Test reading without any object braces or commas. Optional.
	 *
	 * @throws Exception
	 */
	@Test
	public void testObjectValuesWithoutBracesAndCommas()
		throws Exception
	{
		String v = "\"key1\": \"value1\" \"key2\": \"value2\"";
		StreamingInput input = createInput(v);
		assertStream(input, KEY, VALUE, KEY, VALUE);

		input = createInput(v);
		assertStreamValues(input, "\"key1\"", "value1", "\"key2\"", "value2");
	}

	/**
	 * Test reading without any object braces or commas. Instead values are
	 * separated by a new line.
	 *
	 * @throws Exception
	 */
	@Test
	public void testKeyValueWithLinebreak()
		throws Exception
	{
		String v = "\"key1\": 22.0\n\"key2\": \"value2\"";
		StreamingInput input = createInput(v);
		assertStream(input, KEY, VALUE, KEY, VALUE);

		input = createInput(v);
		assertStreamValues(input, "\"key1\"", 22.0, "\"key2\"", "value2");
	}

	/**
	 * Test reading a string value that does not use quotes.
	 *
	 * @throws Exception
	 */
	@Test
	public void testValueNoQuotes()
		throws Exception
	{
		String v = "key: value with spaces";
		StreamingInput input = createInput(v);
		assertStream(input, KEY, VALUE);

		input = createInput(v);
		assertStreamValues(input, "key", "value with spaces");
	}

	/**
	 * Test reading where keys and values do not have any quotes.
	 *
	 * @throws Exception
	 */
	@Test
	public void testMultipleValuesNoQuotes()
		throws Exception
	{
		String v = "key: value with spaces\nkey2: another value 21.0";
		StreamingInput input = createInput(v);
		assertStream(input, KEY, VALUE, KEY, VALUE);

		input = createInput(v);
		assertStreamValues(input, "key", "value with spaces", "key2", "another value 21.0");
	}

	/**
	 * Test reading a key and value separated with equals.
	 *
	 * @throws Exception
	 */
	@Test
	public void testKeyWithEquals()
		throws Exception
	{
		String v = "key = value with spaces";
		StreamingInput input = createInput(v);
		assertStream(input, KEY, VALUE);

		input = createInput(v);
		assertStreamValues(input, "key", "value with spaces");
	}

	/**
	 * Test reading with a comment.
	 *
	 * @throws Exception
	 */
	@Test
	public void testMultipleValuesWithComment()
		throws Exception
	{
		String v = "key: value with spaces\n# This is a comment\nkey2: another value 21.0";
		StreamingInput input = createInput(v);
		assertStream(input, KEY, VALUE, KEY, VALUE);

		input = createInput(v);
		assertStreamValues(input, "key", "value with spaces", "key2", "another value 21.0");
	}

	/**
	 * Test reading a string value that does not use quotes.
	 *
	 * @throws Exception
	 */
	@Test
	public void testOnlyComment()
		throws Exception
	{
		String v = "# comment";
		StreamingInput input = createInput(v);
		assertStream(input);
	}

	/**
	 * Test key without equal or colon.
	 *
	 * @throws Exception
	 */
	@Test
	public void testKeyThenObject()
		throws Exception
	{
		String v = "key { key2: value }";
		StreamingInput input = createInput(v);
		assertStream(input, KEY, OBJECT_START, KEY, VALUE, OBJECT_END);

		input = createInput(v);
		assertStreamValues(input, "key", "key2", "value");
	}

	/**
	 * Test key without equal or colon.
	 *
	 * @throws Exception
	 */
	@Test
	public void testKeyThenObjectWithNewline()
		throws Exception
	{
		String v = "key\n{ key2: value }";
		StreamingInput input = createInput(v);
		assertStream(input, KEY, OBJECT_START, KEY, VALUE, OBJECT_END);

		input = createInput(v);
		assertStreamValues(input, "key", "key2", "value");
	}

	protected ConfigJsonInput createInput(String in)
	{
		return new ConfigJsonInput(new StringReader(in));
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
		while(in.peek() != Token.END_OF_STREAM)
		{
			Token t = in.next();
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
			fail("Did not read all tokens, expected " + tokens.length + " but only read " + i);
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
				case NULL:
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
