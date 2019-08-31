package se.l4.commons.types.matching;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.SetMultimap;

import se.l4.commons.types.internal.TypeHierarchy;

/**
 * Abstract implementation of {@link ClassMatchingMap} that implements all of
 * the matching methods on top any {@link Map} implementation.
 */
public abstract class AbstractClassMatchingMultimap<T, D>
	implements ClassMatchingMultimap<T, D>
{
	private final SetMultimap<Class<? extends T>, D> backingMap;

	protected AbstractClassMatchingMultimap(SetMultimap<Class<? extends T>, D> backingMap)
	{
		this.backingMap = backingMap;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<MatchedType<T, D>> entries()
	{
		return backingMap.entries()
			.stream()
			.map(e -> new DefaultMatchedType<T, D>((Class) e.getKey(), e.getValue()))
			.collect(ImmutableList.toImmutableList());
	}

	@Override
	public void put(Class<? extends T> type, D data)
	{
		Objects.requireNonNull(type);
		Objects.requireNonNull(data);

		backingMap.put(type, data);
	}

	@Override
	public Set<D> get(Class<? extends T> type)
	{
		Objects.requireNonNull(type);

		return backingMap.get(type);
	}

	@Override
	public Set<D> getBest(Class<? extends T> type)
	{
		Objects.requireNonNull(type);

		Set<D> result = new HashSet<>();
		findMatching(type, (t, d) -> {
			result.addAll(d);

			return false;
		});

		return result;
	}

	@Override
	public List<MatchedType<T, D>> getAll(Class<? extends T> type)
	{
		Objects.requireNonNull(type);

		List<MatchedType<T, D>> result = new ArrayList<>();
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
	protected void findMatching(Class<? extends T> type, BiPredicate<Class<? extends T>, Set<D>> predicate)
	{
		TypeHierarchy.visitHierarchy(type, t -> {
			if(backingMap.containsKey(t))
			{
				Set<D> data = backingMap.get((Class) t);
				return predicate.test((Class) t, data);
			}

			return true;
		});
	}

}
