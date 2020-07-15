package se.l4.commons.types.matching;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.multimap.set.SetMultimap;
import org.eclipse.collections.api.set.SetIterable;

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
	private final SetMultimap<Class<?>, TypeRefHolder<D>> backingMap;

	protected AbstractTypeMatchingMap(
		SetMultimap<Class<?>, TypeRefHolder<D>> backingMap
	)
	{
		this.backingMap = Objects.requireNonNull(backingMap);
	}

	@Override
	public RichIterable<MatchedTypeRef<D>> entries()
	{
		return backingMap.keyValuePairsView()
			.<MatchedTypeRef<D>>collect((e) -> new DefaultMatchedTypeRef<D>(e.getTwo().ref, e.getTwo().data));
	}

	@Override
	public Optional<D> get(TypeRef type)
	{
		Objects.requireNonNull(type);

		TypeRefHolder<D> value = backingMap.get(type.getErasedType())
			.detect(p -> p.ref.isAssignableFrom(type));

		if(value == null)
		{
			return Optional.empty();
		}

		return Optional.of(value.data);
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
	public ListIterable<MatchedTypeRef<D>> getAll(TypeRef type)
	{
		Objects.requireNonNull(type);

		MutableList<MatchedTypeRef<D>> result = Lists.mutable.empty();
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
			SetIterable<TypeRefHolder<D>> types = backingMap.get(t.getErasedType());
			for(TypeRefHolder<D> e : types)
			{
				if(e.ref.isAssignableFrom(t))
				{
					boolean c = predicate.test(e.ref, e.data);
					if(! c) return false;
				}
			}

			return true;
		});
	}

	public static class TypeRefHolder<D>
	{
		public final TypeRef ref;
		public final D data;

		public TypeRefHolder(TypeRef ref, D data)
		{
			this.ref = ref;
			this.data = data;
		}

		@Override
		public int hashCode()
		{
			return ref.hashCode();
		}

		@Override
		public boolean equals(Object obj)
		{
			return ref.equals(((TypeRefHolder<?>) obj).ref);
		}
	}

	private static class MutableHolder
	{
		@Nullable
		private Object data;
	}
}
