package se.l4.commons.types.proxies;

import se.l4.commons.types.InstanceFactory;
import se.l4.commons.types.internal.ExtendedTypeBuilderImpl;

/**
 * Default implementation of {@link Proxies}.
 */
public class DefaultProxies
	implements Proxies
{
	private final InstanceFactory instanceFactory;

	public DefaultProxies(InstanceFactory instanceFactory)
	{
		this.instanceFactory = instanceFactory;
	}

	@Override
	public <ContextType> ExtendedTypeBuilder<ContextType> newExtendedType(Class<ContextType> type)
	{
		return new ExtendedTypeBuilderImpl<>(type);
	}

}
