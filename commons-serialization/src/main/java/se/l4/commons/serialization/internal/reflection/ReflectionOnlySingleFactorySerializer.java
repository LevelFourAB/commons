package se.l4.commons.serialization.internal.reflection;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.base.Defaults;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerFormatDefinition;
import se.l4.commons.serialization.format.StreamingInput;
import se.l4.commons.serialization.format.StreamingOutput;
import se.l4.commons.serialization.format.Token;

/**
 * Serializer that uses a smarter mapping creating instances using a single factory.
 *
 * @author Andreas Holstenson
 *
 * @param <T>
 */
public class ReflectionOnlySingleFactorySerializer<T>
	implements Serializer<T>
{
	private final TypeInfo<T> type;
	private final FactoryDefinition<T> factory;
	private final Object[] defaultArguments;

	private final String[] names;
	private final FieldDefinition[] fields;
	private final int[] mapping;

	public ReflectionOnlySingleFactorySerializer(TypeInfo<T> type, FactoryDefinition<T> factory)
	{
		this.type = type;
		this.factory = factory;

		Map<String, Integer> tempMapping = new TreeMap<>();
		Object[] defaultArguments = new Object[factory.arguments.length];
		for(int i=0, n=factory.arguments.length; i<n; i++)
		{
			FactoryDefinition.Argument arg = factory.arguments[i];
			if(arg instanceof FactoryDefinition.SerializedArgument)
			{
				FactoryDefinition.SerializedArgument serializedArg = (FactoryDefinition.SerializedArgument) arg;

				String name = serializedArg.name;
				tempMapping.put(name, i);
				defaultArguments[i] = Defaults.defaultValue(serializedArg.type);

			}
		}
		this.defaultArguments = defaultArguments;

		String[] names = new String[tempMapping.size()];
		FieldDefinition[] fields = new FieldDefinition[tempMapping.size()];
		int[] mapping = new int[tempMapping.size()];
		int i = 0;
		for(Map.Entry<String, Integer> e : tempMapping.entrySet())
		{
			names[i] = e.getKey();
			fields[i] = type.getField(e.getKey());
			mapping[i] = e.getValue();

			i++;

		}

		this.names = names;
		this.fields = fields;
		this.mapping = mapping;
	}

	@Override
	public T read(StreamingInput in)
		throws IOException
	{
		in.next(Token.OBJECT_START);

		Object[] args = Arrays.copyOf(defaultArguments, defaultArguments.length);

		while(in.peek() != Token.OBJECT_END)
		{
			in.next(Token.KEY);
			String key = in.readString();

			int idx = Arrays.binarySearch(names, key);
			if(idx >= 0)
			{
				args[mapping[idx]] = fields[idx].read(in);
			}
			else
			{
				in.skipValue();
			}
		}

		in.next(Token.OBJECT_END);
		return factory.create(args);
	}

	@Override
	public void write(T object, String name, StreamingOutput stream)
		throws IOException
	{
		stream.writeObjectStart(name);

		for(FieldDefinition def : type.getAllFields())
		{
			def.write(object, stream);
		}

		stream.writeObjectEnd(name);
	}

	@Override
	public SerializerFormatDefinition getFormatDefinition()
	{
		return type.getFormatDefinition();
	}
}
