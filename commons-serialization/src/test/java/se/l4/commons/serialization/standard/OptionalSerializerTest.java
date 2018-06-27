package se.l4.commons.serialization.standard;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Optional;

import org.junit.Test;

import se.l4.commons.serialization.DefaultSerializerCollection;
import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerCollection;
import se.l4.commons.serialization.spi.Type;
import se.l4.commons.serialization.spi.TypeViaClass;

public class OptionalSerializerTest
{
	@Test
	public void testDirectEmpty()
	{
		Serializer<Optional<String>> s = new OptionalSerializer<>(new StringSerializer());
		
		byte[] data = s.toBytes(Optional.empty());
		Optional<String> opt = s.fromBytes(data);
		
		assertThat("optional not null", opt, notNullValue());
		assertThat("optional is empty", opt.isPresent(), is(false));
	}

	@Test
	public void testDirectNull()
	{
		Serializer<Optional<String>> s = new OptionalSerializer<>(new StringSerializer());
		
		byte[] data = s.toBytes(null);
		Optional<String> opt = s.fromBytes(data);
		
		assertThat("optional not null", opt, notNullValue());
		assertThat("optional is empty", opt.isPresent(), is(false));
	}

	@Test
	public void testDirectWithValue()
	{
		Serializer<Optional<String>> s = new OptionalSerializer<>(new StringSerializer());
		
		byte[] data = s.toBytes(Optional.of("Hello"));
		Optional<String> opt = s.fromBytes(data);
		
		assertThat("optional not null", opt, notNullValue());
		assertThat("optional is present", opt.isPresent(), is(true));
		assertThat("optional is Hello", opt.get(), is("Hello"));
	}

	@Test
	public void testViaCollection()
	{
		SerializerCollection collection = new DefaultSerializerCollection();
		Serializer<Optional<String>> s = (Serializer) collection.find(new Type()
		{
			@Override
			public Type[] getParameters()
			{
				return new Type[] { new TypeViaClass(String.class) };
			}
		
			@Override
			public Class<?> getErasedType()
			{
				return Optional.class;
			}
		});

		assertThat("serializer can be resolved", s, notNullValue());

		byte[] data = s.toBytes(Optional.of("Hello"));
		Optional<String> opt = s.fromBytes(data);
		
		assertThat("optional not null", opt, notNullValue());
		assertThat("optional is present", opt.isPresent(), is(true));
		assertThat("optional is Hello", opt.get(), is("Hello"));
	}
}
