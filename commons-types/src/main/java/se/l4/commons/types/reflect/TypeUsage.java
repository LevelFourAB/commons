package se.l4.commons.types.reflect;

/**
 * Information about how a type was used. This is used in {@link TypeRef} to
 * represent usage of things like annotations in a type declaration.
 *
 * This allows access to information about annotations used in superclasses,
 * interfaces and members of the type.
 *
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
 *
 */
public interface TypeUsage
	extends Annotated
{
}
