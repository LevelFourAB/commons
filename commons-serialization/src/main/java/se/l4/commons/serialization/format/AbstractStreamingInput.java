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
}
