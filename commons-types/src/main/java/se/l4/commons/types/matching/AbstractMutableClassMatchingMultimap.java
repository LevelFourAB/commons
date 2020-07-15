package se.l4.commons.types.matching;

import java.util.Objects;

import org.eclipse.collections.api.multimap.list.MutableListMultimap;

/**
 * Abstract implementation for {@link MutableClassMatchingMap}.
 *
 * @param <T>
 * @param <D>
 */
public abstract class AbstractMutableClassMatchingMultimap<T, D>
	extends AbstractClassMatchingMultimap<T, D>
	implements MutableClassMatchingMultimap<T, D>
{
	protected final MutableListMultimap<Class<? extends T>, D> backingMap;

	protected AbstractMutableClassMatchingMultimap(MutableListMultimap<Class<? extends T>, D> backingMap)
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
	public ClassMatchingMultimap<T, D> toImmutable()
	{
		return new ImmutableClassMatchingMultimap<>(backingMap.toImmutable());
	}
}
