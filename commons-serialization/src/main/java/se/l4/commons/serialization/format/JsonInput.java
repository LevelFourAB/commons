package se.l4.commons.serialization.format;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import se.l4.commons.io.Bytes;

/**
 * Input for JSON. Please note that this class is not intended for general use
 * and does not strictly conform to the JSON standard.
 *
 * @author Andreas Holstenson
 *
 */
public class JsonInput
	extends AbstractStreamingInput
{
	private static final char NULL = 0;

	private final Reader in;

	private final char[] buffer;
	private int position;
	private int limit;

	private final boolean[] lists;
	private final String[] names;

	private ValueType value;
	private String valueString;
	private boolean valueBoolean;
	private long valueLong;
	private double valueDouble;

	public JsonInput(InputStream in)
	{
		this(new InputStreamReader(in, StandardCharsets.UTF_8));
	}

	public JsonInput(Reader in)
	{
		this.in = in;

		lists = new boolean[20];
		names = new String[20];
		buffer = new char[1024];
	}

	@Override
	public void close()
		throws IOException
	{
		in.close();
	}

	@Override
	protected IOException raiseException(String message)
	{
		StringBuilder path = new StringBuilder();
		for(int i=1; i<level; i++)
		{
			if(i > 1) path.append(" > ");

			path.append(names[i]);
		}
		return new IOException(message + (level > 0 ? " (at " + path + ")" : ""));
	}

	@Override
	public Token next0()
		throws IOException
	{
		Token token = peek();
		switch(token)
		{
			case OBJECT_END:
			case LIST_END:
			{
				readNext();

				char c = peekChar();
				if(c == ',') read();

				return token;
			}
			case OBJECT_START:
			case LIST_START:
				readNext();
				lists[level + 1] = token == Token.LIST_START;
				return token;
			case KEY:
			{
				readWhitespace();
				String key = readString(true);
				char next = readNext();
				if(next != ':')
				{
					throw raiseException("Expected `:`, got `" + next + "`");
				}

				names[level] = key;
				setStringValue(key);
				return token;
			}
			case VALUE:
			case NULL:
			{
				readNextValue();

				// Check for trailing commas
				readWhitespace();

				char c = peekChar();
				if(c == ',') read();

				return token;
			}
		}

		return Token.END_OF_STREAM;
	}

	/**
	 * Read all of the whitespace at the current position.
	 *
	 * @throws IOException
	 */
	private void readWhitespace()
		throws IOException
	{
		if(limit - position > 0 && ! Character.isWhitespace(buffer[position])) return;

		while(true)
		{
			if(limit - position < 1)
			{
				if(! readAhead(1)) return;
			}

			char c = buffer[position];
			if(Character.isWhitespace(c) || c == ',')
			{
				position++;
			}
			else
			{
				return;
			}
		}
	}

	/**
	 * Reader the next character while also skipping whitespace as necessary.
	 *
	 * @return
	 * @throws IOException
	 */
	private char readNext()
		throws IOException
	{
		readWhitespace();

		return read();
	}

	/**
	 * Read a single character at the current position.
	 *
	 * @return
	 * @throws IOException
	 */
	private char read()
		throws IOException
	{
		if(limit - position < 1)
		{
			if(! readAhead(1))
			{
				throw new EOFException();
			}
		}

		return buffer[position++];
	}

	/**
	 * Perform a read ahead for the given number of characters. Will read the
	 * characters into the buffer.
	 *
	 * @param minChars
	 * @return
	 * @throws IOException
	 */
	private boolean readAhead(int minChars)
		throws IOException
	{
		if(limit < 0)
		{
			return false;
		}
		else if(position + minChars < limit)
		{
			return true;
		}
		else if(limit >= position)
		{
			// If we have characters left we need to keep them in the buffer
			int stop = limit - position;

			System.arraycopy(buffer, position, buffer, 0, stop);

			limit = stop;
		}
		else
		{
			limit = 0;
		}

		int read = read(buffer, limit, buffer.length - limit);

		position = 0;
		limit += read;

		if(read == 0)
		{
			return false;
		}

		if(read < minChars)
		{
			throw raiseException("Needed " + minChars + " but got " + read);
		}

		return true;
	}

	/**
	 * Fully read a number of characters.
	 *
	 * @param buffer
	 * @param offset
	 * @param length
	 * @return
	 * @throws IOException
	 */
	private int read(char[] buffer, int offset, int length)
		throws IOException
	{
		int result = 0;
		while(result < length)
		{
			int l = in.read(buffer, offset + result, length - result);
			if(l == -1) break;
			result += l;
		}

		return result;
	}

	/**
	 * Take the current character and turn it into a {@link Token}.
	 *
	 * @param c
	 * @return
	 */
	private Token toToken(char c)
	{
		if(c == NULL)
		{
			return Token.END_OF_STREAM;
		}

		switch(c)
		{
			case '{':
				return Token.OBJECT_START;
			case '}':
				return Token.OBJECT_END;
			case '[':
				return Token.LIST_START;
			case ']':
				return Token.LIST_END;
			case '"':
				if(current() != null && current() != Token.KEY && ! lists[level])
				{
					return Token.KEY;
				}
		}

		if(c == 'n')
		{
			// TODO: Better error detection?
			return Token.NULL;
		}

		return Token.VALUE;
	}

	private void readNextValue()
		throws IOException
	{
		char c = readNext();
		if(c == '"')
		{
			// This is a string
			setStringValue(readString(false));
		}
		else
		{
			StringBuilder value = new StringBuilder();
			_outer:
			while(true)
			{
				value.append(c);

				c = peekChar(false);
				switch(c)
				{
					case NULL:
					case '}':
					case ']':
					case ',':
					case ':':
						break _outer;
					default:
						if(Character.isWhitespace(c)) break _outer;
				}

				read();
			}

			readNonString(value.toString());
		}
	}

	private void readNonString(String in)
		throws IOException
	{
		if("null".equals(in))
		{
			setNullValue();
		}
		else if("false".equals(in))
		{
			setBooleanValue(false);
		}
		else if("true".equals(in))
		{
			setBooleanValue(true);
		}
		else
		{
			try
			{
				setLongValue(Long.parseLong(in));
				return;
			}
			catch(NumberFormatException e)
			{
				try
				{
					setDoubleValue(Double.parseDouble(in));
					return;
				}
				catch(NumberFormatException e2)
				{
				}
			}

			throw raiseException("Unknown type of value: " + in);
		}
	}

	private void setStringValue(String s)
	{
		value = ValueType.STRING;
		this.valueString = s;
	}

	private void setBooleanValue(boolean b)
	{
		value = ValueType.BOOLEAN;
		this.valueBoolean = b;
	}

	private void setLongValue(long l)
	{
		value = ValueType.LONG;
		this.valueLong = l;
	}

	private void setDoubleValue(double d)
	{
		value = ValueType.DOUBLE;
		this.valueDouble = d;
	}

	private void setNullValue()
	{
		value = ValueType.NULL;
	}

	private String readString(boolean readStart)
		throws IOException
	{
		StringBuilder key = new StringBuilder();
		char c = read();
		if(readStart)
		{
			if(c != '"') throw raiseException("Expected \", but got " + c);
			c = read();
		}

		while(c != '"')
		{
			if(c == '\\')
			{
				readEscaped(key);
			}
			else
			{
				key.append(c);
			}

			c = read();
		}

		return key.toString();
	}

	private void readEscaped(StringBuilder result)
		throws IOException
	{
		char c = read();
		switch(c)
		{
			case '\'':
				result.append('\'');
				break;
			case '"':
				result.append('"');
				break;
			case '\\':
				result.append('\\');
				break;
			case '/':
				result.append('/');
				break;
			case 'r':
				result.append('\r');
				break;
			case 'n':
				result.append('\n');
				break;
			case 't':
				result.append('\t');
				break;
			case 'b':
				result.append('\b');
				break;
			case 'f':
				result.append('\f');
				break;
			case 'u':
				// Unicode, read 4 chars and treat as hex
				readAhead(4);
				String s = new String(buffer, position, 4);
				result.append((char) Integer.parseInt(s, 16));
				position += 4;
				break;
		}
	}

	private char peekChar()
		throws IOException
	{
		return peekChar(true);
	}

	private char peekChar(boolean ws)
		throws IOException
	{
		if(ws) readWhitespace();

		if(limit - position < 1)
		{
			if(false == readAhead(1))
			{
				return NULL;
			}
		}

		if(limit - position > 0)
		{
			return buffer[position];
		}

		return NULL;
	}

	@Override
	public Token peek()
		throws IOException
	{
		readWhitespace();

		if(limit - position < 1)
		{
			if(false == readAhead(1)) return Token.END_OF_STREAM;
		}

		if(limit - position > 0)
		{
			return toToken(buffer[position]);
		}

		return Token.END_OF_STREAM;
	}

	@Override
	public Object readDynamic()
		throws IOException
	{
		switch(value)
		{
			case STRING:
				return valueString;
			case BOOLEAN:
				return valueBoolean;
			case LONG:
				return valueLong;
			case DOUBLE:
				return valueDouble;
		}

		return null;
	}

	@Override
	public boolean readBoolean()
		throws IOException
	{
		switch(value)
		{
			case BOOLEAN:
				return valueBoolean;
			default:
				throw raiseException("Expected " + ValueType.BOOLEAN + " but found " + value);
		}
	}

	@Override
	public byte readByte()
		throws IOException
	{
		switch(value)
		{
			case LONG:
				if(valueLong < Byte.MIN_VALUE || valueLong > Byte.MAX_VALUE)
				{
					throw raiseException("Expected " + ValueType.BYTE + " but " + valueLong + " is outside valid range");
				}
				return (byte) valueLong;
			default:
				throw raiseException("Expected " + ValueType.BYTE + " but found " + value);
		}
	}

	@Override
	public char readChar()
		throws IOException
	{
		switch(value)
		{
			case LONG:
				if(valueLong < Character.MIN_VALUE || valueLong > Character.MAX_VALUE)
				{
					throw raiseException("Expected " + ValueType.CHAR + " but " + valueLong + " is outside valid range");
				}
				return (char) valueLong;
			case STRING:
				if(valueString.length() != 1)
				{
					throw raiseException("Expected " + ValueType.CHAR + " but STRING value was not a single character");
				}
				return valueString.charAt(0);
			default:
				throw raiseException("Expected " + ValueType.CHAR + " but found " + value);
		}
	}

	@Override
	public short readShort()
		throws IOException
	{
		switch(value)
		{
			case LONG:
				if(valueLong < Short.MIN_VALUE || valueLong > Short.MAX_VALUE)
				{
					throw raiseException("Expected " + ValueType.SHORT + " but " + valueLong + " is outside valid range");
				}
				return (short) valueLong;
			default:
				throw raiseException("Expected " + ValueType.SHORT + " but found " + value);
		}
	}

	@Override
	public int readInt()
		throws IOException
	{
		switch(value)
		{
			case LONG:
				if(valueLong < Integer.MIN_VALUE || valueLong > Integer.MAX_VALUE)
				{
					throw raiseException("Expected " + ValueType.INTEGER + " but " + valueLong + " is outside valid range");
				}
				return (int) valueLong;
			default:
				throw raiseException("Expected " + ValueType.INTEGER + " but found " + value);
		}
	}

	@Override
	public long readLong()
		throws IOException
	{
		switch(value)
		{
			case LONG:
				return valueLong;
			default:
				throw raiseException("Expected " + ValueType.LONG + " but found " + value);
		}
	}

	@Override
	public float readFloat()
		throws IOException
	{
		switch(value)
		{
			case LONG:
				if(valueLong < Float.MIN_VALUE || valueLong > Float.MAX_VALUE)
				{
					throw raiseException("Expected " + ValueType.FLOAT + " but " + valueLong + " is outside valid range");
				}
				return (float) valueLong;
			case DOUBLE:
				if(valueDouble < Float.MIN_VALUE || valueDouble > Float.MAX_VALUE)
				{
					throw raiseException("Expected " + ValueType.FLOAT + " but " + valueDouble + " is outside valid range");
				}
				return (float) valueDouble;
			default:
				throw raiseException("Expected " + ValueType.FLOAT + " but found " + value);
		}
	}

	@Override
	public double readDouble()
		throws IOException
	{
		switch(value)
		{
			case LONG:
				if(valueLong < Double.MIN_VALUE || valueLong > Double.MAX_VALUE)
				{
					throw raiseException("Expected " + ValueType.DOUBLE + " but " + valueLong + " is outside valid range");
				}
				return (float) valueLong;
			case DOUBLE:
				return valueDouble;
			default:
				throw raiseException("Expected " + ValueType.DOUBLE + " but found " + value);
		}
	}

	@Override
	public String readString()
		throws IOException
	{
		switch(value)
		{
			case STRING:
				return valueString;
			default:
				throw raiseException("Expected " + ValueType.STRING + " but found " + value);
		}
	}

	@Override
	public byte[] readByteArray()
		throws  IOException
	{
		switch(value)
		{
			case STRING:
				/*
		 		 * JSON uses Base64 strings, so we need to decode on demand.
				 */
				return Base64.getDecoder().decode(valueString);
			default:
				throw raiseException("Expected " + ValueType.BYTES + " but found " + value);
		}
	}

	@Override
	public InputStream asInputStream()
		throws IOException
	{
		return new ByteArrayInputStream(readByteArray());
	}

	@Override
	public Bytes readBytes()
		throws IOException
	{
		return Bytes.create(readByteArray());
	}
}
