package se.l4.commons.config.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ValidatorFactory;

import se.l4.commons.config.Config;
import se.l4.commons.config.ConfigBuilder;
import se.l4.commons.config.ConfigException;
import se.l4.commons.io.Bytes;
import se.l4.commons.serialization.DefaultSerializerCollection;
import se.l4.commons.serialization.SerializerCollection;

/**
 * Builder for configuration instances.
 * 
 * @author Andreas Holstenson
 *
 */
public class ConfigBuilderImpl implements ConfigBuilder
{
	private final List<Bytes> suppliers;
	
	private SerializerCollection collection;
	private ValidatorFactory validatorFactory;
	
	private File root;
	
	public ConfigBuilderImpl()
	{
		suppliers = new ArrayList<>();
		collection = new DefaultSerializerCollection();
	}
	
	@Override
	public ConfigBuilder withSerializerCollection(SerializerCollection serializers)
	{
		this.collection = serializers;
		return this;
	}
	
	@Override
	public ConfigBuilder withValidatorFactory(ValidatorFactory validation)
	{
		this.validatorFactory = validation;
		return this;
	}
	
	@Override
	public ConfigBuilder withRoot(String root)
	{
		return withRoot(new File(root));
	}
	
	@Override
	public ConfigBuilder withRoot(Path path)
	{
		return withRoot(path.toFile());
	}
	
	@Override
	public ConfigBuilder withRoot(File root)
	{
		this.root = root;
		
		return this;
	}
	
	@Override
	public ConfigBuilder addFile(String path)
	{
		return addFile(new File(path));
	}
	
	@Override
	public ConfigBuilder addFile(Path path)
	{
		return addFile(path.toFile());
	}

	@Override
	public ConfigBuilder addFile(File file)
	{
		if(root == null)
		{
			root = file.getParentFile();
		}
		
		suppliers.add(Bytes.create(() -> {
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
			
			return new FileInputStream(file);
		}));
		
		return this;
	}
	
	@Override
	public ConfigBuilder addStream(final InputStream stream)
	{
		suppliers.add(Bytes.create(() -> stream));
		return this;
	}
	
	@Override
	public Config build()
	{
		Map<String, Object> data = new HashMap<String, Object>();
		
		for(Bytes bytes : suppliers)
		{
			try(InputStream in = bytes.asInputStream())
			{
				Map<String, Object> readConfig = RawFormatReader.read(in);
				ConfigResolver.resolveTo(readConfig, data);
			}
			catch(IOException e)
			{
				throw new ConfigException("Unable to read configuration; " + e.getMessage(), e);
			}
		}
		
		return new DefaultConfig(collection, validatorFactory, root, data);
	}
}
