package se.l4.commons.types.matching;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import edu.umd.cs.findbugs.annotations.Nullable;
import se.l4.commons.types.internal.TypeHierarchy;

/**
 * Abstract implementation of {@link ClassMatchingMap} that implements all of
 * the matching methods on top any {@link Map} implementation.
 */
public abstract class AbstractClassMatchingMap<T, D>
	implements ClassMatchingMap<T, D>
{
	private final Map<Class<? extends T>, D> backingMap;

	protected AbstractClassMatchingMap(Map<Class<? extends T>, D> backingMap)
	{
		this.backingMap = Objects.requireNonNull(backingMap);
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<MatchedType<T, D>> entries()
	{
		return backingMap.entrySet()
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
	public Optional<D> get(Class<? extends T> type)
	{
		Objects.requireNonNull(type);

		return Optional.ofNullable(backingMap.get(type));
	}

	@Override
	public Optional<D> get(Class<? extends T> type, Function<Class<? extends T>, D> creator)
	{
		Objects.requireNonNull(type);

		return Optional.ofNullable(backingMap.computeIfAbsent(type, creator));
	}

	@Override
	@SuppressWarnings("unchecked")
	public Optional<D> getBest(Class<? extends T> type)
	{
		Objects.requireNonNull(type);

		MutableHolder holder = new MutableHolder();
		findMatching(type, (t, d) -> {
			holder.data = d;

			return false;
		});

		return Optional.ofNullable((D) holder.data);
	}

	@Override
	public List<MatchedType<T, D>> getAll(Class<? extends T> type)
	{
		Objects.requireNonNull(type);

		List<MatchedType<T, D>> result = new ArrayList<>();
		findMatching(type, (t, d) -> {
			result.add(new DefaultMatchedType<>(t, d));

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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void findMatching(Class<? extends T> type, BiPredicate<Class<? extends T>, D> predicate)
	{
		TypeHierarchy.visitHierarchy(type, t -> {
			if(backingMap.containsKey(t))
			{
				D data = backingMap.get(t);
				return predicate.test((Class) t, data);
			}

			return true;
		});
	}

	private static class MutableHolder
	{
		@Nullable
		private Object data;
	}
}
