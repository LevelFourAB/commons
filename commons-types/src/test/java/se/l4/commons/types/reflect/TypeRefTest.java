package se.l4.commons.types.reflect;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import se.l4.commons.types.Types;
import se.l4.commons.types.internal.reflect.TypeHelperImpl;

public class TypeRefTest
{
	@Test
	public void testListUnresolved()
	{
		TypeRef ref = TypeHelperImpl.reference(List.class);
		assertThat(ref.isResolved(), is(true));
		assertThat(ref.isFullyResolved(), is(false));

		assertThat(ref.getErasedType(), is((Object) List.class));

		Optional<TypeRef> elementType = ref.getTypeParameter(0);
		assertThat(elementType.isPresent(), is(true));

		TypeRef elementTypeRef = elementType.get();
		assertThat(elementTypeRef.isResolved(), is(false));
	}

	@Test
	public void testListResolved()
	{
		TypeRef ref = TypeHelperImpl.reference(List.class, String.class);
		assertThat(ref.isResolved(), is(true));
		assertThat(ref.isFullyResolved(), is(true));

		assertThat(ref.getErasedType(), is((Object) List.class));

		Optional<TypeRef> elementType = ref.getTypeParameter(0);
		assertThat(elementType.isPresent(), is(true));

		TypeRef elementTypeRef = elementType.get();
		assertThat(elementTypeRef.isResolved(), is(true));
		assertThat(elementTypeRef.getErasedType(), is((Object) String.class));
	}

	@Test
	public void testArrayListUnresolved()
	{
		TypeRef ref = TypeHelperImpl.reference(ArrayList.class);
		assertThat(ref.isResolved(), is(true));
		assertThat(ref.isFullyResolved(), is(false));

		assertThat(ref.getErasedType(), is((Object) ArrayList.class));

		Optional<TypeRef> elementType = ref.getTypeParameter(0);
		assertThat(elementType.isPresent(), is(true));

		// Check that the element type is not resolved
		TypeRef elementTypeRef = elementType.get();
		assertThat(elementTypeRef.isResolved(), is(false));
		assertThat(elementTypeRef.getErasedType(), is((Object) Object.class));
	}

	@Test
	public void testArrayListResolved()
	{
		TypeRef ref = TypeHelperImpl.reference(ArrayList.class, String.class);
		assertThat(ref.isResolved(), is(true));
		assertThat(ref.isFullyResolved(), is(true));

		assertThat(ref.getErasedType(), is((Object) ArrayList.class));

		Optional<TypeRef> elementType = ref.getTypeParameter(0);
		assertThat(elementType.isPresent(), is(true));

		// Check that the element type is resolved
		TypeRef elementTypeRef = elementType.get();
		assertThat(elementTypeRef.isResolved(), is(true));
		assertThat(elementTypeRef.getErasedType(), is((Object) String.class));

		// Get the List interface
		Optional<TypeRef> listType = ref.getInterface(List.class);
		assertThat(elementType.isPresent(), is(true));

		TypeRef listTypeRef = listType.get();

		assertThat(listTypeRef.isFullyResolved(), is(true));
		assertThat(listTypeRef.getTypeParameter(0).get().getErasedType(), is((Object) String.class));
	}

	@Test
	public void testArrayListSubclassWithTypeParameter()
	{
		class ExtendedArrayList<T> extends ArrayList<T> {
		}

		TypeRef ref = TypeHelperImpl.reference(ExtendedArrayList.class, String.class);
		assertThat(ref.isResolved(), is(true));
		assertThat(ref.isFullyResolved(), is(true));

		assertThat(ref.getErasedType(), is((Object) ExtendedArrayList.class));

		Optional<TypeRef> elementType = ref.getTypeParameter(0);
		assertThat(elementType.isPresent(), is(true));

		// Check that the element type is resolved
		TypeRef elementTypeRef = elementType.get();
		assertThat(elementTypeRef.isResolved(), is(true));
		assertThat(elementTypeRef.getErasedType(), is((Object) String.class));

		// Get the super class
		TypeRef superclass = ref.getSuperclass().get();
		assertThat(superclass.isFullyResolved(), is(true));
		assertThat(superclass.getTypeParameter(0).get().getErasedType(), is((Object) String.class));
	}

	@Test
	public void testArrayListSubclassWithoutTypeParameter()
	{
		class ExtendedArrayList extends ArrayList<String> {
		}

		TypeRef ref = TypeHelperImpl.reference(ExtendedArrayList.class);
		assertThat(ref.isResolved(), is(true));
		assertThat(ref.isFullyResolved(), is(true));

		assertThat(ref.getErasedType(), is((Object) ExtendedArrayList.class));

		// Get the super class
		TypeRef superclass = ref.getSuperclass().get();
		assertThat(superclass.isFullyResolved(), is(true));
		assertThat(superclass.getTypeParameter(0).get().getErasedType(), is((Object) String.class));

		// Find the List directly from the top ref
		TypeRef list = ref.findInterface(List.class).get();
		assertThat(list.isFullyResolved(), is(true));
		assertThat(list.getTypeParameter(0).get().getErasedType(), is((Object) String.class));
	}

	@Test
	public void testSuperclassParameterUsage()
	{
		class ExtendedArrayList extends ArrayList<@NonNull String> {
		}

		TypeRef ref = TypeHelperImpl.reference(ExtendedArrayList.class);

		// Get the super class
		TypeRef superclass = ref.getSuperclass().get();
		assertThat(superclass.isFullyResolved(), is(true));

		TypeRef p1 = superclass.getTypeParameter(0).get();
		Optional<NonNull> o1 = p1.getUsage().getAnnotation(NonNull.class);
		assertThat(o1.isPresent(), is(true));

		// Find the List directly from the top ref
		TypeRef list = ref.findInterface(List.class).get();

		TypeRef p2 = list.getTypeParameter(0).get();
		Optional<NonNull> o2 = p2.getUsage().getAnnotation(NonNull.class);
		assertThat(o2.isPresent(), is(true));
	}

	@Test
	public void testTypeParameterSuperclassParameterUsage()
	{
		class ExtendedArrayList<T> extends ArrayList<@NonNull T> {
		}

		TypeRef ref = TypeHelperImpl.reference(ExtendedArrayList.class);

		// Get the super class
		TypeRef superclass = ref.getSuperclass().get();

		TypeRef p1 = superclass.getTypeParameter(0).get();
		Optional<NonNull> o1 = p1.getUsage().getAnnotation(NonNull.class);
		assertThat("First ArrayList param should contain @NonNull", o1.isPresent(), is(true));

		// Find the List directly from the top ref
		TypeRef list = ref.findInterface(List.class).get();

		TypeRef p2 = list.getTypeParameter(0).get();
		Optional<NonNull> o2 = p2.getUsage().getAnnotation(NonNull.class);
		assertThat("First List param should contain @NonNull", o2.isPresent(), is(true));
	}

	@Test
	public void testAssignable()
	{
		assertThat(Types.reference(List.class).isAssignableFrom(Types.reference(ArrayList.class)), is(true));
		assertThat(Types.reference(ArrayList.class).isAssignableFrom(Types.reference(List.class)), is(false));

		assertThat(
			Types.reference(List.class, Object.class)
				.isAssignableFrom(Types.reference(List.class, String.class)),

			is(true)
		);

		assertThat(
			Types.reference(List.class, String.class)
				.isAssignableFrom(Types.reference(List.class, Object.class)),

			is(false)
		);

		assertThat(
			Types.reference(List.class, String.class)
				.isAssignableFrom(Types.reference(ArrayList.class, String.class)),

			is(true)
		);

		assertThat(
			Types.reference(List.class, Object.class)
				.isAssignableFrom(Types.reference(ArrayList.class, String.class)),

			is(true)
		);
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface NonNull
	{
	}
}
