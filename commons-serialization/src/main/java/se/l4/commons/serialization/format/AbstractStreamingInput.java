package se.l4.commons.serialization.format;

import java.io.IOException;

import se.l4.commons.serialization.SerializationException;

/**
 * Abstract implementation of {@link StreamingInput} to simplify common
 * operations such as peeking and value setting.
 */
public abstract class AbstractStreamingInput
	implements StreamingInput
{
	private ValueType valueType;
	private Object value;

	private boolean valueBoolean;
	private byte valueByte;
	private char valueChar;
	private double valueDouble;
	private float valueFloat;
	private int valueInt;
	private long valueLong;
	private short valueShort;

	private Token token;

	protected int level;

	public AbstractStreamingInput()
	{
	}

	@Override
	public Token next()
		throws IOException
	{
		Token token = next0();
		switch(token)
		{
			case OBJECT_END:
			case LIST_END:
				level--;
				break;
			case OBJECT_START:
			case LIST_START:
				level++;
				break;
			default: // Do nothing
				break;
		}

		return this.token = token;
	}

	protected abstract Token next0()
		throws IOException;

	protected IOException raiseException(String message)
	{
		return new IOException(message);
	}

	protected SerializationException raiseSerializationException(String message)
	{
		return new SerializationException(message);
	}

	@Override
	public Token next(Token expected)
		throws IOException
	{
		Token t = next();
		if(t != expected)
		{
			throw raiseException("Expected "+ expected + " but got " + t);
		}
		return t;
	}

	@Override
	public void skipValue()
		throws IOException
	{
		if(token != Token.KEY)
		{
			throw raiseException("Value skipping can only be used with when token is " + Token.KEY);
		}

		switch(peek())
		{
			case LIST_START:
			case LIST_END:
			case OBJECT_START:
			case OBJECT_END:
				next();
				skip();
				break;
			default:
				next();
				break;
		}
	}

	@Override
	public void skip()
		throws IOException
	{
		Token start = token;
		Token stop;
		switch(token)
		{
			case LIST_START:
				stop = Token.LIST_END;
				break;
			case OBJECT_START:
				stop = Token.OBJECT_END;
				break;
			case VALUE:
				return;
			default:
				throw raiseException("Can only skip when start of object, start of list or value, token is now " + token);
		}

		int currentLevel = level;
		Token next = peek();
		while(true)
		{
			// Loop until no more tokens or if we stopped and the level has been reset
			if(next == null)
			{
				throw raiseException("No more tokens, but end of skipped value not found. Started at " + start + " and tried to find " + stop);
			}
			else if(next == stop && level == currentLevel)
			{
				// Consume this last token
				next();
				break;
			}

			// Read peeked value and peek for next one
			next();
			next = peek();
		}
	}

	@Override
	public Token current()
	{
		return token;
	}

	@Override
	public ValueType getValueType()
	{
		if(token != Token.VALUE && token != Token.NULL)
		{
			throw raiseSerializationException("Can not get value type for non-values");
		}

		return valueType;
	}

	protected void setValueNull()
	{
		valueType = ValueType.NULL;
		value = null;
	}

	protected void setValue(Object value)
	{
		if(value instanceof String)
		{
			setValue((String) value);
		}
		else if(value instanceof byte[])
		{
			setValue((byte[]) value);
		}
		else if(value instanceof Boolean)
		{
			setValue((boolean) value);
		}
		else if(value instanceof Double)
		{
			setValue((double) value);
		}
		else if(value instanceof Float)
		{
			setValue((float) value);
		}
		else if(value instanceof Integer)
		{
			setValue((int) value);
		}
		else if(value instanceof Long)
		{
			setValue((long) value);
		}
		else if(value instanceof Short)
		{
			setValue((short) value);
		}
		else
		{
			throw raiseSerializationException("Unsupported value type received from input, value was: " + value);
		}
	}

	protected void setValue(String value)
	{
		valueType = ValueType.STRING;
		this.value = value;
	}

	protected void setValue(boolean value)
	{
		valueType = ValueType.BOOLEAN;
		this.valueBoolean = value;
	}

	protected void setValue(byte value)
	{
		valueType = ValueType.BYTE;
		this.valueByte = value;
	}

	protected void setValue(char value)
	{
		valueType = ValueType.CHAR;
		this.valueChar = value;
	}

	protected void setValue(double value)
	{
		valueType = ValueType.DOUBLE;
		this.valueDouble = value;
	}

	protected void setValue(float value)
	{
		valueType = ValueType.FLOAT;
		this.valueFloat = value;
	}

	protected void setValue(int value)
	{
		valueType = ValueType.INTEGER;
		this.valueInt = value;
	}

	protected void setValue(long value)
	{
		valueType = ValueType.LONG;
		this.valueLong = value;
	}

	protected void setValue(short value)
	{
		valueType = ValueType.SHORT;
		this.valueShort = value;
	}

	protected void setValue(byte[] value)
	{
		valueType = ValueType.BYTES;
		this.value = value;
	}

	@Override
	public Object getValue()
	{
		switch(valueType)
		{
			case BYTE:
				return valueByte;
			case CHAR:
				return valueChar;
			case DOUBLE:
				return valueDouble;
			case FLOAT:
				return valueFloat;
			case INTEGER:
				return valueInt;
			case LONG:
				return valueLong;
			case SHORT:
				return valueShort;
			case BOOLEAN:
				return valueBoolean;
			case BYTES:
			case STRING:
				return value;
			case NULL:
				throw raiseSerializationException("Value is null, serializer should've checked the type of token before calling getValue()");
		}

		throw new AssertionError("Unknown type of value: " + valueType);
	}

	@Override
	public String getString()
	{
		if(valueType != ValueType.STRING)
		{
			throw raiseSerializationException("Expected a string but got " + valueType);
		}

		return (String) value;
	}

	@Override
	public boolean getBoolean()
	{
		if(valueType != ValueType.BOOLEAN)
		{
			throw raiseSerializationException("Expected a boolean but got " + valueType);
		}

		return valueBoolean;
	}

	@Override
	public byte getByte()
	{
		switch(valueType)
		{
			case BYTE:
				return valueByte;
			case CHAR:
				return (byte) valueChar;
			case DOUBLE:
				return (byte) valueDouble;
			case FLOAT:
				return (byte) valueFloat;
			case INTEGER:
				return (byte) valueInt;
			case LONG:
				return (byte) valueLong;
			case SHORT:
				return (byte) valueShort;
			default:
				throw raiseSerializationException("Expected a value that can be turned into a byte but got " + valueType);
		}
	}

	@Override
	public char getChar()
	{
		switch(valueType)
		{
			case BYTE:
				return (char) valueByte;
			case CHAR:
				return valueChar;
			case DOUBLE:
				return (char) valueDouble;
			case FLOAT:
				return (char) valueFloat;
			case INTEGER:
				return (char) valueInt;
			case LONG:
				return (char) valueLong;
			case SHORT:
				return (char) valueShort;
			default:
				throw raiseSerializationException("Expected a value that can be turned into a char but got " + valueType);
		}
	}

	@Override
	public double getDouble()
	{
		switch(valueType)
		{
			case BYTE:
				return valueByte;
			case CHAR:
				return valueChar;
			case DOUBLE:
				return valueDouble;
			case FLOAT:
				return valueFloat;
			case INTEGER:
				return valueInt;
			case LONG:
				return valueLong;
			case SHORT:
				return valueShort;
			default:
				throw raiseSerializationException("Expected a value that can be turned into a double but got " + valueType);
		}
	}

	@Override
	public float getFloat()
	{
		switch(valueType)
		{
			case BYTE:
				return valueByte;
			case CHAR:
				return valueChar;
			case DOUBLE:
				return (float) valueDouble;
			case FLOAT:
				return valueFloat;
			case INTEGER:
				return valueInt;
			case LONG:
				return valueLong;
			case SHORT:
				return valueShort;
			default:
				throw raiseSerializationException("Expected a value that can be turned into a float but got " + valueType);
		}
	}

	@Override
	public long getLong()
	{
		switch(valueType)
		{
			case BYTE:
				return valueByte;
			case CHAR:
				return valueChar;
			case DOUBLE:
				return (long) valueDouble;
			case FLOAT:
				return (long) valueFloat;
			case INTEGER:
				return valueInt;
			case LONG:
				return valueLong;
			case SHORT:
				return valueShort;
			default:
				throw raiseSerializationException("Expected a value that can be turned into a long but got " + valueType);
		}
	}

	@Override
	public int getInt()
	{
		switch(valueType)
		{
			case BYTE:
				return valueByte;
			case CHAR:
				return valueChar;
			case DOUBLE:
				return (int) valueDouble;
			case FLOAT:
				return (int) valueFloat;
			case INTEGER:
				return valueInt;
			case LONG:
				return (int) valueLong;
			case SHORT:
				return valueShort;
			default:
				throw raiseSerializationException("Expected a value that can be turned into an integer but got " + valueType);
		}
	}

	@Override
	public short getShort()
	{
		switch(valueType)
		{
			case BYTE:
				return valueByte;
			case CHAR:
				return (short) valueChar;
			case DOUBLE:
				return (short) valueDouble;
			case FLOAT:
				return (short) valueFloat;
			case INTEGER:
				return (short) valueInt;
			case LONG:
				return (short) valueLong;
			case SHORT:
				return valueShort;
			default:
				throw raiseSerializationException("Expected a value that can be turned into a short but got " + valueType);
		}
	}

	@Override
	public byte[] getByteArray()
	{
		if(valueType != ValueType.BYTES)
		{
			throw raiseSerializationException("Expected bytres but got " + valueType);
		}

		return (byte[]) value;
	}
}
