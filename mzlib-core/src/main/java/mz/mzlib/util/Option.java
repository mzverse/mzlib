package mz.mzlib.util;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import mz.mzlib.util.wrapper.WrapperFactory;
import mz.mzlib.util.wrapper.WrapperObject;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Nonnull
public final class Option<T> implements Iterable<T>
{
    public static <T> Option<T> some(@Nonnull T value)
    {
        return new Option<>(Objects.requireNonNull(value));
    }
    public static <T> Option<T> none()
    {
        return RuntimeUtil.cast(NONE);
    }
    public static <T> Option<T> fromNullable(@Nullable T value)
    {
        return value != null ? some(value) : none();
    }

    private static final Option<?> NONE = new Option<>(null);

    T value;
    private Option(T value)
    {
        this.value = value;
    }

    @Nullable
    public T toNullable()
    {
        return this.value;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> Option<T> fromOptional(Optional<T> optional)
    {
        return optional.map(Option::some).orElseGet(Option::none);
    }
    public Optional<T> toOptional()
    {
        return this.map(Optional::of).unwrapOrGet(Optional::empty);
    }

    public static <T extends WrapperObject> Option<T> fromWrapper(T wrapper)
    {
        if(wrapper != null && wrapper.isPresent())
            return some(wrapper);
        else
            return none();
    }

    public Either<T, Void> toEither()
    {
        return this.map(Either::<T, Void>first).unwrapOrGet(() -> Either.second(null));
    }

    public boolean isSome()
    {
        return this.toNullable() != null;
    }

    public boolean isSome(Object value)
    {
        T v = this.toNullable();
        return v != null && v.equals(value);
    }

    public boolean isNone()
    {
        return this.toNullable() == null;
    }

    @Nonnull
    public T unwrap() throws NoSuchElementException
    {
        return this.unwrap(NoSuchElementException::new);
    }
    @Nonnull
    public <E extends Throwable> T unwrap(Supplier<E> supplier) throws E
    {
        return this.unwrapOrGet(() -> RuntimeUtil.valueThrow(supplier.get()));
    }
    public T unwrapOr(T defaultValue)
    {
        return this.unwrapOrGet(ThrowableSupplier.constant(defaultValue));
    }
    public <E extends Throwable> T unwrapOrGet(ThrowableSupplier<? extends T, E> supplier) throws E
    {
        T result = this.toNullable();
        if(result == null)
            result = supplier.getOrThrow();
        return result;
    }
    public <E extends Throwable> T unwrapOrGet(Supplier<? extends T> supplier)
    {
        return this.unwrapOrGet(ThrowableSupplier.ofSupplier(supplier));
    }

    public <U> Option<U> and(Option<U> other)
    {
        if(this.isNone())
            return none();
        else
            return other;
    }
    public Option<T> or(Option<T> other)
    {
        if(this.isSome())
            return this;
        else
            return other;
    }

    public <U, E extends Throwable> Option<U> flatMap(ThrowableFunction<? super T, ? extends Option<? extends U>, E> mapper)
        throws E
    {
        if(this.isSome())
            return upcast(mapper.applyOrThrow(this.unwrap()));
        return none();
    }
    public <U> Option<U> flatMap(Function<? super T, ? extends Option<? extends U>> mapper)
    {
        return this.flatMap(ThrowableFunction.ofFunction(mapper));
    }

    public <U, E extends Throwable> Option<U> map(ThrowableFunction<? super T, ? extends U, E> mapper)
        throws E
    {
        return this.flatMap(it -> Option.fromNullable(mapper.applyOrThrow(it)));
    }
    public <U> Option<U> map(Function<? super T, ? extends U> mapper)
    {
        return this.map(ThrowableFunction.ofFunction(mapper));
    }

    public static <T extends U, U> Option<U> upcast(Option<T> value)
    {
        return RuntimeUtil.cast(value);
    }

    /**
     * @see #flatMap(ThrowableFunction)
     */
    @Deprecated
    public <U, E extends Throwable> Option<U> then(ThrowableFunction<? super T, Option<U>, E> mapper) throws E
    {
        return this.flatMap(mapper);
    }
    /**
     * @see #flatMap(Function)
     */
    @Deprecated
    public <U> Option<U> then(Function<? super T, Option<U>> mapper)
    {
        return this.then(ThrowableFunction.ofFunction(mapper));
    }

    public <E extends Throwable> Option<T> filter(ThrowablePredicate<? super T, E> predicate) throws E
    {
        for(T v : this)
        {
            if(predicate.testOrThrow(v))
                return this;
        }
        return none();
    }
    public Option<T> filter(Predicate<? super T> predicate)
    {
        return this.filter(ThrowablePredicate.ofPredicate(predicate));
    }

    public <U> Option<U> filter(Class<U> type)
    {
        return this.filter(type::isInstance).map(type::cast);
    }
    public <U extends WrapperObject> Option<U> filter(WrapperFactory<U> type)
    {
        return this.filter(WrapperObject.class).filter(type::isInstance).map(type::cast);
    }

    public Stream<T> stream()
    {
        return this.map(Stream::of).unwrapOrGet(Stream::empty);
    }

    @Override
    @Nonnull
    public Iterator<T> iterator()
    {
        return this.map(Collections::singleton).map(Set::iterator).unwrapOrGet(Collections::emptyIterator);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.value);
    }
    @Override
    public boolean equals(Object obj)
    {
        if(obj == this)
            return true;
        if(!(obj instanceof Option))
            return false;
        Option<?> that = (Option<?>) obj;
        return Objects.equals(this.value, that.value);
    }
}