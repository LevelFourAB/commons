package se.l4.commons.serialization.format;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import se.l4.commons.io.Bytes;

/**
 * Output for custom binary format.
 *
 * @author Andreas Holstenson
 *
 */
public class BinaryOutput
	implements StreamingOutput
{
	private static final int LEVELS = 20;

	public static final int TAG_KEY = 0;

	public static final int TAG_OBJECT_START = 1;
	public static final int TAG_OBJECT_END = 2;
	public static final int TAG_LIST_START = 3;
	public static final int TAG_LIST_END = 4;

	public static final int TAG_STRING = 10;
	public static final int TAG_INT = 11;
	public static final int TAG_LONG = 12;
	public static final int TAG_NULL = 13;
	public static final int TAG_FLOAT = 14;
	public static final int TAG_DOUBLE = 15;
	public static final int TAG_BOOLEAN = 16;
	public static final int TAG_BYTE_ARRAY = 17;
	public static final int TAG_POSITIVE_INT = 18;
	public static final int TAG_POSITIVE_LONG = 19;
	public static final int TAG_NEGATIVE_INT = 20;
	public static final int TAG_NEGATIVE_LONG = 21;

	private final OutputStream out;

	private boolean[] lists;
	private boolean nextKey;

	private int level;

	public BinaryOutput(OutputStream out)
	{
		this.out = out;

		lists = new boolean[LEVELS];
	}

	@Override
	public void close()
		throws IOException
	{
		flush();
		out.close();
	}

	/**
	 * Increase the level by one.
	 *
	 * @param list
	 */
	private void increaseLevel(boolean list)
	{
		level++;
		if(lists.length == level)
		{
			// Grow lists when needed
			lists = Arrays.copyOf(lists, lists.length * 2);
		}

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

	@Override
	public void writeObjectStart()
		throws IOException
	{
		failKey();

		out.write(TAG_OBJECT_START);

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

		out.write(TAG_OBJECT_END);
	}

	@Override
	public void writeListStart()
		throws IOException
	{
		failKey();

		out.write(TAG_LIST_START);

		increaseLevel(true);
	}

	@Override
	public void writeListEnd()
		throws IOException
	{
		failKey();

		decreaseLevel();
		out.write(TAG_LIST_END);
	}

	@Override
	public void writeString(String value)
		throws IOException
	{
		if(nextKey)
		{
			out.write(TAG_KEY);

			nextKey = false;
		}
		else
		{
			out.write(TAG_STRING);

			nextKey = ! lists[level];
		}

		writeIntegerNoTag(value.length());
		for(int i=0, n=value.length(); i<n; i++)
		{
			char c = value.charAt(i);
			if(c <= 0x007f)
			{
				out.write((byte) c);
			}
			else if(c > 0x07ff)
			{
				out.write((byte) (0xe0 | c >> 12 & 0x0f));
				out.write((byte) (0x80 | c >> 6 & 0x3f));
				out.write((byte) (0x80 | c >> 0 & 0x3f));
			}
			else
			{
				out.write((byte) (0xc0 | c >> 6 & 0x1f));
				out.write((byte) (0x80 | c >> 0 & 0x3f));
			}
		}
	}

	@Override
	public void writeByte(byte b)
		throws IOException
	{
		writeInt((int) b);
	}

	@Override
	public void writeChar(char c)
		throws IOException
	{
		writeInt((int) c);
	}

	@Override
	public void writeShort(short s)
		throws IOException
	{
		writeInt((int) s);
	}

	/**
	 * Write an integer to the output stream.
	 *
	 * @param value
	 * @throws IOException
	 */
	@Override
	public void writeInt(int value)
		throws IOException
	{
		failKey();

		if(value < 0)
		{
			out.write(TAG_NEGATIVE_INT);
			writeIntegerNoTag(-value);
		}
		else
		{
			out.write(TAG_POSITIVE_INT);
			writeIntegerNoTag(value);
		}
	}

	/**
	 * Write an integer to the output stream without tagging it.
	 *
	 * @param value
	 * @throws IOException
	 */
	private void writeIntegerNoTag(int value)
		throws IOException
	{
		while(true)
		{
			if((value & ~0x7F) == 0)
			{
				out.write(value);
				break;
			}
			else
			{
				out.write((value & 0x7f) | 0x80);
				value >>>= 7;
			}
		}
	}

	/**
	 * Write a long to the output stream.
	 *
	 * @param value
	 * @throws IOException
	 */
	@Override
	public void writeLong(long value)
		throws IOException
	{
		failKey();

		if(value < 0)
		{
			out.write(TAG_NEGATIVE_LONG);
			writeLongNoTag(- value);
		}
		else
		{
			out.write(TAG_POSITIVE_LONG);
			writeLongNoTag(value);
		}
	}


	/**
	 * Write a long to the output stream.
	 *
	 * @param value
	 * @throws IOException
	 */
	private void writeLongNoTag(long value)
		throws IOException
	{
		while(true)
		{
			if((value & ~0x7FL) == 0)
			{
				out.write((int) value);
				break;
			}
			else
			{
				out.write(((int) value & 0x7f) | 0x80);
				value >>>= 7;
			}
		}
	}

	@Override
	public void writeNull()
		throws IOException
	{
		failKey();

		out.write(TAG_NULL);
	}

	@Override
	public void writeFloat(float value)
		throws IOException
	{
		failKey();

		out.write(TAG_FLOAT);

		int i = Float.floatToRawIntBits(value);
		out.write(i & 0xff);
		out.write((i >> 8) & 0xff);
		out.write((i >> 16) & 0xff);
		out.write((i >> 24) & 0xff);
	}

	@Override
	public void writeDouble(double value)
		throws IOException
	{
		failKey();

		out.write(TAG_DOUBLE);

		long l = Double.doubleToRawLongBits(value);
		out.write((int) l & 0xff);
		out.write((int) (l >> 8) & 0xff);
		out.write((int) (l >> 16) & 0xff);
		out.write((int) (l >> 24) & 0xff);
		out.write((int) (l >> 32) & 0xff);
		out.write((int) (l >> 40) & 0xff);
		out.write((int) (l >> 48) & 0xff);
		out.write((int) (l >> 56) & 0xff);
	}

	@Override
	public void writeBoolean(boolean b)
		throws IOException
	{
		failKey();

		out.write(TAG_BOOLEAN);

		out.write(b ? 1 : 0);
	}

	@Override
	public void writeBytes(byte[] data)
		throws IOException
	{
		failKey();

		out.write(TAG_BYTE_ARRAY);

		writeIntegerNoTag(data.length);

		out.write(data);
	}

	@Override
	public OutputStream writeBytes()
		throws IOException
	{
		return new ByteArrayOutputStream()
		{
			@Override
			public void close()
				throws IOException
			{
				writeBytes(toByteArray());
			}
		};
	}

	@Override
	public void writeBytes(Bytes data)
		throws IOException
	{
		writeBytes(data.toByteArray());
	}

	@Override
	public void flush()
		throws IOException
	{
		out.flush();
	}
}
