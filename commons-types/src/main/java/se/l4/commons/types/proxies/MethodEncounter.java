package se.l4.commons.types.proxies;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.fasterxml.classmate.ResolvedType;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * An encounter with a method, contains methods to help with creating a
 * {@link MethodInvoker}.
 *
 * @author Andreas Holstenson
 *
 */
public interface MethodEncounter
{
	/**
	 * Get the method name.
	 *
	 * @return
	 */
	@NonNull
	String getName();

	/**
	 * Get the actual {@link Method}.
	 *
	 * @return
	 */
	@NonNull
	Method getMethod();

	/**
	 * Get the return type of this method.
	 *
	 * @return
	 */
	@NonNull
	ResolvedType getReturnType();

	/**
	 * Get annotations present on the metod.
	 *
	 * @return
	 */
	@NonNull
	Annotation[] getAnnotations();

	/**
	 * Get if the method is annotated with the given type.
	 *
	 * @param annotation
	 *	 annotation to look for
	 * @return
	 *	 {@code true} if the annotation exists on the method, {@code false}
	 *	 otherwise
	 */
	boolean hasAnnotation(@NonNull Class<? extends Annotation> annotation);

	/**
	 * Get a specific annotation on the method.
	 * @param annotation
	 *	 annotation to look for
	 * @return
	 *	 The found annotation or {@code null}.
	 */
	@Nullable
	<T extends Annotation> T getAnnotation(@NonNull Class<T> annotation);

	/**
	 * Get the number of arguments this method has.
	 *
	 * @return
	 */
	int getArgumentCount();

	/**
	 * Get the type of the given argument.
	 *
	 * @param argument
	 *	 the argument index
	 * @return
	 * @throws IllegalArgumentException
	 *   if the argument does not exist
	 */
	@NonNull
	ResolvedType getArgumentType(int argument);

	/**
	 * Find an annotation on the given argument.
	 *
	 * @param argument
	 *	 the argument index
	 * @param type
	 *	 the annotation to look for
	 * @return
	 *	 The found annotation or {@code null}.
	 * @throws IllegalArgumentException
	 *   if the argument does not exist
	 */
	@Nullable
	<T extends Annotation> T findArgumentAnnotation(int argument, @NonNull Class<T> type);

}
