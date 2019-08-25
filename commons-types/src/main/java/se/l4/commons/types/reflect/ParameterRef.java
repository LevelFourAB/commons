package se.l4.commons.types.reflect;

import java.lang.reflect.Parameter;
import java.util.Optional;

/**
 * Reference to a {@link Parameter}. Parameter references are retrieved from
 * a {@link ExecutableRef} such as {@link MethodRef} and {@link ConstructorRef}.
 */
public interface ParameterRef
	extends Annotated
{
	/**
	 * Get the parameter referenced.
	 *
	 * @return
	 */
	Parameter getParameter();

	/**
	 * Get the name of the parameter if available.
	 *
	 * @return
	 */
	Optional<String> getName();

	/**
	 * Get if the name is present.
	 *
	 * @return
	 */
	boolean isNamePresent();

	/**
	 * Get the type of the parameter.
	 *
	 * @return
	 */
	TypeRef getType();

	/**
	 * Get if this parameter was implicitly defined in the source code.
	 *
	 * @return
	 */
	boolean isImplicit();

	/**
	 * Get if this parameter was neither implicitly nor explicitly declared in
	 * the source code.
	 *
	 * @return
	 */
	boolean isSynthetic();

	/**
	 * Get if this parameter represents a variable argument list.
	 *
	 * @return
	 */
	boolean isVarArgs();
}
