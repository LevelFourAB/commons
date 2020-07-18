package se.l4.commons.types.matching;

import java.util.Objects;

import org.eclipse.collections.api.multimap.list.MutableListMultimap;

import se.l4.commons.types.reflect.TypeRef;

public abstract class AbstractMutableTypeMatchingMultimap<D>
	extends AbstractTypeMatchingMultimap<D>
	implements MutableTypeMatchingMultimap<D>
{
	protected final MutableListMultimap<Class<?>, TypeRefHolder<D>> backingMap;

	public AbstractMutableTypeMatchingMultimap(
		MutableListMultimap<Class<?>, TypeRefHolder<D>> backingMap
	)
	{
		super(backingMap);

		this.backingMap = backingMap;
	}

	@Override
	public void put(TypeRef type, D data)
	{
		Objects.requireNonNull(type);
		Objects.requireNonNull(data);

		backingMap.put(type.getErasedType(), new TypeRefHolder<>(type, data));
	}

	@Override
	public TypeMatchingMultimap<D> toImmutable()
	{
		return new ImmutableTypeMatchingMultimap<>(backingMap.toImmutable());
	}
}
