package se.l4.commons.types.matching;

/**
 * Default implementation of {@link MatchedType}.
 */
public class DefaultMatchedType<T, D>
	implements MatchedType<T, D>
{
	private final Class<? extends T> type;
	private final D data;

	public DefaultMatchedType(Class<? extends T> type, D data)
	{
		this.type = type;
		this.data = data;
	}

	@Override
	public Class<? extends T> getType()
	{
		return type;
	}

	@Override
	public D getData()
	{
		return data;
	}

	@Override
	public String toString()
	{
		return "DefaultMatchedType{" + type + " => " + data + "}";
	}
}
