package se.l4.commons.types.matching;

import java.util.Objects;
import java.util.function.BiPredicate;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.multimap.list.ListMultimap;

import se.l4.commons.types.reflect.TypeRef;

/**
 * Abstract implementation of {@link TypeMatchingMultimap}.
 *
 * @param <D>
 */
public abstract class AbstractTypeMatchingMultimap<D>
	implements TypeMatchingMultimap<D>
{
	protected final ListMultimap<Class<?>, TypeRefHolder<D>> backingMap;

	protected AbstractTypeMatchingMultimap(
		ListMultimap<Class<?>, TypeRefHolder<D>> backingMap
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
	public ListIterable<D> get(TypeRef type)
	{
		Objects.requireNonNull(type);

		return backingMap.get(type.getErasedType())
			.select(p -> p.ref.isAssignableFrom(type))
			.collect(p -> p.data);
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
			ListIterable<TypeRefHolder<D>> types = backingMap.get(t.getErasedType());
			boolean shouldStop = types.anySatisfy(e -> {
				if(e.ref.isAssignableFrom(t))
				{
					boolean c = predicate.test(e.ref, e.data);
					if(! c) return true;
				}

				return false;
			});

			return ! shouldStop;
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
}
