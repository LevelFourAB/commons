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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultMatchedType other = (DefaultMatchedType) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
