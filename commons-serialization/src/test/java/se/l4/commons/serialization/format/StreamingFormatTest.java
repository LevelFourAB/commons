package se.l4.commons.serialization.format;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import se.l4.commons.io.IOConsumer;
import se.l4.commons.io.IOSupplier;

/**
 * Abstract base class for testing of a {@link StreamingFormat}. Will test
 * that written data can be read.
 */
public abstract class StreamingFormatTest
{
	protected abstract StreamingFormat format();

	protected IOSupplier<StreamingInput> write(IOConsumer<StreamingOutput> output)
		throws IOException
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try(StreamingOutput out = format().createOutput(stream))
		{
			output.accept(out);
		}

		byte[] input = stream.toByteArray();
		return () -> format().createInput(new ByteArrayInputStream(input));
	}

	@Test
	public void testNull()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeNull("");
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.NULL);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testInt()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.write("", 12);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readInt(), is(12));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testIntNegative()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.write("", -2829);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readInt(), is(-2829));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testLong()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.write("", 1029l);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readLong(), is(1029l));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testLongNegative()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.write("", -1029l);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readLong(), is(-1029l));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testLongLarge()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.write("", 1324475548554l);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readLong(), is(1324475548554l));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testBooleanFalse()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.write("", false);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readBoolean(), is(false));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testBooleanTrue()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.write("", true);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readBoolean(), is(true));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testFloat()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.write("", 3.14f);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readFloat(), is(3.14f));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testDouble()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.write("", 89765.0);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readDouble(), is(89765.0));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testString()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.write("", "string value");
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readString(), is("string value"));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testByteArray()
		throws IOException
	{
		byte[] data = new byte[] { 0, -28, 42, 100 };
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.write("", data);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readByteArray(), is(data));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testObjectEmpty()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeObjectStart("");
			out.writeObjectEnd("");
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.OBJECT_START);
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testObjectValues()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeObjectStart("");
			out.write("key1", "value1");
			out.write("key2", 12l);
			out.writeObjectEnd("");
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.OBJECT_START);
			in.next(Token.KEY);
			assertThat(in.readString(), is("key1"));
			in.next(Token.VALUE);
			assertThat(in.readString(), is("value1"));
			in.next(Token.KEY);
			assertThat(in.readString(), is("key2"));
			in.next(Token.VALUE);
			assertThat(in.readLong(), is(12l));
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testObjectValuesWithNull()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeObjectStart("");
			out.writeNull("key1");
			out.write("key2", 12);
			out.writeObjectEnd("");
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.OBJECT_START);
			in.next(Token.KEY);
			assertThat(in.readString(), is("key1"));
			in.next(Token.NULL);
			assertThat(in.readDynamic(), nullValue());
			in.next(Token.KEY);
			assertThat(in.readString(), is("key2"));
			in.next(Token.VALUE);
			assertThat(in.readInt(), is(12));
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testListEmpty()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeListStart("");
			out.writeListEnd("");
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.LIST_START);
			in.next(Token.LIST_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testListWithSeveralValues()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeListStart("");
			out.write("entry", "value");
			out.write("entry", 74749);
			out.writeListEnd("");
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.LIST_START);
			in.next(Token.VALUE);
			assertThat(in.readString(), is("value"));
			in.next(Token.VALUE);
			assertThat(in.readInt(), is(74749));
			in.next(Token.LIST_END);
			in.next(Token.END_OF_STREAM);
		}
	}
}
