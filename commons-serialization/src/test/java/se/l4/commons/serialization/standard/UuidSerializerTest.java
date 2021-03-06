package se.l4.commons.serialization.standard;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

import org.junit.Test;

import se.l4.commons.serialization.format.BinaryInput;
import se.l4.commons.serialization.format.BinaryOutput;

public class UuidSerializerTest
{
	@Test
	public void testOne()
	{
		UUID uuid = UUID.fromString("29fa14b7-6fb6-4d68-bec1-40307a949421");
		UUID second = writeAndRead(uuid);

		assertThat(second, is(uuid));
	}

	private UUID writeAndRead(UUID uuid)
	{
		UuidSerializer serializer = new UuidSerializer();

		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			serializer.write(uuid, new BinaryOutput(out));

			return serializer.read(new BinaryInput(new ByteArrayInputStream(out.toByteArray())));
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
