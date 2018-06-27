package se.l4.commons.serialization.standard;

import java.io.IOException;

import se.l4.commons.serialization.QualifiedName;
import se.l4.commons.serialization.SerializationException;
import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerCollection;
import se.l4.commons.serialization.SerializerFormatDefinition;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.StreamingOutput;
import se.l4.commons.serialization.format.Token;
import se.l4.commons.serialization.internal.SerializerFormatDefinitionBuilderImpl;

/**
 * Serializer that will attempt to dynamically resolve serializers based on
 * their name.
 *
 * @author Andreas Holstenson
 *
 */
public class CompactDynamicSerializer
	implements Serializer<Object>
{
	private final SerializerCollection collection;
	private final SerializerFormatDefinition formatDefinition;

	public CompactDynamicSerializer(SerializerCollection collection)
	{
		this.collection = collection;

		formatDefinition = new SerializerFormatDefinitionBuilderImpl()
			.list(SerializerFormatDefinition.any())
			.build();
	}

	@Override
	public Object read(StreamingInput in)
		throws IOException
	{
		// Read start of object
		in.next(Token.LIST_START);

		in.next(in.peek() == Token.NULL ? Token.NULL : Token.VALUE);
		String namespace = in.getString();
		if(namespace == null) namespace = "";

		in.next(Token.VALUE);
		String name = in.getString();

		Object result = null;

		Serializer<?> serializer = collection.find(namespace, name);
		if(serializer == null)
		{
			throw new SerializationException("No serializer found for `" + name + (namespace != null ? "` in `" + namespace + "`" : "`"));
		}
		else
		{
			result = serializer.read(in);
		}

		in.next(Token.LIST_END);
		return result;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void write(Object object, String name, StreamingOutput stream)
		throws IOException
	{
		Serializer serializer = collection.find(object.getClass());
		QualifiedName qname = collection.findName(serializer);
		if(qname == null)
		{
			throw new SerializationException("Tried to use dynamic serialization for " + object.getClass() + ", but type has no name");
		}

		stream.writeListStart(name);

		if(! qname.getNamespace().equals(""))
		{
			stream.write("namespace", qname.getNamespace());
		}
		else
		{
			stream.writeNull("namespace");
		}

		stream.write("name", qname.getName());

		serializer.write(object, "value", stream);

		stream.writeListEnd(name);
	}

	@Override
	public SerializerFormatDefinition getFormatDefinition()
	{
		return formatDefinition;
	}
}
