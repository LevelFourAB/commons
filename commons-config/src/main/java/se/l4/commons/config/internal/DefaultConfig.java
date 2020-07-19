package se.l4.commons.config.internal;

import java.io.File;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Path.Node;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.eclipse.collections.api.RichIterable;

import se.l4.commons.config.Config;
import se.l4.commons.config.ConfigException;
import se.l4.commons.config.ConfigKey;
import se.l4.commons.config.Value;
import se.l4.commons.config.internal.streaming.MapInput;
import se.l4.commons.config.internal.streaming.NullInput;
import se.l4.commons.config.sources.ConfigSource;
import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.Serializers;
import se.l4.commons.serialization.WrappedSerializers;
import se.l4.commons.serialization.format.StreamingInput;

/**
 * Default implementation of {@link Config}.
 *
 * @author Andreas Holstenson
 *
 */
public class DefaultConfig
	implements Config
{
	private final Serializers collection;
	private final ValidatorFactory validatorFactory;
	private final ConfigSource source;

	DefaultConfig(
		Serializers collection,
		ValidatorFactory validatorFactory,
		ConfigSource source,
		File root
	)
	{
		this.collection = new WrappedSerializers(collection);
		this.validatorFactory = validatorFactory;
		this.source = source;

		collection.bind(File.class, new FileSerializer(root));
		collection.bind(ConfigKey.class, new ConfigKey.ConfigKeySerializer(this));
	}

	@Override
	public <T> Optional<T> asObject(String path, Class<T> type)
	{
		return Optional.ofNullable(get(path, type).getOrDefault(null));
	}

	@Override
	public <T> Optional<T> asObject(String path, Serializer<T> serializer)
	{
		return Optional.ofNullable(get(path, serializer).getOrDefault(null));
	}

	@Override
	public RichIterable<String> keys(String path)
	{
		Objects.requireNonNull(path);
		return source.getKeys(path);
	}

	private void validateInstance(String path, Object object)
	{
		if(validatorFactory == null) return;

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<Object>> violations = validator.validate(object);

		if(violations.isEmpty())
		{
			// No violations
			return;
		}

		StringBuilder builder = new StringBuilder("Validation failed for `" + path + "`:\n");

		for(ConstraintViolation<Object> violation : violations)
		{
			builder
				.append("* ")
				.append(join(violation.getPropertyPath()))
				.append(violation.getMessage())
				.append("\n");
		}

		throw new ConfigException(builder.toString());
	}

	private String join(Path path)
	{
		StringBuilder builder = new StringBuilder();
		for(Node node : path)
		{
			if(builder.length() > 0)
			{
				builder.append(".");
			}

			builder.append(node.getName());
		}

		if(builder.length() > 0)
		{
			builder.append(": ");
		}

		return builder.toString();
	}

	@Override
	public <T> Value<T> get(String path, Class<T> type)
	{
		Objects.requireNonNull(path);
		Objects.requireNonNull(type);

		Serializer<T> serializer = collection.find(type);
		return get(path, serializer);
	}

	@Override
	public <T> Value<T> get(String path, Serializer<T> serializer)
	{
		Objects.requireNonNull(path);
		Objects.requireNonNull(serializer);

		StreamingInput input = MapInput.resolveInput(source, path);
		if(input instanceof NullInput)
		{
			return new ValueImpl<T>(path, false, null);
		}

		try
		{
			T instance = serializer.read(input);

			validateInstance(path, instance);

			return new ValueImpl<T>(path, true, instance);
		}
		catch(ConfigException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new ConfigException("Unable to get config data at `" + path + "`; " + e.getMessage(), e);
		}
	}

	private static class ValueImpl<T>
		implements Value<T>
	{
		private final String path;
		private final boolean exists;
		private final T instance;

		public ValueImpl(String path, boolean exists, T instance)
		{
			this.path = path;
			this.exists = exists;
			this.instance = instance;
		}

		@Override
		public T get()
		{
			if(! exists)
			{
				throw new ConfigException("Unable to get config value at `" + path + "`");
			}
			return instance;
		}

		@Override
		public T getOrDefault(T defaultInstance)
		{
			return exists ? instance : defaultInstance;
		}

		@Override
		public boolean exists()
		{
			return exists;
		}
	}
}
