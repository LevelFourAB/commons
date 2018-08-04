package se.l4.commons.types.proxies;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Optional;
import java.util.function.Function;

import org.junit.Test;

import se.l4.commons.types.internal.ExtendedTypeBuilderImpl;

public class ExtendedTypeBuilderTest
{
	private <CT> ExtendedTypeBuilder<CT> createBuilder(Class<CT> ctx)
	{
		return new ExtendedTypeBuilderImpl<>(ctx);
	}

	@Test
	public void testPrivateInterface()
	{
		try
		{
			Function<String, PrivateInterface> c = createBuilder(String.class)
				.with(encounter -> Optional.of((ctx, args) -> ctx))
				.create(PrivateInterface.class);
		}
		catch(ProxyException e)
		{
			return;
		}

		fail("Possible to implement private interface");
	}

	@Test
	public void testPublicInterface()
	{
		Function<String, PublicInterface> c = createBuilder(String.class)
			.with(encounter -> Optional.of((ctx, args) -> ctx))
			.create(PublicInterface.class);

		assertThat("function is created", c, notNullValue());

		String contextIn = "Hello World";

		PublicInterface obj = c.apply(contextIn);
		assertThat("object is created", obj, notNullValue());

		String contextOut = obj.returnContext();
		assertThat("returnContext echoes context", contextOut, is(contextIn));
	}

	@Test
	public void testNoInvokerThrowsProxyException()
	{
		try
		{
			createBuilder(String.class)
				.create(PublicInterface.class);
		}
		catch(ProxyException e)
		{
			return;
		}

		fail("Should have failed with ProxyException");
	}

	private interface PrivateInterface
	{
		String returnContext();
	}

	public interface PublicInterface
	{
		String returnContext();
	}
}
