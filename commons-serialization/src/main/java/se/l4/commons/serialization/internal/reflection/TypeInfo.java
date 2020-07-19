package se.l4.commons.serialization.internal.reflection;

import java.util.Map;

import org.eclipse.collections.api.map.MapIterable;

import se.l4.commons.serialization.QualifiedName;
import se.l4.commons.serialization.ReflectionSerializer;
import se.l4.commons.serialization.SerializerFormatDefinition;

/**
 * Information about a type used with {@link ReflectionSerializer}.
 *
 * @author Andreas Holstenson
 *
 */
public class TypeInfo<T>
{
	private final Class<T> type;
	private final QualifiedName name;
	private final FieldDefinition[] fields;
	private final MapIterable<String, FieldDefinition> fieldMap;
	private final FactoryDefinition<T>[] factories;
	private final SerializerFormatDefinition formatDefinition;

	public TypeInfo(
		Class<T> type,
		QualifiedName qualifiedName,
		FactoryDefinition<T>[] factories,
		MapIterable<String, FieldDefinition> fieldMap,
		FieldDefinition[] fields
	)
	{
		this.type = type;
		this.name = qualifiedName;
		this.factories = factories;
		this.fieldMap = fieldMap;
		this.fields = fields;

		SerializerFormatDefinition.Builder builder = SerializerFormatDefinition.builder();
		for(FieldDefinition fdef : fields)
		{
			builder.field(fdef.getName())
				.withType(fdef.getType())
				.withHints(fdef.getHints())
				.using(fdef.getSerializer());
		}
		formatDefinition = builder.build();
	}

	public Class<T> getType()
	{
		return type;
	}

	public QualifiedName getName()
	{
		return name;
	}

	public FieldDefinition[] getAllFields()
	{
		return fields;
	}

	public FieldDefinition getField(String name)
	{
		return fieldMap.get(name);
	}

	/**
	 * Create a new instance.
	 *
	 * @param fields
	 * @return
	 */
	public T newInstance(Map<String, Object> fields)
	{
		try
		{
			FactoryDefinition<T> bestDef = factories[0];
			int bestScore = bestDef.getScore(fields);

			for(int i=1, n=factories.length; i<n; i++)
			{
				int score = factories[i].getScore(fields);
				if(score > bestScore)
				{
					bestDef = factories[i];
					bestScore = score;
				}
			}

			return bestDef.create(fields);
		}
		catch(RuntimeException e)
		{
			throw new RuntimeException("Could not create " + type + "; " + e.getMessage(), e);
		}
	}

	public FactoryDefinition<T> findSingleFactoryWithEverything()
	{
		int fields = this.getAllFields().length;
		FactoryDefinition<T> result = null;
		for(FactoryDefinition<T> def : factories)
		{
			if(! def.hasSerializedFields()) continue;

			if(fields == def.getFieldCount())
			{
				// Set this factory as our candidate
				result = def;
			}
			else if(result != null)
			{
				// A factory was found, but this factory does not cover everything so a single does not exist
				return null;
			}
		}

		return result;
	}

	public SerializerFormatDefinition getFormatDefinition()
	{
		return formatDefinition;
	}
}
