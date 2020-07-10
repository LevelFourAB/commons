package se.l4.commons.config.internal.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import se.l4.commons.io.Bytes;
import se.l4.commons.serialization.format.AbstractStreamingInput;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.Token;

/**
 * Input that works on lists.
 *
 * @author Andreas Holstenson
 *
 */
public class ListInput
	extends AbstractStreamingInput
{
	private enum State
	{
		START,
		VALUE,
		END,
		DONE
	}

	private State state;
	private State previousState;

	private Iterator<Object> iterator;
	private Object object;

	private StreamingInput subInput;
	private String key;

	public ListInput(String key, List<Object> root)
	{
		this.key = key;
		state = State.START;

		iterator = root.iterator();
	}

	@Override
	public void close()
		throws IOException
	{
		// Nothing to close
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
				return Token.LIST_START;
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
				return Token.LIST_END;
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
				return Token.LIST_START;
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
				return t;
			case END:
				setState(State.DONE);
				return Token.LIST_END;
		}

		return Token.END_OF_STREAM;
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
			object = iterator.next();
			subInput = MapInput.resolveInput(key, object);
			setState(State.VALUE);
		}
		else
		{
			setState(State.END);
		}
	}

	@Override
	public Token current()
	{
		return subInput != null ? subInput.current() : super.current();
	}

	@Override
	public Object readDynamic()
		throws IOException
	{
		switch(previousState)
		{
			case VALUE:
				return subInput.readDynamic();
			default:
				throw raiseException("Not reading a value");
		}
	}

	@Override
	public String readString()
		throws IOException
	{
		switch(state)
		{
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
		switch(state)
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
		switch(state)
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
		switch(state)
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
		switch(state)
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
		switch(state)
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
		switch(state)
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
		switch(state)
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
		switch(state)
		{
			case VALUE:
				return subInput.readChar();
			default:
				throw raiseException("Not reading a value");
		}
	}

	@Override
	public byte[] readByteArray()
		throws IOException
	{
		switch(state)
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
		switch(state)
		{
			case VALUE:
				return subInput.asInputStream();
			default:
				throw raiseException("Not reading a value");
		}
	}

	@Override
	public Bytes readBytes()
		throws IOException
	{
		switch(state)
		{
			case VALUE:
				return subInput.readBytes();
			default:
				throw raiseException("Not reading a value");
		}
	}
}
