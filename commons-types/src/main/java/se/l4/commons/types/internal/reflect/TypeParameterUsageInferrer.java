package se.l4.commons.types.internal.reflect;

import java.lang.reflect.TypeVariable;
import java.util.Optional;

import org.eclipse.collections.api.list.ListIterable;

import se.l4.commons.types.reflect.TypeInferrer;
import se.l4.commons.types.reflect.TypeRef;

/**
 * {@link TypeInferrer} that can infer how to a type variable is used in a
 * concrete type.
 */
public class TypeParameterUsageInferrer
	implements TypeInferrer
{
	private final TypeVariable<?> variable;
	private final TypeRef patternType;

	public TypeParameterUsageInferrer(
		TypeVariable<?> variable,
		TypeRef patternType
	)
	{
		this.variable = variable;
		this.patternType = patternType;
	}

	@Override
	public Optional<TypeRef> infer(TypeRef... types)
	{
		if(types.length != 1)
		{
			return Optional.empty();
		}

		TypeRef concrete = types[0];
		if(patternType.getErasedType() == concrete.getErasedType())
		{
			// If it's exactly the same type, run a match directly
			return match(patternType, concrete);
		}

		if(! patternType.isAssignableFrom(concrete))
		{
			// Not assignable, not going to try to infer anything
			return Optional.empty();
		}

		if(patternType.getType() instanceof TypeVariable<?>)
		{
			/*
			 * Handle the top level case, where the pattern is actually the
			 * type variable.
			 */
			String name = ((TypeVariable<?>) patternType.getType()).getName();
			if(variable.getName().equals(name))
			{
				return Optional.of(concrete);
			}
			else
			{
				return Optional.empty();
			}
		}

		// Find the superclass or interface and try to find our variable in it
		return concrete.findSuperclassOrInterface(patternType.getErasedType())
			.flatMap(c -> match(patternType, c));
	}

	private Optional<TypeRef> match(TypeRef pattern, TypeRef concrete)
	{
		if(pattern.getType() instanceof TypeVariable<?>)
		{
			String name = ((TypeVariable<?>) pattern.getType()).getName();
			if(variable.getName().equals(name))
			{
				return Optional.of(concrete);
			}
		}

		ListIterable<TypeRef> patternParameters = pattern.getTypeParameters();
		ListIterable<TypeRef> concreteParameters = concrete.getTypeParameters();
		if(patternParameters.size() != concreteParameters.size())
		{
			return Optional.empty();
		}

		for(int i=0, n=patternParameters.size(); i<n; i++)
		{
			Optional<TypeRef> match = match(
				patternParameters.get(i),
				concreteParameters.get(i)
			);

			if(match.isPresent())
			{
				return match;
			}
		}

		return Optional.empty();
	}
}
