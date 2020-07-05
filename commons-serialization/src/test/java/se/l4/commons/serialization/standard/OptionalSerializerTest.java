package se.l4.commons.serialization.standard;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.Optional;

import org.junit.Test;

import se.l4.commons.io.Bytes;
import se.l4.commons.io.StreamingCodec;
import se.l4.commons.serialization.DefaultSerializerCollection;
import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerCollection;
import se.l4.commons.serialization.format.StreamingFormat;
import se.l4.commons.types.Types;

public class OptionalSerializerTest
{
	@Test
	public void testDirectEmpty()
		throws IOException
	{
		Serializer<Optional<String>> s = new OptionalSerializer<>(new StringSerializer());

		StreamingCodec<Optional<String>> codec = s.toCodec(StreamingFormat.BINARY);
		Bytes data = Bytes.forObject(codec, Optional.empty());
		Optional<String> opt = data.asObject(codec);

		assertThat("optional not null", opt, notNullValue());
		assertThat("optional is empty", opt.isPresent(), is(false));
	}

	@Test
	public void testDirectNull()
		throws IOException
	{
		Serializer<Optional<String>> s = new OptionalSerializer<>(new StringSerializer());

		StreamingCodec<Optional<String>> codec = s.toCodec(StreamingFormat.BINARY);
		Bytes data = Bytes.forObject(codec, null);
		Optional<String> opt = data.asObject(codec);

		assertThat("optional not null", opt, notNullValue());
		assertThat("optional is empty", opt.isPresent(), is(false));
	}

	@Test
	public void testDirectWithValue()
		throws IOException
	{
		Serializer<Optional<String>> s = new OptionalSerializer<>(new StringSerializer());

		StreamingCodec<Optional<String>> codec = s.toCodec(StreamingFormat.BINARY);
		Bytes data = Bytes.forObject(codec, Optional.of("Hello"));
		Optional<String> opt = data.asObject(codec);

		assertThat("optional not null", opt, notNullValue());
		assertThat("optional is present", opt.isPresent(), is(true));
		assertThat("optional is Hello", opt.get(), is("Hello"));
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testViaCollection()
		throws IOException
	{
		SerializerCollection collection = new DefaultSerializerCollection();
		Serializer<Optional<String>> s = (Serializer) collection.find(
			Types.reference(Optional.class, String.class)
		).get();

		assertThat("serializer can be resolved", s, notNullValue());

		StreamingCodec<Optional<String>> codec = s.toCodec(StreamingFormat.BINARY);
		Bytes data = Bytes.forObject(codec, Optional.of("Hello"));
		Optional<String> opt = data.asObject(codec);

		assertThat("optional not null", opt, notNullValue());
		assertThat("optional is present", opt.isPresent(), is(true));
		assertThat("optional is Hello", opt.get(), is("Hello"));
	}
}
