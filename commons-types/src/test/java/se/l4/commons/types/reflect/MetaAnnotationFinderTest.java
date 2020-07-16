package se.l4.commons.types.reflect;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Optional;

import org.junit.Test;

import se.l4.commons.types.Types;
import se.l4.commons.types.internal.reflect.MetaAnnotationLocator;

public class MetaAnnotationFinderTest
{
	@Test
	public void testDirect()
	{
		@Meta
		class A {}

		TypeRef type = Types.reference(A.class);
		AnnotationLocator<Meta> locator = new MetaAnnotationLocator<>(Meta.class);
		Optional<Meta> meta = type.getAnnotation(locator);

		assertThat(meta.isPresent(), is(true));
		assertThat(meta.get(), instanceOf(Meta.class));
	}

	@Test
	public void testAnnotated()
	{
		@WithMeta
		class A {}

		TypeRef type = Types.reference(A.class);
		AnnotationLocator<Meta> locator = new MetaAnnotationLocator<>(Meta.class);
		Optional<Meta> meta = type.getAnnotation(locator);

		assertThat(meta.isPresent(), is(true));
		assertThat(meta.get(), instanceOf(Meta.class));
	}

	@Test
	public void testAnnotated2()
	{
		@WithIndirectMeta
		class A {}

		TypeRef type = Types.reference(A.class);
		AnnotationLocator<Meta> locator = new MetaAnnotationLocator<>(Meta.class);
		Optional<Meta> meta = type.getAnnotation(locator);

		assertThat(meta.isPresent(), is(true));
		assertThat(meta.get(), instanceOf(Meta.class));
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Meta
	{
	}

	@Meta
	@Retention(RetentionPolicy.RUNTIME)
	public @interface WithMeta
	{
	}

	@Retention(RetentionPolicy.RUNTIME)
	@WithMeta
	public @interface WithIndirectMeta
	{
	}
}
