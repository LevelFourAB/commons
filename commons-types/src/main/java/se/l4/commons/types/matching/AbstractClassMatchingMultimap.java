package se.l4.commons.types.matching;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.multimap.list.ListMultimap;
import org.eclipse.collections.impl.list.mutable.FastList;

import se.l4.commons.types.internal.TypeHierarchy;

/**
 * Abstract implementation of {@link ClassMatchingMap} that implements all of
 * the matching methods on top any {@link Map} implementation.
 */
public abstract class AbstractClassMatchingMultimap<T, D>
	implements ClassMatchingMultimap<T, D>
{
	protected final ListMultimap<Class<? extends T>, D> backingMap;

	protected AbstractClassMatchingMultimap(ListMultimap<Class<? extends T>, D> backingMap)
	{
		this.backingMap = backingMap;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public RichIterable<MatchedType<T, D>> entries()
	{
		return backingMap.keyValuePairsView()
			.<MatchedType<T, D>>collect((e) -> new DefaultMatchedType<T, D>((Class) e.getOne(), e.getTwo()));
	}

	@Override
	public ListIterable<D> get(Class<? extends T> type)
	{
		Objects.requireNonNull(type);

		return backingMap.get(type);
	}

	@Override
	public ListIterable<D> getBest(Class<? extends T> type)
	{
		Objects.requireNonNull(type);

		FastList<D> result = FastList.newList();
		findMatching(type, (t, d) -> {
			result.addAllIterable(d);

			return false;
		});

		return result;
	}

	@Override
	public ListIterable<MatchedType<T, D>> getAll(Class<? extends T> type)
	{
		Objects.requireNonNull(type);

		FastList<MatchedType<T, D>> result = FastList.newList();
		findMatching(type, (t, all) -> {
			for(D d : all)
			{
				result.add(new DefaultMatchedType<>(t, d));
			}

			// Always continue
			return true;
		});

		return result;
	}

	/**
	 * Perform matching against the given type. This will go through the
	 * hierarchy and interfaces of the type trying to find if this map has
	 * an entry for them.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void findMatching(Class<? extends T> type, BiPredicate<Class<? extends T>, ListIterable<D>> predicate)
	{
		TypeHierarchy.visitHierarchy(type, t -> {
			if(backingMap.containsKey(t))
			{
				ListIterable<D> data = backingMap.get((Class) t);
				return predicate.test((Class) t, data);
			}

			return true;
		});
	}

}
