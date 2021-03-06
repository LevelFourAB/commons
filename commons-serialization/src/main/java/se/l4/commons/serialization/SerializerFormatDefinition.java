package se.l4.commons.serialization;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;

import se.l4.commons.serialization.format.ValueType;
import se.l4.commons.serialization.internal.SerializerFormatDefinitionBuilderImpl;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Definition of the output of a {@link Serializer}.
 *
 * @author Andreas Holstenson
 *
 */
public class SerializerFormatDefinition
{
	private static final SerializerFormatDefinition ANY = new SerializerFormatDefinition(3, null, Collections.<FieldDefinition>emptyList());
	private static final SerializerFormatDefinition UNKNOWN = new SerializerFormatDefinition(4, null, Collections.<FieldDefinition>emptyList());
	private static final Map<ValueType, SerializerFormatDefinition> VALUES;

	static
	{
		Map<ValueType, SerializerFormatDefinition> values = new EnumMap<ValueType, SerializerFormatDefinition>(ValueType.class);
		for(ValueType vt : ValueType.values())
		{
			values.put(vt, new SerializerFormatDefinition(0, vt, Collections.<FieldDefinition>emptyList()));
		}

		VALUES = values;
	}

	private final int type;
	private final ValueType valueType;
	private final ImmutableMap<String, FieldDefinition> fields;

	public SerializerFormatDefinition(int type, ValueType valueType, Iterable<FieldDefinition> definitions)
	{
		MutableMap<String, FieldDefinition> builder = Maps.mutable.empty();
		for(FieldDefinition fd : definitions)
		{
			builder.put(fd.getName(), fd);
		}

		this.fields = builder.toImmutable();

		this.type = type;
		this.valueType = valueType;
	}

	public FieldDefinition getField(String fieldName)
	{
		return fields.get(fieldName);
	}

	public RichIterable<FieldDefinition> getFields()
	{
		return fields.valuesView();
	}

	public ValueType getValueType()
	{
		return valueType;
	}

	public boolean isList()
	{
		return type == 2;
	}

	public boolean isObject()
	{
		return type == 1;
	}

	public boolean isValue()
	{
		return type == 0;
	}

	public boolean isAny()
	{
		return type == 3;
	}

	public boolean isUnknown()
	{
		return type == 4;
	}

	public static Builder builder()
	{
		return new SerializerFormatDefinitionBuilderImpl();
	}

	public static SerializerFormatDefinition any()
	{
		return ANY;
	}

	public static SerializerFormatDefinition unknown()
	{
		return UNKNOWN;
	}

	public static SerializerFormatDefinition forValue(ValueType value)
	{
		return VALUES.get(value);
	}

	public static class FieldDefinition
	{
		private final String name;
		private final SerializerFormatDefinition definition;
		private final TypeRef type;
		private final Annotation[] hints;

		public FieldDefinition(String name, SerializerFormatDefinition definition, TypeRef type, Annotation[] hints)
		{
			this.name = name;
			this.definition = definition;
			this.type = type;
			this.hints = hints;
		}

		public String getName()
		{
			return name;
		}

		public SerializerFormatDefinition getDefinition()
		{
			return definition;
		}

		public TypeRef getType()
		{
			return type;
		}

		public Annotation[] getHints()
		{
			return hints;
		}
	}

	public interface Builder
	{
		Builder object();

		/**
		 * Start adding a field to this definition. This implies that the
		 * type will be {@link #object()}.
		 *
		 * @param name
		 * 		name of the field in its serialized form
		 * @return
		 * 		builder for the field
		 */
		FieldBuilder field(String name);

		/**
		 * Define that we represent a certain type of value.
		 *
		 * @param valueType
		 * @return
		 */
		Builder value(ValueType valueType);

		/**
		 * Define that we represent a list.
		 *
		 * @param itemDefinition
		 * 		the format definition that is used for fields in this list
		 * @return
		 */
		Builder list(SerializerFormatDefinition itemDefinition);

		/**
		 * Define that we represent a list.
		 *
		 * @param itemDefinition
		 * 		the format definition that is used for fields in this list
		 * @return
		 */
		Builder list(Serializer<?> itemSerializer);

		/**
		 * Build the definition.
		 *
		 * @return
		 */
		SerializerFormatDefinition build();
	}
	/**
	 * Builder for field definition for object.
	 *
	 * @author Andreas Holstenson
	 *
	 */
	public interface FieldBuilder
	{
		/**
		 * Add a hint to the definition.
		 *
		 * @param hint
		 * @return
		 */
		FieldBuilder withHint(Annotation hint);

		/**
		 * Add several hints to the definition.
		 *
		 * @param hints
		 * @return
		 */
		FieldBuilder withHints(Annotation... hints);

		/**
		 * Set information about the Java-type.
		 *
		 * @param type
		 * @return
		 */
		FieldBuilder withType(Class<?> type);

		/**
		 * Set information about the Java-type.
		 *
		 * @param type
		 * @return
		 */
		FieldBuilder withType(TypeRef type);

		/**
		 * Define that this field uses the specified serializer.
		 *
		 * @param serializer
		 * @return
		 */
		Builder using(Serializer<?> serializer);

		/**
		 * Define that this field uses the specified definition.
		 *
		 * @param def
		 * @return
		 */
		Builder using(SerializerFormatDefinition def);

		/**
		 * Using the definition for the specified value type.
		 *
		 * @param valueType
		 * @return
		 */
		Builder using(ValueType valueType);
	}
}
