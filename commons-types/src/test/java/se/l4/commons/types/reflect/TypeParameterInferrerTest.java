package se.l4.commons.types.reflect;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

import se.l4.commons.types.Types;
import se.l4.commons.types.internal.reflect.TypeParameterInferrer;

/**
 * Tests for {@link TypeParameterInferrer}.
 */
public class TypeParameterInferrerTest
{
	@Test
	public void testDirect()
	{
		class A<T> implements WithParameter<T> {
		}

		TypeRef type = Types.reference(A.class);
		TypeRef parameter = type.getInterface(WithParameter.class).get();
		TypeRef patternType = parameter.getTypeParameter(0).get();

		TypeInferrer inferrer = type.getTypeParameterInferrer(patternType);

		Optional<TypeRef> result = inferrer.infer(Types.reference(String.class));

		assertThat("Result must be present", result.isPresent(), is(true));
		assertThat(result.get().getType(), is((Object) String.class));
	}

	@Test
	public void testIndirect()
	{
		class A<T> implements WithParameter<List<T>> {
		}

		TypeRef type = Types.reference(A.class);
		TypeRef parameter = type.getInterface(WithParameter.class).get();
		TypeRef patternType = parameter.getTypeParameter(0).get();

		TypeInferrer inferrer = type.getTypeParameterInferrer(patternType);

		Optional<TypeRef> result = inferrer.infer(Types.reference(String.class));

		assertThat("Result must be present", result.isPresent(), is(true));
		assertThat(result.get().getErasedType(), is((Object) List.class));

		assertThat(result.get().getTypeParameter(0).get(), is(Types.reference(String.class)));
	}

	interface WithParameter<A>
	{
	}

	interface WithParameters<A, B>
	{
	}
}
