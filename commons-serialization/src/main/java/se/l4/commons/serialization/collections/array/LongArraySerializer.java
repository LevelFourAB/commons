package se.l4.commons.serialization.collections.array;

import java.io.IOException;
import java.util.Arrays;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.collections.ArraySerializer;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.StreamingOutput;
import se.l4.commons.serialization.format.Token;

/**
 * Custom serializer for arrays of longs.
 */
public class LongArraySerializer
	implements Serializer<long[]>
{

	@Override
	public long[] read(StreamingInput in)
		throws IOException
	{
		in.next(Token.LIST_START);

		int length = 0;
		long[] current = new long[512];
		while(in.peek() != Token.LIST_END)
		{
			in.next(Token.VALUE);

			if(length == current.length)
			{
				int newSize = ArraySerializer.growArray(current.length);
				current = Arrays.copyOf(current, newSize);
			}

			current[length++] = in.readLong();
		}

		in.next(Token.LIST_END);
		return Arrays.copyOf(current, length);
	}

	@Override
	public void write(long[] object, StreamingOutput out)
		throws IOException
	{
		out.writeListStart();
		for(long v : object)
		{
			out.writeLong(v);
		}
		out.writeListEnd();
	}

}
