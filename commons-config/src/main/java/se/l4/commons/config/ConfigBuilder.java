package se.l4.commons.config;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

import javax.validation.ValidatorFactory;

import edu.umd.cs.findbugs.annotations.NonNull;
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
	@NonNull
	ConfigBuilder withSerializerCollection(@NonNull SerializerCollection serializers);

	/**
	 * Set the {@link ValidatorFactory} to use when validating loaded
	 * configuration objects.
	 *
	 * @param validation
	 * @return
	 */
	@NonNull
	ConfigBuilder withValidatorFactory(@NonNull ValidatorFactory validation);

	/**
	 * Set the root folder of the configuration.
	 *
	 * @param root
	 * @return
	 */
	@NonNull
	ConfigBuilder withRoot(@NonNull String root);

	/**
	 * Set the root folder of the configuration.
	 *
	 * @param path
	 * @return
	 */
	@NonNull
	ConfigBuilder withRoot(@NonNull Path path);

	/**
	 * Set the root folder of the configuration.
	 *
	 * @param root
	 * @return
	 */
	@NonNull
	ConfigBuilder withRoot(@NonNull File root);

	/**
	 * Add a file that should be loaded.
	 *
	 * @param path
	 * @return
	 */
	@NonNull
	ConfigBuilder addFile(@NonNull String path);

	/**
	 * Add a file that should be loaded.
	 *
	 * @param path
	 * @return
	 */
	@NonNull
	ConfigBuilder addFile(@NonNull Path path);

	/**
	 * Add a file that should be loaded.
	 *
	 * @param file
	 * @return
	 */
	@NonNull
	ConfigBuilder addFile(@NonNull File file);

	/**
	 * Add a stream that should be read.
	 *
	 * @param stream
	 * @return
	 */
	@NonNull
	ConfigBuilder addStream(@NonNull InputStream stream);

	/**
	 * Create the configuration object. This will load any declared input
	 * files.
	 *
	 * @return
	 */
	@NonNull
	Config build();

}
