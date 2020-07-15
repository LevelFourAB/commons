package se.l4.commons.types.matching;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.impl.list.mutable.FastList;

import edu.umd.cs.findbugs.annotations.Nullable;
import se.l4.commons.types.internal.TypeHierarchy;

/**
 * Abstract implementation of {@link ClassMatchingMap} that implements all of
 * the matching methods on top any {@link MapIterable} implementation.
 */
public abstract class AbstractClassMatchingMap<T, D>
	implements ClassMatchingMap<T, D>
{
	protected final MapIterable<Class<? extends T>, D> backingMap;

	protected AbstractClassMatchingMap(MapIterable<Class<? extends T>, D> backingMap)
	{
		this.backingMap = Objects.requireNonNull(backingMap);
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public RichIterable<MatchedType<T, D>> entries()
	{
		return backingMap.keyValuesView()
			.<MatchedType<T, D>>collect((e) -> new DefaultMatchedType<T, D>((Class) e.getOne(), e.getTwo()));
	}

	@Override
	public Optional<D> get(Class<? extends T> type)
	{
		Objects.requireNonNull(type);

		return Optional.ofNullable(backingMap.get(type));
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
	public ImmutableList<MatchedType<T, D>> getAll(Class<? extends T> type)
	{
		Objects.requireNonNull(type);

		MutableList<MatchedType<T, D>> result = FastList.newList();
		findMatching(type, (t, d) -> {
			result.add(new DefaultMatchedType<>(t, d));

			// Always continue
			return true;
		});

		return result.toImmutable();
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
