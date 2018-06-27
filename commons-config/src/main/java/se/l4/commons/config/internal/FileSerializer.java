package se.l4.commons.config.internal;

import java.io.File;
import java.io.IOException;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerFormatDefinition;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.StreamingOutput;
import se.l4.commons.serialization.format.Token;
import se.l4.commons.serialization.format.ValueType;

/**
 * Serializer for {@link File}.
 *
 * @author Andreas Holstenson
 *
 */
public class FileSerializer
	implements Serializer<File>
{
	private final File root;
	private final SerializerFormatDefinition formatDefinition;

	public FileSerializer(File root)
	{
		this.root = root;

		formatDefinition = SerializerFormatDefinition.forValue(ValueType.STRING);
	}

	@Override
	public File read(StreamingInput in)
		throws IOException
	{
		in.next(Token.VALUE);

		String file = in.getString();
		if(file == null) return null;

		File temp = new File(file);
		if(temp.isAbsolute())
		{
			return temp;
		}
		else
		{
			return new File(root, file);
		}
	}

	@Override
	public void write(File object, String name, StreamingOutput stream)
		throws IOException
	{
	}

	@Override
	public SerializerFormatDefinition getFormatDefinition()
	{
		return formatDefinition;
	}
}
