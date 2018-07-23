package se.l4.commons.id;

/**
 * Implementation of {@link LongIdCodec} that encodes and decodes a long
 * as a Base-62 encoded string.
 *
 */
public class Base62LongIdCodec
	implements LongIdCodec<String>
{
	private final static char[] DIGITS = {
		'0', '1', '2', '3', '4', '5',
		'6', '7', '8', '9', 'a', 'b',
		'c', 'd', 'e', 'f', 'g', 'h',
		'i', 'j', 'k', 'l', 'm', 'n',
		'o', 'p', 'q', 'r', 's', 't',
		'u', 'v', 'w', 'x', 'y', 'z',
		'A', 'B', 'C', 'D', 'E', 'F',
		'G', 'H', 'I', 'J', 'K', 'L',
		'M', 'N', 'O', 'P', 'Q', 'R',
		'S', 'T', 'U', 'V', 'W', 'X',
		'Y', 'Z'
    };

	private final static int MAX = DIGITS.length;

	@Override
	public String encode(long i)
	{
		if(i < 0)
		{
			throw new NumberFormatException("Negative identifiers are not supported");
		}

		char[] buf = new char[11];
		int charPos = 10;

		int radix = MAX;
		i = -i;
		while(i <= -radix)
		{
			buf[charPos--] = DIGITS[(int) (-(i % radix))];
			i = i / radix;
		}
		buf[charPos] = DIGITS[(int) (-i)];

		return new String(buf, charPos, (11 - charPos));
	}

	@Override
	public long decode(String in)
	{
		long result = 0;
		long limit = -Long.MAX_VALUE;
		int radix = MAX;
		long multmin = limit / radix;

		for(int i=0, n=in.length(); i<n; i++)
		{
			char c = in.charAt(i);
			int digit;
			if(c >= '0' && c <= '9')
			{
				digit = c - '0';
			}
			else if(c >= 'a' && c <= 'z')
			{
				digit = 10 + c - 'a';
			}
			else if(c >= 'A' && c <= 'Z')
			{
				digit = 36 + c - 'A';
			}
			else
			{
				throw new NumberFormatException("Not an identifier: " + in);
			}

			if(result < multmin)
			{
				throw new NumberFormatException("Not an identifier: " + in);
			}
			result *= radix;
			if(result < limit + digit)
			{
				throw new NumberFormatException("Not an identifier: " + in);
			}
			result -= digit;
		}

		return -result;
	}
}
