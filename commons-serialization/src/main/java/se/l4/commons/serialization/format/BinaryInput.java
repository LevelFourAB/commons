package se.l4.commons.serialization.format;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.ByteStreams;

import se.l4.commons.io.Bytes;

/**
 * Input for binary format.
 *
 * @author Andreas Holstenson
 *
 */
public class BinaryInput
	extends AbstractStreamingInput
{
	private static final int CHARS_SIZE = 1024;
	private static final ThreadLocal<char[]> CHARS = new ThreadLocal<char[]>()
	{
		@Override
		protected char[] initialValue()
		{
			return new char[1024];
		}
	};

	private final InputStream in;

	private final byte[] buffer;

	private int peekedByte;
	private int currentValueByte;
	private boolean didReadValue;

	public BinaryInput(InputStream in)
	{
		this.in = in;
		buffer = new byte[8];

		peekedByte = -2;
	}

	@Override
	public void close()
		throws IOException
	{
		in.close();
	}

	@Override
	public Token peek()
		throws IOException
	{
		if((current() == Token.VALUE || current() == Token.KEY) && ! didReadValue)
		{
			// The value hasn't actually been read
			readDynamic();
		}

		if(peekedByte == -2)
		{
			peekedByte = in.read();
		}

		switch(peekedByte)
		{
			case -1:
				return Token.END_OF_STREAM;
			case BinaryOutput.TAG_KEY:
				return Token.KEY;
			case BinaryOutput.TAG_OBJECT_START:
				return Token.OBJECT_START;
			case BinaryOutput.TAG_OBJECT_END:
				return Token.OBJECT_END;
			case BinaryOutput.TAG_LIST_START:
				return Token.LIST_START;
			case BinaryOutput.TAG_LIST_END:
				return Token.LIST_END;
			case BinaryOutput.TAG_NULL:
				return Token.NULL;
			default:
				return Token.VALUE;
		}
	}

	@Override
	protected Token next0()
		throws IOException
	{
		Token current = peek();
		if(current == Token.KEY || current == Token.VALUE)
		{
			// Read actual data of keys and values
			currentValueByte = peekedByte;
			didReadValue = false;
		}
		else
		{
			if(current == Token.NULL)
			{
				currentValueByte = peekedByte;
			}

			peekedByte = in.read();
		}

		return current;
	}

	private void readBuffer(int len)
		throws IOException
	{
		int n = 0;
		while(n < len)
		{
			int count = in.read(buffer, n, len - n);
			if(count < 0)
			{
				throw new EOFException("Expected to read " + len + " bytes, but could only read " + n);
			}
			n += count;
		}
	}

	private double readRawDouble()
		throws IOException
	{
		readBuffer(8);
		long value = ((long) buffer[0] & 0xff) |
			((long) buffer[1] & 0xff) << 8 |
			((long) buffer[2] & 0xff) << 16 |
			((long) buffer[3] & 0xff) << 24 |
			((long) buffer[4] & 0xff) << 32 |
			((long) buffer[5] & 0xff) << 40 |
			((long) buffer[6] & 0xff) << 48 |
			((long) buffer[7] & 0xff) << 56;

		return Double.longBitsToDouble(value);
	}

	private float readRawFloat()
		throws IOException
	{
		readBuffer(4);
		int value = (buffer[0] & 0xff) |
			(buffer[1] & 0xff) << 8 |
			(buffer[2] & 0xff) << 16 |
			(buffer[3] & 0xff) << 24;

		return Float.intBitsToFloat(value);
	}

	private int readRawInteger()
		throws IOException
	{
		int shift = 0;
		int result = 0;
		while(shift < 32)
		{
			final byte b = (byte) in.read();
			result |= (b & 0x7F) << shift;
			if((b & 0x80) == 0) return result;

			shift += 7;
		}

		throw new EOFException("Invalid integer");
	}

	private long readRawLong()
		throws IOException
	{
		int shift = 0;
		long result = 0;
		while(shift < 64)
		{
			final byte b = (byte) in.read();
			result |= (long) (b & 0x7F) << shift;
			if((b & 0x80) == 0) return result;

			shift += 7;
		}

		throw new EOFException("Invalid long");
	}

	private String readRawString()
		throws IOException
	{
		int length = readRawInteger();
		char[] chars = length < CHARS_SIZE ? CHARS.get() : new char[length];

		for(int i=0; i<length; i++)
		{
			int c = in.read() & 0xff;
			int t = c >> 4;
			if(t > -1 && t < 8)
			{
				chars[i] = (char) c;
			}
			else if(t == 12 || t == 13)
			{
				chars[i] = (char) ((c & 0x1f) << 6 | in.read() & 0x3f);
			}
			else if(t == 14)
			{
				chars[i] = (char) ((c & 0x0f) << 12
					| (in.read() & 0x3f) << 6
					| (in.read() & 0x3f) << 0);
			}
		}

		return new String(chars, 0, length);
	}

	private byte[] readRawByteArray()
		throws IOException
	{
		int length = readRawInteger();
		byte[] buffer = new byte[length];
		ByteStreams.readFully(in, buffer);

		return buffer;
	}

	public Object readDynamic()
		throws IOException
	{
		switch(currentValueByte)
		{
			case BinaryOutput.TAG_BOOLEAN:
				return readBoolean();
			case BinaryOutput.TAG_DOUBLE:
				return readDouble();
			case BinaryOutput.TAG_FLOAT:
				return readFloat();
			case BinaryOutput.TAG_INT:
			case BinaryOutput.TAG_POSITIVE_INT:
			case BinaryOutput.TAG_NEGATIVE_INT:
				return readInt();
			case BinaryOutput.TAG_LONG:
			case BinaryOutput.TAG_POSITIVE_LONG:
			case BinaryOutput.TAG_NEGATIVE_LONG:
				return readLong();
			case BinaryOutput.TAG_KEY:
			case BinaryOutput.TAG_STRING:
				return readString();
			case BinaryOutput.TAG_BYTE_ARRAY:
				return readByteArray();
			case BinaryOutput.TAG_NULL:
				return null;
			default:
				throw new IOException("Unexpected value type, no idea what to do (type was " + currentValueByte + ")");
		}
	}

	@Override
	public boolean readBoolean()
		throws IOException
	{
		switch(currentValueByte)
		{
			case BinaryOutput.TAG_BOOLEAN:
				int b = in.read();
				markValueRead();
				return b == 1;
			default:
				throw raiseException("Expected " + ValueType.BOOLEAN + ", but found " + valueType(currentValueByte));
		}
	}

	@Override
	public byte readByte()
		throws IOException
	{
		int value = readInt();
		if(value < Byte.MIN_VALUE || value > Byte.MAX_VALUE)
		{
			throw raiseException("Expected " + ValueType.BYTE + " but " + value + " is outside valid range");
		}
		return (byte) value;
	}

	@Override
	public short readShort()
		throws IOException
	{
		int value = readInt();
		if(value < Short.MIN_VALUE || value > Short.MAX_VALUE)
		{
			throw raiseException("Expected " + ValueType.SHORT + " but " + value + " is outside valid range");
		}
		return (short) value;
	}

	@Override
	public char readChar()
		throws IOException
	{
		int value = readInt();
		if(value < Character.MIN_VALUE || value > Character.MAX_VALUE)
		{
			throw raiseException("Expected " + ValueType.CHAR + " but " + value + " is outside valid range");
		}
		return (char) value;
	}

	@Override
	public int readInt()
		throws IOException
	{
		switch(currentValueByte)
		{
			case BinaryOutput.TAG_INT:
			{
				int i = readRawInteger();
				i = (i >>> 1) ^ -(i & 1);
				markValueRead();
				return i;
			}
			case BinaryOutput.TAG_POSITIVE_INT:
			{
				int i = readRawInteger();
				markValueRead();
				return i;
			}
			case BinaryOutput.TAG_NEGATIVE_INT:
			{
				int i = - readRawInteger();
				markValueRead();
				return i;
			}

			case BinaryOutput.TAG_LONG:
			case BinaryOutput.TAG_POSITIVE_LONG:
			case BinaryOutput.TAG_NEGATIVE_LONG:
				long v = readLong();
				if(v < Integer.MIN_VALUE || v > Integer.MAX_VALUE)
				{
					throw raiseException("Expected " + ValueType.INTEGER + " but " + v + " is outside valid range");
				}
				return (int) v;

			default:
				throw raiseException("Expected " + ValueType.INTEGER + ", but found " + valueType(currentValueByte));
		}
	}

	@Override
	public long readLong()
		throws IOException
	{
		switch(currentValueByte)
		{
			case BinaryOutput.TAG_LONG:
			{
				long l = readRawLong();
				l = (l >>> 1) ^ -(l & 1);
				markValueRead();
				return l;
			}
			case BinaryOutput.TAG_POSITIVE_LONG:
			{
				long l = readRawLong();
				markValueRead();
				return l;
			}
			case BinaryOutput.TAG_NEGATIVE_LONG:
			{
				long l = - readRawLong();
				markValueRead();
				return l;
			}

			case BinaryOutput.TAG_INT:
			case BinaryOutput.TAG_POSITIVE_INT:
			case BinaryOutput.TAG_NEGATIVE_INT:
				return readInt();

			default:
				throw raiseException("Expected " + ValueType.LONG + ", but found " + valueType(currentValueByte));
		}
	}

	@Override
	public float readFloat()
		throws IOException
	{
		switch(currentValueByte)
		{
			case BinaryOutput.TAG_FLOAT:
				float f = readRawFloat();
				markValueRead();
				return f;

			default:
				throw raiseException("Expected " + ValueType.FLOAT + ", but found " + valueType(currentValueByte));
		}
	}

	@Override
	public double readDouble()
		throws IOException
	{
		switch(currentValueByte)
		{
			case BinaryOutput.TAG_DOUBLE:
				double d = readRawDouble();
				markValueRead();
				return d;

			default:
				throw raiseException("Expected " + ValueType.FLOAT + ", but found " + valueType(currentValueByte));
		}
	}

	@Override
	public String readString()
		throws IOException
	{
		switch(currentValueByte)
		{
			case BinaryOutput.TAG_STRING:
			case BinaryOutput.TAG_KEY:
				String s = readRawString();
				markValueRead();
				return s;
			default:
				throw raiseException("Expected " + ValueType.STRING + ", but found " + valueType(currentValueByte));
		}
	}

	@Override
	public byte[] readByteArray()
		throws IOException
	{
		switch(currentValueByte)
		{
			case BinaryOutput.TAG_BYTE_ARRAY:
				byte[] b = readRawByteArray();
				markValueRead();
				return b;
			default:
				throw raiseException("Expected " + ValueType.BYTES + ", but found " + valueType(currentValueByte));
		}
	}

	@Override
	public Bytes readBytes()
		throws IOException
	{
		return Bytes.create(readByteArray());
	}

	@Override
	public InputStream asInputStream()
		throws IOException
	{
		return new ByteArrayInputStream(readByteArray());
	}

	private void markValueRead()
		throws IOException
	{
		didReadValue = true;
		peekedByte = in.read();
	}

	private ValueType valueType(int b)
		throws IOException
	{
		switch(b)
		{
			case BinaryOutput.TAG_BOOLEAN:
				return ValueType.BOOLEAN;
			case BinaryOutput.TAG_DOUBLE:
				return ValueType.DOUBLE;
			case BinaryOutput.TAG_FLOAT:
				return ValueType.FLOAT;
			case BinaryOutput.TAG_INT:
			case BinaryOutput.TAG_POSITIVE_INT:
			case BinaryOutput.TAG_NEGATIVE_INT:
				return ValueType.INTEGER;
			case BinaryOutput.TAG_LONG:
			case BinaryOutput.TAG_POSITIVE_LONG:
			case BinaryOutput.TAG_NEGATIVE_LONG:
				return ValueType.LONG;
			case BinaryOutput.TAG_NULL:
				return ValueType.NULL;
			case BinaryOutput.TAG_STRING:
				return ValueType.STRING;
			case BinaryOutput.TAG_BYTE_ARRAY:
				return ValueType.BYTES;
			default:
				throw raiseException("Unexpected value type, no idea what to do (read byte was " + b + ")");
		}
	}
}
