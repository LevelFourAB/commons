package se.l4.commons.serialization.internal;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import se.l4.commons.serialization.Serializer;
import se.l4.commons.serialization.SerializerFormatDefinition;
import se.l4.commons.serialization.SerializerFormatDefinition.Builder;
import se.l4.commons.serialization.SerializerFormatDefinition.FieldBuilder;
import se.l4.commons.serialization.SerializerFormatDefinition.FieldDefinition;
import se.l4.commons.serialization.format.ValueType;
import se.l4.commons.types.Types;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Implementation of {@link SerializerDefinition.Builder}.
 *
 * @author Andreas Holstenson
 *
 */
public class SerializerFormatDefinitionBuilderImpl
	implements Builder
{
	private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];

	private final List<FieldDefinition> fields;
	private int type;
	private ValueType valueType;
	private SerializerFormatDefinition itemDefinition;

	public SerializerFormatDefinitionBuilderImpl()
	{
		fields = new ArrayList<FieldDefinition>();
		type = 1;
	}

	@Override
	public FieldBuilder field(String name)
	{
		return new FieldBuilderImpl(name);
	}

	@Override
	public Builder list(SerializerFormatDefinition itemDefinition)
	{
		type = 2;
		this.itemDefinition = itemDefinition;
		return this;
	}

	@Override
	public Builder list(Serializer<?> itemSerializer)
	{
		return list(itemSerializer.getFormatDefinition());
	}

	@Override
	public Builder object()
	{
		type = 1;
		return this;
	}

	@Override
	public Builder value(ValueType valueType)
	{
		type = 0;
		this.valueType = valueType;
		return this;
	}

	@Override
	public SerializerFormatDefinition build()
	{
		if(type == 0)
		{
			return SerializerFormatDefinition.forValue(valueType);
		}

		return new SerializerFormatDefinition(type, valueType, fields);
	}

	private class FieldBuilderImpl
		implements FieldBuilder
	{
		private final Collection<Annotation> hints;
		private String name;
		private TypeRef type;

		public FieldBuilderImpl(String name)
		{
			this.name = name;
			hints = new LinkedHashSet<Annotation>();
		}

		@Override
		public FieldBuilder withHint(Annotation hint)
		{
			hints.add(hint);
			return this;
		}

		@Override
		public FieldBuilder withHints(Annotation... hints)
		{
			for(Annotation a : hints)
			{
				this.hints.add(a);
			}
			return this;
		}

		@Override
		public FieldBuilder withType(Class<?> type)
		{
			return withType(Types.reference(type));
		}

		@Override
		public FieldBuilder withType(TypeRef type)
		{
			this.type = type;
			return this;
		}

		@Override
		public Builder using(Serializer<?> serializer)
		{
			return using(serializer.getFormatDefinition());
		}

		@Override
		public Builder using(ValueType valueType)
		{
			return using(SerializerFormatDefinition.forValue(valueType));
		}

		@Override
		public Builder using(SerializerFormatDefinition def)
		{
			fields.add(new SerializerFormatDefinition.FieldDefinition(
				name,
				def,
				type,
				hints.isEmpty() ? EMPTY_ANNOTATIONS : hints.toArray(EMPTY_ANNOTATIONS)
			));
			return SerializerFormatDefinitionBuilderImpl.this;
		}
	}
}
