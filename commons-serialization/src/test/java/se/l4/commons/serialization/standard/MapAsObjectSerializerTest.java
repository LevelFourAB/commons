package se.l4.commons.serialization.standard;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import se.l4.commons.serialization.DefaultSerializers;
import se.l4.commons.serialization.SerializationTestHelper;
import se.l4.commons.serialization.collections.MapAsObjectSerializer;
import se.l4.commons.serialization.collections.MapSerializerResolver;
import se.l4.commons.serialization.collections.StringKey;

/**
 * Tests for {@link MapAsObjectSerializer} that is resolved from
 * {@link MapSerializerResolver} if the annotation hint {@link StringKey}
 * is present.
 *
 * @author Andreas Holstenson
 *
 */
public class MapAsObjectSerializerTest
{
	@Test
	public void testEmptyMapWithStrings()
	{
		MapAsObjectSerializer<String> serializer = new MapAsObjectSerializer<>(new StringSerializer());
		Map<String, String> map = new HashMap<>();
		SerializationTestHelper.testWriteAndRead(serializer, map);
	}

	@Test
	public void testMapWithStrings()
	{
		MapAsObjectSerializer<String> serializer = new MapAsObjectSerializer<>(new StringSerializer());
		Map<String, String> map = new HashMap<>();
		map.put("hello", "cookie");
		map.put("yum", null);
		SerializationTestHelper.testWriteAndRead(serializer, map);
	}

	@Test
	public void testMapWithDynamicSerializer()
	{
		DefaultSerializers collection = new DefaultSerializers();
		MapAsObjectSerializer<Object> serializer = new MapAsObjectSerializer<>(new DynamicSerializer.Impl(collection));
		Map<String, Object> map = new HashMap<>();

		map.put("hello", "cookie");
		map.put("world", 129l);
		map.put("yum", null);
		SerializationTestHelper.testWriteAndRead(serializer, map);
	}
}
