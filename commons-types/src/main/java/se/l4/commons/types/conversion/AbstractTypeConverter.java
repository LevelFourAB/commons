package se.l4.commons.types.conversion;

import java.util.HashSet;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Supplier;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import edu.umd.cs.findbugs.annotations.NonNull;
import se.l4.commons.types.Types;
import se.l4.commons.types.matching.ClassMatchingMultimap;
import se.l4.commons.types.matching.MatchedType;
import se.l4.commons.types.reflect.TypeRef;

public abstract class AbstractTypeConverter
	implements TypeConverter
{
	private static final ConversionFunction<?, ?> IDENTITY = in -> in;

	protected final ClassMatchingMultimap<Object, Conversion<?, ?>> conversions;
	protected final Cache<CacheKey, Conversion<?, ?>> cached;

	protected AbstractTypeConverter(ClassMatchingMultimap<Object, Conversion<?, ?>> map)
	{
		this.conversions = map;

		cached = CacheBuilder.newBuilder()
			.maximumSize(100)
			.build();
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addConversion(ConversionFunction<?, ?> function)
	{
		TypeRef ref = Types.reference(function.getClass());
		TypeRef func = ref.findInterface(ConversionFunction.class)
			.get();

		Supplier<IllegalArgumentException> error = () -> new IllegalArgumentException(
			"The input and output of the conversion is not know, either "
			+ "update the implementation or specify input and output when "
			+ "registering"
		);

		TypeRef input = func.getTypeParameter(0).orElseThrow(error);
		TypeRef output = func.getTypeParameter(1).orElseThrow(error);

		if(! input.isResolved() || ! output.isResolved())
		{
			throw error.get();
		}

		addConversion(input.getErasedType(), output.getErasedType(), (ConversionFunction) function);
	}

	@Override
	public <I, O> void addConversion(Class<I> in, Class<O> out, ConversionFunction<I, O> function)
	{
		addConversion(new ConversionImpl<>(in, out, function));
	}

	@Override
	public void addConversion(Conversion<?, ?> conversion)
	{
		conversions.put(conversion.getInput(), conversion);
	}

	@Override
	public boolean canConvertBetween(Class<?> in, Class<?> out)
	{
		return getConversion(in, out).isPresent();
	}

	@Override
	public boolean canConvertBetween(Object in, Class<?> out)
	{
		return canConvertBetween(in == null ? Void.class : in.getClass(), out);
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@NonNull
	public <I, O> Optional<Conversion<I, O>> getConversion(
		@NonNull Class<I> input,
		@NonNull Class<O> output
	)
	{
		if(output.isAssignableFrom(input))
		{
			return Optional.of(new ConversionImpl(input, output, IDENTITY));
		}

		CacheKey key = new CacheKey(input, output);

		Conversion<?, ?> conversion = cached.getIfPresent(key);
		if(conversion != null)
		{
			return Optional.of((Conversion) conversion);
		}

		PriorityQueue<Conversion<?, ?>> queue = new PriorityQueue<>((a, b) -> Integer.compare(score(a), score(b)));
		Set<Conversion<?, ?>> checked = new HashSet<>();

		// Get all of the conversion that can handle the input type
		for(MatchedType<Object, Conversion<?, ?>> m : conversions.getAll(input))
		{
			checked.add(m.getData());
			queue.add(m.getData());
		}

		while(! queue.isEmpty())
		{
			Conversion<?, ?> potential = queue.poll();
			if(output.isAssignableFrom(potential.getOutput()))
			{
				// The output is assignable from the result of the conversion
				cached.put(key, potential);
				return Optional.of((Conversion) potential);
			}

			// Continue the search
			for(MatchedType<Object, Conversion<?, ?>> m : conversions.getAll(input))
			{
				if(checked.add(m.getData()))
				{
					queue.add(m.getData());
				}
			}
		}

		return Optional.empty();
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <I, O> Optional<Conversion<I, O>> getConversion(I input, Class<O> output)
	{
		return (Optional) getConversion((Class) (input == null ? Void.class : input.getClass()), output);
	}

	@Override
	public <T> Conversion<? extends Object, T> createDynamicConversionTo(Class<T> out)
	{
		return new Conversion<Object, T>()
		{
			@Override
			public Class<Object> getInput()
			{
				return Object.class;
			}

			@Override
			public Class<T> getOutput()
			{
				return out;
			}

			@Override
			public T convert(Object in)
			{
				return AbstractTypeConverter.this.convert(in, out);
			}
		};
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <I, O> Conversion<I, O> getDynamicConversion(Class<I> in, Class<O> out)
	{
		return getConversion(in, out)
			.orElseGet(() -> (Conversion) createDynamicConversionTo(out));
	}

	@Override
	public <T> T convert(Object in, Class<T> output)
	{
		return getConversion(in, output)
			.map(c -> c.convert(in))
			.orElseThrow(() -> new ConversionException("No conversion available to " + output));

	}

	private static int score(Conversion<?, ?> c)
	{
		if(c instanceof CompoundConversion)
		{
			return ((CompoundConversion<?, ?, ?>) c).score;
		}

		return 1;
	}

	protected static class CacheKey
	{
		private Class<?> in;
		private Class<?> out;

		public CacheKey(Class<?> in, Class<?> out)
		{
			this.in = in;
			this.out = out;
		}

		@Override
		public boolean equals(Object obj)
		{
			if(obj instanceof CacheKey)
			{
				CacheKey key = (CacheKey) obj;
				return in.equals(key.in) && out.equals(key.out);
			}

			return false;
		}

		@Override
		public int hashCode()
		{
			int c = 37;
			int t = 17;

			t = t * c + in.hashCode();
			t = t * c + out.hashCode();

			return t;
		}
	}

	private static class ConversionImpl<I, O>
		implements Conversion<I, O>
	{
		private final Class<I> input;
		private final Class<O> output;
		private final ConversionFunction<I, O> function;

		public ConversionImpl(
			Class<I> input,
			Class<O> output,
			ConversionFunction<I, O> function
		)
		{
			this.input = input;
			this.output = output;
			this.function = function;
		}

		@Override
		public Class<I> getInput()
		{
			return input;
		}

		@Override
		public Class<O> getOutput()
		{
			return output;
		}

		@Override
		public O convert(I object)
		{
			return function.convert(object);
		}
	}

	private static class CompoundConversion<I, T, O>
		implements Conversion<I, O>
	{
		private final Conversion<I, T> left;
		private final Conversion<T, O> right;

		public final int score;

		public CompoundConversion(
			Conversion<I, T> left,
			Conversion<T, O> right
		)
		{
			this.left = left;
			this.right = right;

			score = score(left) + score(right);
		}

		@Override
		public Class<I> getInput()
		{
			return left.getInput();
		}

		@Override
		public Class<O> getOutput()
		{
			return right.getOutput();
		}

		@Override
		public O convert(I object)
		{
			return right.convert(left.convert(object));
		}
	}
}
