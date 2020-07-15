package se.l4.commons.types.matching;

import java.util.Objects;

import org.eclipse.collections.api.multimap.set.MutableSetMultimap;

import se.l4.commons.types.reflect.TypeRef;

public class AbstractMutableTypeMatchingMap<D>
	extends AbstractTypeMatchingMap<D>
	implements MutableTypeMatchingMap<D>
{
	protected final MutableSetMultimap<Class<?>, TypeRefHolder<D>> backingMap;

	protected AbstractMutableTypeMatchingMap(MutableSetMultimap<Class<?>, TypeRefHolder<D>> backingMap)
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
}
