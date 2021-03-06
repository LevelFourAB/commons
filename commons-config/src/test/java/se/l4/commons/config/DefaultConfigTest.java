package se.l4.commons.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.junit.Test;

import se.l4.commons.serialization.Expose;
import se.l4.commons.serialization.ReflectionSerializer;
import se.l4.commons.serialization.Use;

public class DefaultConfigTest
{
	@Test
	public void testBasic()
	{

	}

	@Test
	public void testSizeObject()
	{
		Config config = Config.builder()
			.addStream(stream("medium: { width: 100, height: 100 }"))
			.build();

		Value<Size> size = config.get("medium", Size.class);
		assertThat(size, notNullValue());

		Size actual = size.get();
		assertThat(actual, notNullValue());

		assertThat(actual.width, is(100));
		assertThat(actual.height, is(100));
	}

	@Test
	public void testSizeObjectViaKeys()
	{
		Config config = Config.builder()
			.with("medium.width", 100)
			.with("medium.height", 100)
			.build();

		Value<Size> size = config.get("medium", Size.class);
		assertThat(size, notNullValue());

		Size actual = size.get();
		assertThat(actual, notNullValue());

		assertThat(actual.width, is(100));
		assertThat(actual.height, is(100));
	}

	@Test
	public void testThumbnailsObject()
	{
		Config config = Config.builder()
			.addStream(stream("thumbnails: { \n medium: { width: 100, height: 100 }\n }"))
			.build();

		Value<Thumbnails> value = config.get("thumbnails", Thumbnails.class);
		assertThat(value, notNullValue());

		Thumbnails thumbs = value.get();
		assertThat(thumbs, notNullValue());

		assertThat(thumbs.medium, notNullValue());
	}

	@Test
	public void testInvalidSize()
	{
		Config config = Config.builder()
			.withValidatorFactory(Validation.buildDefaultValidatorFactory())
			.addStream(stream("medium: { width: 100 }\n }"))
			.build();

		try
		{
			config.get("medium", Size.class);

			fail("validation should have failed");
		}
		catch(ConfigException e)
		{
		}
	}

	@Test
	public void testInvalidThumbnailsSize()
	{
		Config config = Config.builder()
			.withValidatorFactory(Validation.buildDefaultValidatorFactory())
			.addStream(stream("thumbnails: { \n medium: { width: 100, height: 4000 }\n }"))
			.build();

		try
		{
			config.get("thumbnails", Thumbnails.class);

			fail("validation should have failed");
		}
		catch(ConfigException e)
		{
		}
	}

	@Test
	public void testListAccessor()
	{
		Config config = Config.builder()
			.addStream(stream("values: [ \n \"one\", \n \"two\" \n ]"))
			.build();

		String value = config.asObject("values.0", String.class).get();
		assertThat(value, is("one"));

		value = config.asObject("values.1", String.class).get();
		assertThat(value, is("two"));
	}

	@Test
	public void testListAccessorWithSizes()
	{
		Config config = Config.builder()
			.addStream(stream("values: [ \n { width: 100, height: 100 }, \n { width: 200, height: 200 } \n ]"))
			.build();

		Size value = config.asObject("values.0", Size.class).get();
		assertThat(value, notNullValue());
		assertThat(value.width, is(100));
		assertThat(value.height, is(100));

		value = config.asObject("values.1", Size.class).get();
		assertThat(value, notNullValue());
		assertThat(value.width, is(200));
		assertThat(value.height, is(200));
	}

	@Test
	public void testListAccessorWithSubPath()
	{
		Config config = Config.builder()
			.addStream(stream("values: [ \n { width: 100, height: 100 } \n ]"))
			.build();

		Integer value = config.asObject("values.0.width", Integer.class).get();
		assertThat(value, is(100));
	}

	private InputStream stream(String in)
	{
		return new ByteArrayInputStream(in.getBytes(StandardCharsets.UTF_8));
	}

	@Use(ReflectionSerializer.class)
	public static class Thumbnails
	{
		@Expose @Valid
		public Size medium;
		@Expose @Valid
		public Size large;
	}

	@Use(ReflectionSerializer.class)
	public static class Size
	{
		@Min(1) @Max(1000)
		@Expose
		public int width;

		@Min(1) @Max(1000)
		@Expose
		public int height;
	}
}
