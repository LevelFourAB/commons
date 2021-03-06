package se.l4.commons.types.reflect;

import java.lang.reflect.Executable;
import java.util.Optional;

import org.eclipse.collections.api.list.ListIterable;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Reference to {@link Executable}.
 */
public interface ExecutableRef
	extends MemberRef
{
	/**
	 * Get the executable that this is a reference to.
	 *
	 * @return
	 */
	@NonNull
	Executable getExecutable();

	/**
	 * Get all of the parameter names that are available for this type.
	 *
	 * @return
	 */
	@NonNull
	ListIterable<String> getTypeParameterNames();

	/**
	 * Get the type parameters.
	 *
	 * @return
	 */
	@NonNull
	ListIterable<TypeRef> getTypeParameters();

	/**
	 * Get a type parameter using an index.
	 *
	 * @param index
	 * @return
	 */
	@NonNull
	Optional<TypeRef> getTypeParameter(int index);

	/**
	 * Get a type parameter using a name.
	 *
	 * @param name
	 * @return
	 */
	@NonNull
	Optional<TypeRef> getTypeParameter(String name);

	/**
	 * Get the parameter types of this executable.
	 *
	 * @return
	 */
	ListIterable<TypeRef> getParameterTypes();

	/**
	 * Get the parameters of this executable.
	 *
	 * @return
	 */
	ListIterable<ParameterRef> getParameters();

	/**
	 * Get the number of parameters that are available.
	 *
	 * @return
	 */
	int getParameterCount();

	/**
	 * Get the receiver type of this executable. See {@link Executable#getAnnotatedReceiverType()}
	 * for details.
	 */
	TypeRef getReceiverType();

	/**
	 * Get the return type of this executable.
	 */
	TypeRef getReturnType();

	/**
	 * Get the exception types of this executable.
	 */
	ListIterable<TypeRef> getExceptionTypes();

	/**
	 * Get if this executable was declared to take a variable number of
	 * arguments.
	 */
	boolean isVarArgs();
}
