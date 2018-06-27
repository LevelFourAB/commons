package se.l4.commons.io;

import java.io.Closeable;
import java.io.DataInput;
import java.io.IOException;

public interface ExtendedDataInput
	extends DataInput, Closeable
{
	int read()
		throws IOException;

	int readVInt()
		throws IOException;

	long readVLong()
		throws IOException;

	String readString()
		throws IOException;

	Bytes readBytes()
		throws IOException;

	Bytes readTemporaryBytes()
		throws IOException;
}
