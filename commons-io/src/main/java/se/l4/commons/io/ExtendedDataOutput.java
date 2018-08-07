package se.l4.commons.io;

import java.io.Closeable;
import java.io.DataOutput;
import java.io.IOException;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface ExtendedDataOutput
	extends DataOutput, Closeable
{
	void writeVInt(int i)
		throws IOException;

	void writeVLong(long l)
		throws IOException;

	void writeString(@NonNull String string)
		throws IOException;

	void writeBytes(@NonNull Bytes bytes)
		throws IOException;
}
