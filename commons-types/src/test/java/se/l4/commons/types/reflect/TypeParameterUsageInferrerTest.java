package se.l4.commons.types.reflect;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import se.l4.commons.types.Types;

/**
 * Test for {@link se.l4.commons.types.internal.reflect.TypeParameterUsageInferrer}.
 */
public class TypeParameterUsageInferrerTest
{
	@Test
	public void testDirectTypeInInterface()
	{
		class A<T> implements WithParameter<T> {
		}

		TypeRef type = Types.reference(A.class);
		TypeRef parameter = type.getInterface(WithParameter.class).get();
		TypeRef pattern = parameter.getTypeParameter(0).get();

		TypeInferrer inferrer = type.getTypeParameterUsageInferrer(0, pattern);

		Optional<TypeRef> result = inferrer.infer(Types.reference(String.class));

		assertThat("Result must be present", result.isPresent(), is(true));
		assertThat(result.get().getType(), is((Object) String.class));
	}

	@Test
	public void testIndirectTypeInInterfaceViaManualReference()
	{
		class A<T> implements WithParameter<List<T>> {
		}

		TypeRef type = Types.reference(A.class);
		TypeRef parameter = type.getInterface(WithParameter.class).get();
		TypeRef pattern = parameter.getTypeParameter(0).get();

		TypeInferrer inferrer = type.getTypeParameterUsageInferrer(0, pattern);

		Optional<TypeRef> result = inferrer.infer(Types.reference(List.class, String.class));

		assertThat("Result must be present", result.isPresent(), is(true));
		assertThat(result.get().getType(), is((Object) String.class));
	}

	@Test
	public void testIndirectTypeInInterfaceViaType()
	{
		class A<T> implements WithParameter<List<T>> {
		}

		class B extends ArrayList<String> {
		}

		TypeRef type = Types.reference(A.class);
		TypeRef parameter = type.getInterface(WithParameter.class).get();
		TypeRef pattern = parameter.getTypeParameter(0).get();

		TypeInferrer inferrer = type.getTypeParameterUsageInferrer(0, pattern);

		Optional<TypeRef> result = inferrer.infer(Types.reference(B.class));

		assertThat("Result must be present", result.isPresent(), is(true));
		assertThat(result.get().getType(), is((Object) String.class));
	}

	interface WithParameter<A>
	{
	}
}
