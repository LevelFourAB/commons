package se.l4.commons.config.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ValidatorFactory;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;

import se.l4.commons.config.Config;
import se.l4.commons.config.ConfigException;
import se.l4.commons.config.Config.Builder;
import se.l4.commons.config.sources.ConfigSource;
import se.l4.commons.config.sources.EnvironmentConfigSource;
import se.l4.commons.config.sources.FileConfigSource;
import se.l4.commons.config.sources.MapBasedConfigSource;
import se.l4.commons.config.sources.MergingConfigSource;
import se.l4.commons.io.IOSupplier;
import se.l4.commons.serialization.DefaultSerializers;
import se.l4.commons.serialization.Serializers;

/**
 * Builder for configuration instances.
 *
 * @author Andreas Holstenson
 *
 */
public class ConfigBuilderImpl
	implements Config.Builder
{
	private final MutableMap<String, Object> keys;
	private final List<IOSupplier<ConfigSource>> suppliers;

	private Serializers collection;
	private ValidatorFactory validatorFactory;

	private File root;

	public ConfigBuilderImpl()
	{
		suppliers = new ArrayList<>();
		collection = new DefaultSerializers();

		keys = Maps.mutable.empty();
	}

	@Override
	public Config.Builder withSerializers(Serializers serializers)
	{
		this.collection = serializers;
		return this;
	}

	@Override
	public Config.Builder withValidatorFactory(ValidatorFactory validation)
	{
		this.validatorFactory = validation;
		return this;
	}

	@Override
	public Config.Builder withRoot(String root)
	{
		return withRoot(new File(root));
	}

	@Override
	public Config.Builder withRoot(Path path)
	{
		return withRoot(path.toFile());
	}

	@Override
	public Config.Builder withRoot(File root)
	{
		this.root = root;

		return this;
	}

	@Override
	public Config.Builder addFile(String path)
	{
		return addFile(new File(path));
	}

	@Override
	public Config.Builder addFile(Path path)
	{
		return addFile(path.toFile());
	}

	@Override
	public Config.Builder addFile(File file)
	{
		if(root == null)
		{
			root = file.getParentFile();
		}

		suppliers.add(() -> {
			if(! file.exists())
			{
				throw new ConfigException("The file " + file + " does not exist");
			}
			else if(! file.isFile())
			{
				throw new ConfigException(file + " is not a file");
			}
			else if(! file.canRead())
			{
				throw new ConfigException("Can not read " + file + ", check permissions");
			}

			return FileConfigSource.read(file);
		});

		return this;
	}

	@Override
	public Config.Builder addStream(InputStream stream)
	{
		suppliers.add(() -> FileConfigSource.read(stream));
		return this;
	}

	@Override
	public Builder addSource(ConfigSource source)
	{
		suppliers.add(() -> source);
		return this;
	}

	@Override
	public Config.Builder with(String key, Object value)
	{
		this.keys.put(key, value);
		return this;
	}

	@Override
	public Config build()
	{
		MutableList<ConfigSource> sources = Lists.mutable.empty();
		sources.add(new MapBasedConfigSource(keys.toImmutable()));

		for(IOSupplier<ConfigSource> supplier : suppliers)
		{
			try
			{
				sources.add(supplier.get());
			}
			catch(IOException e)
			{
				throw new ConfigException("Unable to read configuration; " + e.getMessage(), e);
			}
		}

		sources.add(new EnvironmentConfigSource());

		ConfigSource source = new MergingConfigSource(sources.toReversed());
		return new DefaultConfig(collection, validatorFactory, source, root);
	}
}
