package se.l4.commons.types.internal.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.AnnotatedWildcardType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

public class AnnotatedTypeEmulation
{
	public static final Annotation[] EMPTY = new Annotation[0];

	public static AnnotatedType annotate(Type type)
	{
		return annotate(type, EMPTY);
	}

	public static AnnotatedType annotate(Type type, Annotation[] annotations)
	{
		if(type instanceof Class<?>)
		{
			return new AnnotatedTypeImpl(type, annotations);
		}
		else if(type instanceof GenericArrayType)
		{
			Type genericComponent = ((GenericArrayType) type).getGenericComponentType();
			return new AnnotatedArrayTypeImpl(type, annotations, annotate(genericComponent));
		}
		else if(type instanceof ParameterizedType)
		{
			ParameterizedType pt = (ParameterizedType) type;
			return new AnnotatedParameterizedTypeImpl(
				type,
				annotations,
				Arrays.stream(pt.getActualTypeArguments())
					.map(AnnotatedTypeEmulation::annotate)
					.toArray(AnnotatedType[]::new)
			);
		}
		else if(type instanceof TypeVariable<?>)
		{
			TypeVariable<?> tv = (TypeVariable<?>) type;
			return new AnnotatedTypeVariableImpl(
				tv,
				annotations,
				Arrays.stream(tv.getBounds())
					.map(AnnotatedTypeEmulation::annotate)
					.toArray(AnnotatedType[]::new)
			);
		}
		else if(type instanceof WildcardType)
		{
			WildcardType wc = (WildcardType) type;
			return new AnnotatedWildcardTypeImpl(
				type,
				annotations,
				Arrays.stream(wc.getLowerBounds())
					.map(AnnotatedTypeEmulation::annotate)
					.toArray(AnnotatedType[]::new),
				Arrays.stream(wc.getUpperBounds())
					.map(AnnotatedTypeEmulation::annotate)
					.toArray(AnnotatedType[]::new)
			);
		}

		throw new IllegalArgumentException("Can not annotate type: " + type.getTypeName());
	}

	private static class AnnotatedTypeImpl
		implements AnnotatedType
	{
		private final Type type;
		private final Annotation[] annotations;

		public AnnotatedTypeImpl(Type type, Annotation[] annotations)
		{
			this.type = type;
			this.annotations = annotations;
		}

		@Override
		public Type getType()
		{
			return type;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T extends Annotation> T getAnnotation(Class<T> annotationClass)
		{
			for(Annotation a : annotations)
			{
				if(a.getClass().isAssignableFrom(annotationClass))
				{
					return (T) a;
				}
			}

			return null;
		}

		@Override
		public Annotation[] getAnnotations()
		{
			return Arrays.copyOf(annotations, annotations.length);
		}

		@Override
		public Annotation[] getDeclaredAnnotations()
		{
			return getAnnotations();
		}

		@Override
		public AnnotatedType getAnnotatedOwnerType()
		{
			return null;
		}
	}

	private static class AnnotatedArrayTypeImpl
		extends AnnotatedTypeImpl
		implements AnnotatedArrayType
	{
		private final AnnotatedType componentType;

		public AnnotatedArrayTypeImpl(
			Type type,
			Annotation[] annotations,
			AnnotatedType componentType
		)
		{
			super(type, annotations);

			this.componentType = componentType;
		}

		@Override
		public AnnotatedType getAnnotatedGenericComponentType()
		{
			return componentType;
		}
	}

	private static class AnnotatedParameterizedTypeImpl
		extends AnnotatedTypeImpl
		implements AnnotatedParameterizedType
	{
		private final AnnotatedType[] actualTypeArguments;

		public AnnotatedParameterizedTypeImpl(
			Type type,
			Annotation[] annotations,
			AnnotatedType[] actualTypeArguments
		)
		{
			super(type, annotations);

			this.actualTypeArguments = actualTypeArguments;
		}

		@Override
		public AnnotatedType[] getAnnotatedActualTypeArguments()
		{
			return actualTypeArguments;
		}
	}

	private static class AnnotatedTypeVariableImpl
		extends AnnotatedTypeImpl
		implements AnnotatedTypeVariable
	{
		private final AnnotatedType[] bounds;

		public AnnotatedTypeVariableImpl(
			Type type,
			Annotation[] annotations,
			AnnotatedType[] bounds
		)
		{
			super(type, annotations);

			this.bounds = bounds;
		}

		@Override
		public AnnotatedType[] getAnnotatedBounds()
		{
			return bounds;
		}
	}

	private static class AnnotatedWildcardTypeImpl
		extends AnnotatedTypeImpl
		implements AnnotatedWildcardType
	{
		private final AnnotatedType[] lowerBounds;
		private final AnnotatedType[] upperBounds;

		public AnnotatedWildcardTypeImpl(
			Type type,
			Annotation[] annotations,
			AnnotatedType[] lowerBounds,
			AnnotatedType[] upperBounds
		)
		{
			super(type, annotations);

			this.lowerBounds = lowerBounds;
			this.upperBounds = upperBounds;
		}

		@Override
		public AnnotatedType[] getAnnotatedLowerBounds()
		{
			return lowerBounds;
		}

		@Override
		public AnnotatedType[] getAnnotatedUpperBounds()
		{
			return upperBounds;
		}
	}
}
