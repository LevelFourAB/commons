package se.l4.commons.types.proxies;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.FieldValue;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class NonPublicByteBuddyRunner
{
	private final MethodInvocationHandler handler;

	public NonPublicByteBuddyRunner(MethodInvocationHandler handler)
	{
		this.handler = handler;
	}

	@RuntimeType
	public Object run(@FieldValue("context") Object context, @AllArguments Object[] arguments)
		throws Exception
	{
		return handler.handle(context, arguments);
	}
}
