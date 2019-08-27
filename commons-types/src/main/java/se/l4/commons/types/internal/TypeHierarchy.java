package se.l4.commons.types.internal;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Utilities for working with type hierarchies.
 */
public class TypeHierarchy
{
	private TypeHierarchy()
	{
	}

	/**
	 * Visit the hierarchy of the given type, in a breadth-first order. If the
	 * visitor returns {@code false} the visiting will be aborted, while
	 * {@code true} continues the search.
	 *
	 * @param root
	 * @param visitor
	 */
	public static void visitHierarchy(Class<?> root, Predicate<Class<?>> visitor)
	{
		Queue<Class<?>> queue = new LinkedList<>();
		Set<Class<?>> visited = new HashSet<>();

		queue.add(root);

		while(! queue.isEmpty())
		{
			Class<?> type = queue.poll();
			if(! visitor.test(type))
			{
				return;
			}

			for(Class<?> interfaceRef : type.getInterfaces())
			{
				if(! visited.add(interfaceRef)) continue;

				queue.add(interfaceRef);
			}

			Class<?> superclass = type.getSuperclass();
			if(superclass != null && visited.add(superclass))
			{
				queue.add(superclass);
			}
		}
	}

}
