package se.l4.commons.types.internal.mapping;

import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.collections.api.list.ListIterable;

import se.l4.commons.types.mapping.Mapped;

public class MappedImpl<T>
	implements Mapped<T>
{
	private final T data;
	private final ListIterable<Exception> errors;

	public MappedImpl(T data, ListIterable<Exception> errors)
	{
		this.data = data;
		this.errors = errors;
	}

	@Override
	public boolean isPresent()
	{
		return data != null;
	}

	@Override
	public T get()
	{
		if(data == null)
		{
			throw new IllegalStateException();
		}

		return data;
	}

	@Override
	public Optional<T> asOptional()
	{
		return asOptional(() -> new RuntimeException());
	}

	@Override
	public <E extends Exception> Optional<T> asOptional(Supplier<E> errorProvider)
		throws E
	{
		if(errors.isEmpty())
		{
			return Optional.ofNullable(data);
		}

		E exception = errorProvider.get();
		for(Exception e : errors)
		{
			exception.addSuppressed(e);
		}

		throw exception;
	}

	@Override
	public boolean hasErrors()
	{
		return ! errors.isEmpty();
	}

	@Override
	public ListIterable<Exception> getErrors()
	{
		return errors;
	}
}
