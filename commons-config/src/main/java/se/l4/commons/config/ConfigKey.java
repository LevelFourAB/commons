package se.l4.commons.config;

import java.io.IOException;
import java.util.Optional;

import edu.umd.cs.findbugs.annotations.NonNull;
import se.l4.commons.serialization.Expose;
import se.l4.commons.serialization.ReflectionSerializer;
import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerFormatDefinition;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.StreamingOutput;
import se.l4.commons.serialization.format.Token;
import se.l4.commons.serialization.format.ValueType;

/**
 * A configuration key, represents the path of the config object that has been
 * deserialized. Used to resolve further configuration values.
 *
 * <p>
 * Use this to deserialize any field named {@code config:key} via {@link Config}.
 *
 * <p>
 * Example use with {@link Expose} and {@link ReflectionSerializer}:
 * <pre>
 * @Expose(ConfigKey.NAME)
 * private ConfigKey configKey;
 * </pre>
 *
 * @author Andreas Holstenson
 *
 */
public class ConfigKey
{
	public static final String NAME = "__commons__:configKey";

	private final Config config;
	private final String key;

	private ConfigKey(Config config, String key)
	{
		this.config = config;
		this.key = key;
	}

	/**
	 * Get the value of a sub path to this key.
	 *
	 * @param subPath
	 * @param type
	 * @return
	 */
	@NonNull
	public <T> Value<T> get(@NonNull String subPath, @NonNull Class<T> type)
	{
		return config.get(key + '.' + subPath, type);
	}

	/**
	 * Get the value of a sub path to this key.
	 *
	 * @param subPath
	 * @param type
	 * @return
	 */
	@NonNull
	public <T> Optional<T> asObject(String subPath, Class<T> type)
	{
		return config.asObject(key + '.' + subPath, type);
	}

	/**
	 * Get this object as another type.
	 *
	 * @param type
	 * @return
	 */
	public <T> T asObject(Class<T> type)
	{
		return config.asObject(key, type).get();
	}

	public static class ConfigKeySerializer
		implements Serializer<ConfigKey>
	{
		private final Config config;

		public ConfigKeySerializer(Config config)
		{
			this.config = config;
		}

		@Override
		public ConfigKey read(StreamingInput in)
			throws IOException
		{
			in.next(Token.VALUE);
			return new ConfigKey(config, in.readString());
		}

		@Override
		public void write(ConfigKey object, StreamingOutput stream)
			throws IOException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public SerializerFormatDefinition getFormatDefinition()
		{
			return SerializerFormatDefinition.forValue(ValueType.STRING);
		}
	}
}
