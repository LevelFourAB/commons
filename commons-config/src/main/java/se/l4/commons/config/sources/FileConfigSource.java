package se.l4.commons.config.sources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;

import se.l4.commons.config.ConfigException;
import se.l4.commons.config.internal.streaming.ConfigJsonInput;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.Token;

/**
 * Source that provides access to config values read from a file.
 *
 * <p>
 * The format is similar to JSON but is not as strict. For example it does not
 * require quotes around keys or string values and the initial braces can be
 * skipped.
 *
 * <p>
 * Example:
 * <pre>
 * thumbs: {
 *	medium: { width: 400, height: 400 }
 *	small: {
 *		width: 100
 *		height: 100
 *	}
 * }
 *
 * # Override the width
 * thumbs.small.width: 150
 * </pre>
 */
public class FileConfigSource
	extends MapBasedConfigSource
{
	private FileConfigSource(ImmutableMap<String, Object> properties)
	{
		super(properties);
	}

	public static FileConfigSource read(Path path)
		throws IOException
	{
		try(InputStream in = Files.newInputStream(path))
		{
			return read(in);
		}
	}

	public static FileConfigSource read(File path)
		throws IOException
	{
		try(InputStream in = new FileInputStream(path))
		{
			return read(in);
		}
	}

	public static FileConfigSource read(InputStream in)
		throws IOException
	{
		try(Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8))
		{
			return read(reader);
		}
	}

	public static FileConfigSource read(Reader reader)
		throws IOException
	{
		try(ConfigJsonInput in = new ConfigJsonInput(reader))
		{
			return read(in);
		}
	}

	public static FileConfigSource readString(String source)
		throws IOException
	{
		try(ConfigJsonInput in = new ConfigJsonInput(new StringReader(source)))
		{
			return read(in);
		}
	}

	private static FileConfigSource read(ConfigJsonInput in)
		throws IOException
	{
		MutableMap<String, Object> properties = Maps.mutable.empty();

		readMap(properties, in, "");

		return new FileConfigSource(properties.toImmutable());
	}

	/**
	 * Read a single map from the input, optionally while reading object
	 * start and end tokens.
	 *
	 * @param input
	 * @return
	 * @throws IOException
	 */
	private static void readMap(MutableMap<String, Object> properties, StreamingInput input, String prefix)
		throws IOException
	{
		boolean readEnd = false;
		if(input.peek() == Token.OBJECT_START)
		{
			// Check if the object is wrapped
			readEnd = true;
			input.next();
		}

		Token t;
		while((t = input.peek()) != Token.OBJECT_END && t != Token.END_OF_STREAM)
		{
			// Read the key
			input.next(Token.KEY);
			String key = input.readString();

			// Read the value
			readDynamic(properties, input, prefix.isEmpty() ? key : (prefix + ConfigKeys.PATH_DELIMITER + key));
		}

		if(readEnd)
		{
			input.next(Token.OBJECT_END);
		}
	}

	/**
	 * Read a list from the input.
	 *
	 * @param input
	 * @return
	 * @throws IOException
	 */
	private static void readList(MutableMap<String, Object> properties, StreamingInput input, String prefix)
		throws IOException
	{
		input.next(Token.LIST_START);

		int i = 0;
		while(input.peek() != Token.LIST_END)
		{
			// Read the value
			readDynamic(properties, input, prefix + ConfigKeys.PATH_DELIMITER + i);
			i++;
		}

		input.next(Token.LIST_END);
	}

	/**
	 * Depending on the next token read either a value, a list or a map.
	 *
	 * @param input
	 * @return
	 * @throws IOException
	 */
	private static void readDynamic(MutableMap<String, Object> properties, StreamingInput input, String prefix)
		throws IOException
	{
		switch(input.peek())
		{
			case VALUE:
				input.next();
				properties.put(prefix, input.readDynamic());
				return;
			case NULL:
				input.next();
				properties.put(prefix, input.readDynamic());
				return;
			case LIST_START:
				readList(properties, input, prefix);
				return;
			case OBJECT_START:
				readMap(properties, input, prefix);
				return;
			default:
				throw new ConfigException("Unable to read file, unknown start of value: " + input.peek());
		}
	}
}
