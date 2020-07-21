package se.l4.commons.serialization.reflection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Before;

import se.l4.commons.serialization.DefaultSerializers;
import se.l4.commons.serialization.ReflectionSerializer;
import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.Serializers;
import se.l4.commons.serialization.format.JsonInput;
import se.l4.commons.serialization.format.JsonOutput;
import se.l4.commons.serialization.internal.TypeEncounterImpl;
import se.l4.commons.types.Types;
import se.l4.commons.types.mapping.OutputDeduplicator;

public class ReflectionTest
{
	protected Serializers collection;

	@Before
	public void beforeTests()
	{
		collection = new DefaultSerializers();
	}

	public <T> Serializer<T> resolve(Class<T> type)
	{
		return new ReflectionSerializer<T>()
			.find(new TypeEncounterImpl(collection, OutputDeduplicator.none(), Types.reference(type)))
			.get();
	}

	protected <T> void testSymmetry(Serializer<T> serializer, T instance)
	{
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			JsonOutput jsonOut = new JsonOutput(out);
			jsonOut.writeObject(serializer, instance);
			jsonOut.flush();

			ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
			JsonInput jsonIn = new JsonInput(new InputStreamReader(in));
			T read = serializer.read(jsonIn);

			assertThat("Deserialized instance does not match", instance, is(read));
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
