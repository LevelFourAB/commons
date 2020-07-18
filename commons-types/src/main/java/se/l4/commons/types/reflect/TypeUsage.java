package se.l4.commons.types.reflect;

import java.lang.annotation.Annotation;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Lists;

import se.l4.commons.types.internal.reflect.TypeUsageImpl;

/**
 * Information about how a type was used. This is used in {@link TypeRef} to
 * represent usage of things like annotations in a type declaration.
 *
 * <p>
 * This allows access to information about annotations used in superclasses,
 * interfaces and members of the type.
 *
 * <p>
 * An example would be to extract the {@code NotNull} annotation in this
 * example:
 *
 * <pre>
 * class Test implements GenericInterface<@NotNull String> {
 * }
 *
 * // Resolve the type and get the interface
 * TypeRef testType = Types.reference(Test.class);
 * Optional<TypeRef> genericInterfaceType = testType.getInterface(GenericInterface.class);
 *
 * // Resolve the type parameter
 * TypeRef parameter = genericInterfaceType.get().getTypeParameter(0);
 *
 * // Get information about how the type is used
 * TypeUsage usage = parameter.getUsage();
 * if(usage.hasAnnotation(NotNull.class)) {
 *   // NotNull present
 * }
 * </pre>
 *
 */
public interface TypeUsage
	extends Annotated
{
	/**
	 * Create an instance that indicates that the given annotations have been
	 * used.
	 *
	 * @param annotations
	 * @return
	 */
	public static TypeUsage forAnnotations(Annotation... annotations)
	{
		return forAnnotations(Lists.immutable.of(annotations));
	}

	/**
	 * Create an instance that indicates that the given annotations have been
	 * used.
	 *
	 * @param annotations
	 * @return
	 */
	public static TypeUsage forAnnotations(Iterable<? extends Annotation> annotations)
	{
		if(annotations instanceof RichIterable)
		{
			return new TypeUsageImpl((RichIterable<? extends Annotation>) annotations);
		}

		return new TypeUsageImpl(Lists.immutable.ofAll(annotations));
	}
}
