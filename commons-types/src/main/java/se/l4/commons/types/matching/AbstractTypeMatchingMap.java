package se.l4.commons.types.matching;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;

import edu.umd.cs.findbugs.annotations.Nullable;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Abstract implementation of {@link TypeMatchingMap}.
 *
 * @param <D>
 */
public abstract class AbstractTypeMatchingMap<D>
	implements TypeMatchingMap<D>
{
	private final Map<Class<?>, Map<TypeRef, D>> backingMap;
	private final Supplier<Map<TypeRef, D>> subMapSupplier;

	protected AbstractTypeMatchingMap(
		Map<Class<?>, Map<TypeRef, D>> backingMap,
		Supplier<Map<TypeRef, D>> subMapSupplier
	)
	{
		this.backingMap = Objects.requireNonNull(backingMap);
		this.subMapSupplier = subMapSupplier;
	}

	@Override
	public List<MatchedTypeRef<D>> entries()
	{
		ImmutableList.Builder<MatchedTypeRef<D>> builder = ImmutableList.builder();

		for(Map<TypeRef, D> map : backingMap.values())
		{
			for(Map.Entry<TypeRef, D> e : map.entrySet())
			{
				builder.add(new DefaultMatchedTypeRef<>(e.getKey(), e.getValue()));
			}
		}

		return builder.build();
	}

	@Override
	public void put(TypeRef type, D data)
	{
		Objects.requireNonNull(type);
		Objects.requireNonNull(data);

		Map<TypeRef, D> types = backingMap.computeIfAbsent(type.getErasedType(), t -> subMapSupplier.get());
		types.put(type, data);
	}

	@Override
	public Optional<D> get(TypeRef type)
	{
		Objects.requireNonNull(type);

		Map<TypeRef, D> types = backingMap.get(type.getErasedType());
		if(types == null)
		{
			return Optional.empty();
		}

		return Optional.ofNullable(types.get(type));
	}

	@Override
	public Optional<D> get(TypeRef type, Function<TypeRef, D> creator)
	{
		Objects.requireNonNull(type);

		Map<TypeRef, D> types = backingMap.computeIfAbsent(type.getErasedType(), t -> subMapSupplier.get());
		return Optional.ofNullable(types.computeIfAbsent(type, creator));
	}

	@Override
	@SuppressWarnings("unchecked")
	public Optional<D> getBest(TypeRef type)
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
	public List<MatchedTypeRef< D>> getAll(TypeRef type)
	{
		Objects.requireNonNull(type);

		List<MatchedTypeRef<D>> result = new ArrayList<>();
		findMatching(type, (t, d) -> {
			result.add(new DefaultMatchedTypeRef<>(t, d));

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
	protected void findMatching(TypeRef type, BiPredicate<TypeRef, D> predicate)
	{
		type.visitHierarchy(t -> {
			Map<TypeRef, D> types = backingMap.get(t.getErasedType());
			if(types != null)
			{
				for(Map.Entry<TypeRef, D> e : types.entrySet())
				{
					if(e.getKey().isAssignableFrom(t))
					{
						return predicate.test(e.getKey(), e.getValue());
					}
				}
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
