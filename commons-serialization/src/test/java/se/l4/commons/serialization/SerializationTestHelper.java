package se.l4.commons.serialization;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;

import se.l4.commons.serialization.format.BinaryInput;
import se.l4.commons.serialization.format.BinaryOutput;
import se.l4.commons.serialization.format.JsonInput;
import se.l4.commons.serialization.format.JsonOutput;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.StreamingOutput;

public class SerializationTestHelper
{
	private SerializationTestHelper()
	{
	}

	public static <T> void testWriteAndRead(Serializer<T> serializer, T object)
	{
		testWriteAndRead(serializer, object, BinaryInput::new, BinaryOutput::new);
		testWriteAndRead(serializer, object, JsonInput::new, JsonOutput::new);
	}

	public static <T> void testWriteAndRead(Serializer<T> serializer, T object,
			Function<InputStream, StreamingInput> inputFactory,
			Function<OutputStream, StreamingOutput> outputFactory)
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try(StreamingOutput so = outputFactory.apply(out))
		{
			serializer.write(object, so);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		try(StreamingInput si = inputFactory.apply(in))
		{
			T value = serializer.read(si);


			assertThat(value, is(object));
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
