package se.l4.commons.config;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

import javax.validation.ValidatorFactory;

import se.l4.commons.serialization.SerializerCollection;

/**
 * Builder for instances of {@link Config}. Makes it easy to create a
 * configuration over several files. When creating a configuration one can opt
 * to use a {@link #withSerializerCollection(SerializerCollection) custom serializer collection}
 * and its possible to activate validation via {@link #withValidatorFactory(ValidatorFactory)}.
 *
 * @author Andreas Holstenson
 *
 */
public interface ConfigBuilder
{
	/**
	 * Set the the {@link SerializerCollection} to use when reading the
	 * configuration files.
	 *
	 * @param serializers
	 * @return
	 */
	ConfigBuilder withSerializerCollection(SerializerCollection serializers);

	/**
	 * Set the {@link ValidatorFactory} to use when validating loaded
	 * configuration objects.
	 *
	 * @param validation
	 * @return
	 */
	ConfigBuilder withValidatorFactory(ValidatorFactory validation);

	/**
	 * Set the root folder of the configuration.
	 *
	 * @param root
	 * @return
	 */
	ConfigBuilder withRoot(String root);

	/**
	 * Set the root folder of the configuration.
	 *
	 * @param path
	 * @return
	 */
	ConfigBuilder withRoot(Path path);

	/**
	 * Set the root folder of the configuration.
	 *
	 * @param root
	 * @return
	 */
	ConfigBuilder withRoot(File root);

	/**
	 * Add a file that should be loaded.
	 *
	 * @param path
	 * @return
	 */
	ConfigBuilder addFile(String path);

	/**
	 * Add a file that should be loaded.
	 *
	 * @param path
	 * @return
	 */
	ConfigBuilder addFile(Path path);

	/**
	 * Add a file that should be loaded.
	 *
	 * @param file
	 * @return
	 */
	ConfigBuilder addFile(File file);

	/**
	 * Add a stream that should be read.
	 *
	 * @param stream
	 * @return
	 */
	ConfigBuilder addStream(InputStream stream);

	/**
	 * Create the configuration object. This will load any declared input
	 * files.
	 *
	 * @return
	 */
	Config build();

}
