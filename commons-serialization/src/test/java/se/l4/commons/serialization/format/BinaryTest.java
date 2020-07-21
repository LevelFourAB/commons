package se.l4.commons.serialization.format;

/**
 * Tests for the binary format. Tests by first writing some values and then
 * checking that it is possible to read the serialized stream.
 *
 * @author Andreas Holstenson
 *
 */
public class BinaryTest
	extends StreamingFormatTest
{
	@Override
	protected StreamingFormat format()
	{
		return StreamingFormat.BINARY;
	}
}
