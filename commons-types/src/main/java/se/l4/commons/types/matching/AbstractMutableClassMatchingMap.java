package se.l4.commons.types.matching;

import java.util.Objects;

import org.eclipse.collections.api.map.MutableMap;

public abstract class AbstractMutableClassMatchingMap<T, D>
	extends AbstractClassMatchingMap<T, D>
	implements MutableClassMatchingMap<T, D>
{
	protected final MutableMap<Class<? extends T>, D> backingMap;

	protected AbstractMutableClassMatchingMap(MutableMap<Class<? extends T>, D> backingMap)
	{
		super(backingMap);

		this.backingMap = backingMap;
	}

	@Override
	public void put(Class<? extends T> type, D data)
	{
		Objects.requireNonNull(type);
		Objects.requireNonNull(data);

		backingMap.put(type, data);
	}

	@Override
	public ClassMatchingMap<T, D> toImmutable()
	{
		return new ImmutableClassMatchingMap<>(backingMap.toImmutable());
	}
}
