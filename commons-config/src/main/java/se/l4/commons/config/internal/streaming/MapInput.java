package se.l4.commons.config.internal.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import se.l4.commons.io.Bytes;
import se.l4.commons.serialization.format.AbstractStreamingInput;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.Token;

/**
 * Implementation of {@link StreamingInput} that works on a objects.
 *
 * @author Andreas Holstenson
 *
 */
public class MapInput
	extends AbstractStreamingInput
{
	private enum State
	{
		START,
		KEY,
		VALUE,
		END,
		DONE
	}

	private State state;
	private State previousState;
	private Token token;

	private Map.Entry<String, Object> entry;
	private Iterator<Map.Entry<String, Object>> iterator;

	private StreamingInput subInput;
	private String key;

	public MapInput(String key, Map<String, Object> root)
	{
		this.key = key;
		state = State.START;

		iterator = root.entrySet().iterator();
	}

	@Override
	public void close()
		throws IOException
	{
		// Nothing to close
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static StreamingInput resolveInput(String key, Object data)
	{
		if(data instanceof Map)
		{
			return new MapInput(key, (Map) data);
		}
		else if(data instanceof List)
		{
			return new ListInput(key, (List) data);
		}
		else if(data == null)
		{
			return new NullInput(key);
		}
		else
		{
			return new ValueInput(key, data);
		}
	}

	private StreamingInput resolveInput()
	{
		String newKey = key.isEmpty() ? entry.getKey() : key + '.' + entry.getKey();
		return resolveInput(newKey, entry.getValue());
	}

	@Override
	protected IOException raiseException(String message)
	{
		return new IOException(key + ": " + message);
	}

	@Override
	public Token peek()
		throws IOException
	{
		switch(state)
		{
			case START:
				return Token.OBJECT_START;
			case KEY:
				return Token.KEY;
			case VALUE:
				Token peeked = subInput.peek();
				if(peeked != Token.END_OF_STREAM)
				{
					return peeked;
				}
				else
				{
					advancePosition();
					return peek();
				}
			case END:
				return Token.OBJECT_END;
		}

		return Token.END_OF_STREAM;
	}

	@Override
	public Token next0()
		throws IOException
	{
		switch(state)
		{
			case START:
				// Check what the next state should be
				advancePosition();
				return token = Token.OBJECT_START;
			case KEY:
				setState(State.VALUE);
				subInput = resolveInput();
				return token = Token.KEY;
			case VALUE:
				/*
				 * Value state, check the sub input until it returns null
				 */
				Token t = subInput.next();
				if(t == Token.END_OF_STREAM)
				{
					// Nothing left in the value, advance and check again
					advancePosition();
					return next();
				}

				setState(State.VALUE);
				return token = t;
			case END:
				setState(State.DONE);
				return token = Token.OBJECT_END;
		}

		return token = Token.END_OF_STREAM;
	}

	private void setState(State state)
	{
		previousState = this.state;
		this.state = state;
	}

	private void advancePosition()
	{
		if(iterator.hasNext())
		{
			entry = iterator.next();
			setState(State.KEY);
		}
		else
		{
			setState(State.END);
		}
	}

	@Override
	public Token current()
	{
		return subInput != null ? subInput.current() : token;
	}

	@Override
	public Object readDynamic()
		throws IOException
	{
		switch(previousState)
		{
			case KEY:
				return entry.getKey();
			case VALUE:
				return subInput.readDynamic();
		}

		return null;
	}

	@Override
	public String readString()
		throws IOException
	{
		switch(previousState)
		{
			case KEY:
				return entry.getKey();
			case VALUE:
				return subInput.readString();
			default:
				throw raiseException("Not reading a value");
		}
	}

	@Override
	public boolean readBoolean()
		throws IOException
	{
		switch(previousState)
		{
			case VALUE:
				return subInput.readBoolean();
			default:
				throw raiseException("Not reading a value");
		}
	}

	@Override
	public double readDouble()
		throws IOException
	{
		switch(previousState)
		{
			case VALUE:
				return subInput.readDouble();
			default:
				throw raiseException("Not reading a value");
		}
	}

	@Override
	public float readFloat()
		throws IOException
	{
		switch(previousState)
		{
			case VALUE:
				return subInput.readFloat();
			default:
				throw raiseException("Not reading a value");
		}
	}

	@Override
	public long readLong()
		throws IOException
	{
		switch(previousState)
		{
			case VALUE:
				return subInput.readLong();
			default:
				throw raiseException("Not reading a value");
		}
	}

	@Override
	public int readInt()
		throws IOException
	{
		switch(previousState)
		{
			case VALUE:
				return subInput.readInt();
			default:
				throw raiseException("Not reading a value");
		}
	}

	@Override
	public short readShort()
		throws IOException
	{
		switch(previousState)
		{
			case VALUE:
				return subInput.readShort();
			default:
				throw raiseException("Not reading a value");
		}
	}

	@Override
	public byte readByte()
		throws IOException
	{
		switch(previousState)
		{
			case VALUE:
				return subInput.readByte();
			default:
				throw raiseException("Not reading a value");
		}
	}

	@Override
	public char readChar()
		throws IOException
	{
		switch(previousState)
		{
			case VALUE:
				return subInput.readChar();
			default:
				throw raiseException("Not reading a value");
		}
	}

	@Override
	public Bytes readBytes()
		throws IOException
	{
		switch(previousState)
		{
			case VALUE:
				return subInput.readBytes();
			default:
				throw raiseException("Not reading a value");
		}
	}

	@Override
	public byte[] readByteArray()
		throws IOException
	{
		switch(previousState)
		{
			case VALUE:
				return subInput.readByteArray();
			default:
				throw raiseException("Not reading a value");
		}
	}

	@Override
	public InputStream asInputStream()
		throws IOException
	{
		switch(previousState)
		{
			case VALUE:
				return subInput.asInputStream();
			default:
				throw raiseException("Not reading a value");
		}
	}
}
