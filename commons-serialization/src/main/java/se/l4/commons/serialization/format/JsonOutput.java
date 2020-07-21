package se.l4.commons.serialization.format;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Streamer that outputs JSON.
 *
 * @author andreas
 *
 */
public class JsonOutput
	implements StreamingOutput
{
	private static final int HEX_MASK = (1 << 4) - 1;

	private static final char[] DIGITS = {
		'0', '1', '2', '3', '4', '5', '6', '7',
		'8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
	};

	private final static char[] BASE64 = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
		'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
		'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
	};

	private static final int LEVELS = 20;

	protected final Writer writer;
	private final boolean beautify;

	private boolean[] lists;
	private boolean[] hasData;

	private int level;
	private boolean nextKey;

	private final char[] encoded;

	/**
	 * Create a JSON streamer that will write to the given output.
	 *
	 * @param out
	 */
	public JsonOutput(OutputStream out)
	{
		this(out, false);
	}

	/**
	 * Create a JSON streamer that will write to the given output, optionally
	 * with beautification of the generated JSON.
	 *
	 * @param out
	 * @param beautify
	 */
	public JsonOutput(OutputStream out, boolean beautify)
	{
		this(new OutputStreamWriter(out, StandardCharsets.UTF_8), beautify);
	}

	/**
	 * Create a JSON streamer that will write to the given output.
	 *
	 * @param out
	 */
	public JsonOutput(Writer writer)
	{
		this(writer, false);
	}

	/**
	 * Create a JSON streamer that will write to the given output, optionally
	 * with beautification of the generated JSON.
	 *
	 * @param out
	 * @param beautify
	 */
	public JsonOutput(Writer writer, boolean beautify)
	{
		this.writer = writer;
		this.beautify = beautify;

		lists = new boolean[LEVELS];
		hasData = new boolean[LEVELS];

		encoded = new char[4];
	}

	@Override
	public void close()
		throws IOException
	{
		flush();
		writer.close();
	}

	/**
	 * Escape and write the given string.
	 *
	 * @param in
	 * @throws IOException
	 */
	private void writeEscaped(String in)
		throws IOException
	{
		for(int i=0, n=in.length(); i<n; i++)
		{
			char c = in.charAt(i);
			if(c == '"' || c == '\\')
			{
				writer.write('\\');
				writer.write(c);
			}
			else if(c == '\r')
			{
				writer.write('\\');
				writer.write('r');
			}
			else if(c == '\n')
			{
				writer.write('\\');
				writer.write('n');
			}
			else if(c == '\t')
			{
				writer.write('\\');
				writer.write('t');
			}
			else if(c == '\b')
			{
				writer.write('\\');
				writer.write('b');
			}
			else if(c == '\f')
			{
				writer.write('\\');
				writer.write('f');
			}
			else if(c <= 0x1F)
			{
				writer.write('\\');
				writer.write('u');

				int v = c;
				int pos = 4;
				do
				{
					encoded[--pos] = DIGITS[v & HEX_MASK];
					v >>>= 4;
				}
				while (v != 0);

				for(int j=0; j<pos; j++) writer.write('0');
				writer.write(encoded, pos, 4 - pos);
			}
			else
			{
				writer.write(c);
			}
		}
	}

	/**
	 * Increase the level by one.
	 *
	 * @param list
	 */
	private void increaseLevel(boolean list)
	{
		level++;

		if(hasData.length == level)
		{
			// Grow lists when needed
			hasData = Arrays.copyOf(hasData, hasData.length * 2);
			lists = Arrays.copyOf(lists, hasData.length * 2);
		}

		hasData[level] = false;
		lists[level] = list;

		nextKey = ! list;
	}

	/**
	 * Decrease the level by one.
	 *
	 * @throws IOException
	 */
	private void decreaseLevel()
		throws IOException
	{
		level--;
		nextKey = ! lists[level];

		if(beautify && hasData[level])
		{
			writer.write('\n');

			for(int i=0; i<level; i++)
			{
				writer.write("\t");
			}
		}
	}

	/**
	 * Helper to check if this write is a key and if so fail it as this output
	 * only supports string keys.
	 */
	private void failKey()
		throws IOException
	{
		if(nextKey)
		{
			throw new IOException("Trying to write a key that is not a string");
		}

		// If we are reading an object make sure there is a key
		nextKey = ! lists[level];
	}

	/**
	 * Start a write, will output commas and beautification if needed.
	 *
	 * @throws IOException
	 */
	private void startWrite()
		throws IOException
	{
		if(! lists[level] && ! nextKey)
		{
			return;
		}

		if(hasData[level]) writer.write(',');

		hasData[level] = true;

		if(beautify && level > 0)
		{
			writer.write('\n');

			for(int i=0; i<level; i++)
			{
				writer.write('\t');
			}
		}
	}

	@Override
	public void writeObjectStart()
		throws IOException
	{
		startWrite();
		failKey();

		writer.write('{');

		increaseLevel(false);
	}

	@Override
	public void writeObjectEnd()
		throws IOException
	{
		if(! nextKey)
		{
			throw new IOException("Trying to end an object without writing a key");
		}

		decreaseLevel();
		writer.write('}');
	}

	@Override
	public void writeListStart()
		throws IOException
	{
		startWrite();
		failKey();

		writer.write('[');

		increaseLevel(true);
	}

	@Override
	public void writeListEnd()
		throws IOException
	{
		failKey();
		decreaseLevel();
		writer.write(']');
	}

	@Override
	public void writeString(String value)
		throws IOException
	{
		if(nextKey)
		{
			if(value == null)
			{
				throw new IOException("Tried writing a null key");
			}

			startWrite();

			writer.write('"');
			writeEscaped(value);
			writer.write('"');
			writer.write(':');

			nextKey = false;
		}
		else
		{
			startWrite();

			if(value == null)
			{
				writer.write("null");
			}
			else
			{
				writer.write('"');
				writeEscaped(value);
				writer.write('"');
			}

			nextKey = ! lists[level];
		}
	}

	private void writeUnescaped(String value)
		throws IOException
	{
		startWrite();
		failKey();

		if(value == null)
		{
			writer.write("null");
		}
		else
		{
			writer.write(value);
		}
	}

	@Override
	public void writeByte(byte b)
		throws IOException
	{
		writeUnescaped(Byte.toString(b));
	}

	@Override
	public void writeChar(char c)
		throws IOException
	{
		failKey();
		writeString(String.valueOf(c));
	}

	@Override
	public void writeShort(short s)
		throws IOException
	{
		writeUnescaped(Short.toString(s));
	}

	@Override
	public void writeInt(int number)
		throws IOException
	{
		writeUnescaped(Integer.toString(number));
	}

	@Override
	public void writeLong(long number)
		throws IOException
	{
		writeUnescaped(Long.toString(number));
	}

	@Override
	public void writeFloat(float number)
		throws IOException
	{
		writeUnescaped(Float.toString(number));
	}

	@Override
	public void writeDouble(double number)
		throws IOException
	{
		writeUnescaped(Double.toString(number));
	}

	@Override
	public void writeBoolean(boolean bool)
		throws IOException
	{
		writeUnescaped(Boolean.toString(bool));
	}

	@Override
	public void writeBytes(byte[] data)
		throws IOException
	{
		startWrite();
		failKey();

		if(data == null)
		{
			writer.write("null");
			return;
		}

		writer.write('"');

		int i = 0;
		for(int n=data.length - 2; i<n; i+=3)
		{
			write(data, i, 3);
		}

		if(i < data.length)
		{
			write(data, i, data.length - i);
		}

		writer.write('"');
	}

	/**
	 * Write some BASE64 encoded bytes.
	 *
	 * @param data
	 * @param pos
	 * @param chars
	 * @param len
	 * @throws IOException
	 */
	private void write(byte[] data, int pos, int len)
		throws IOException
	{
		char[] chars = BASE64;

		int loc = (len > 0 ? (data[pos] << 24) >>> 8 : 0) |
			(len > 1 ? (data[pos+1] << 24) >>> 16 : 0) |
			(len > 2 ? (data[pos+2] << 24) >>> 24 : 0);

		switch(len)
		{
			case 3:
				writer.write(chars[loc >>> 18]);
				writer.write(chars[(loc >>> 12) & 0x3f]);
				writer.write(chars[(loc >>> 6) & 0x3f]);
				writer.write(chars[loc & 0x3f]);
				break;
			case 2:
				writer.write(chars[loc >>> 18]);
				writer.write(chars[(loc >>> 12) & 0x3f]);
				writer.write(chars[(loc >>> 6) & 0x3f]);
				writer.write('=');
				break;
			case 1:
				writer.write(chars[loc >>> 18]);
				writer.write(chars[(loc >>> 12) & 0x3f]);
				writer.write('=');
				writer.write('=');
		}
	}

	@Override
	public void writeNull()
		throws IOException
	{
		writeUnescaped(null);
	}

	@Override
	public void flush() throws IOException
	{
		writer.flush();
	}
}
