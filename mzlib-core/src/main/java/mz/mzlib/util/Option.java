package mz.mzlib.util;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import mz.mzlib.util.wrapper.WrapperFactory;
import mz.mzlib.util.wrapper.WrapperObject;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

// TODO Refactor to final class
@Nonnull
public abstract class Option<T> implements Iterable<T>
{
    public static <T> Option<T> some(T value)
    {
        return new Some<>(value);
    }
    public static <T> Option<T> none()
    {
        return RuntimeUtil.cast(None.instance);
    }

    public static <T> Option<T> fromNullable(@Nullable T value)
    {
        if(value == null)
            return none();
        else
            return some(value);
    }

    @Nullable
    public abstract T toNullable();

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> Option<T> fromOptional(Optional<T> optional)
    {
        return fromNullable(optional.orElse(null));
    }

    public abstract Optional<T> toOptional();

    public static <T extends WrapperObject> Option<T> fromWrapper(T wrapper)
    {
        if(wrapper != null && wrapper.isPresent())
            return some(wrapper);
        else
            return none();
    }

    public abstract boolean isSome();

    public abstract boolean isSome(Object value);

    public abstract boolean isNone();

    @Nonnull
    public abstract T unwrap();

    @Nonnull
    public <E extends Throwable> T unwrap(Supplier<E> supplier) throws E
    {
        return this.unwrapOrGet(() -> RuntimeUtil.valueThrow(supplier.get()));
    }

    public abstract T unwrapOr(T defaultValue);

    public abstract <E extends Throwable> T unwrapOrGet(ThrowableSupplier<? extends T, E> supplier) throws E;

    public T unwrapOrGet(Supplier<? extends T> supplier)
    {
        return this.unwrapOrGet(ThrowableSupplier.ofSupplier(supplier));
    }

    public abstract <U> Option<U> and(Option<U> other);

    public abstract Option<T> or(Option<T> other);

    public abstract <U, E extends Throwable> Option<U> flatMap(ThrowableFunction<? super T, ? extends Option<? extends U>, E> mapper)
        throws E;
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

    public abstract <U, E extends Throwable> Option<U> then(ThrowableFunction<? super T, Option<U>, E> mapper) throws E;
    public <U> Option<U> then(Function<? super T, Option<U>> mapper)
    {
        return this.then(ThrowableFunction.ofFunction(mapper));
    }

    public abstract <E extends Throwable> Option<T> filter(ThrowablePredicate<? super T, E> predicate) throws E;
    public Option<T> filter(Predicate<? super T> predicate)
    {
        return this.filter(ThrowablePredicate.ofPredicate(predicate));
    }

    public <U> Option<U> filter(Class<U> type)
    {
        return this.filter(type::isInstance).map(RuntimeUtil::cast);
    }
    public <U extends WrapperObject> Option<U> filter(WrapperFactory<U> type)
    {
        return this.filter(WrapperObject.class).filter(type::isInstance).map(RuntimeUtil::cast);
    }

    public Stream<T> stream()
    {
        for(T t : this)
            return Stream.of(t);
        return Stream.empty();
    }

    protected static class Some<T> extends Option<T>
    {
        T value;
        public Some(T value)
        {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public boolean isSome()
        {
            return true;
        }
        @Override
        public boolean isSome(Object value)
        {
            return this.unwrap().equals(value);
        }
        @Override
        public boolean isNone()
        {
            return false;
        }
        @Override
        public T toNullable()
        {
            return this.unwrap();
        }

        @Override
        public Optional<T> toOptional()
        {
            return Optional.of(this.unwrap());
        }


        @Nonnull
        @Override
        public T unwrap()
        {
            return this.value;
        }
        public T unwrapOr(T defaultValue)
        {
            return this.unwrap();
        }
        @Override
        public <E extends Throwable> T unwrapOrGet(ThrowableSupplier<? extends T, E> supplier)
        {
            return this.unwrap();
        }

        @Override
        public <U> Option<U> and(Option<U> other)
        {
            return other;
        }

        @Override
        public Option<T> or(Option<T> other)
        {
            return this;
        }

        @Override
        public <U, E extends Throwable> Option<U> flatMap(ThrowableFunction<? super T, ? extends Option<? extends U>, E> mapper) throws E
        {
            return RuntimeUtil.cast(mapper.applyOrThrow(this.unwrap()));
        }

        @Override
        public <U, E extends Throwable> Option<U> then(ThrowableFunction<? super T, Option<U>, E> mapper) throws E
        {
            return mapper.applyOrThrow(this.unwrap());
        }

        @Override
        public <E extends Throwable> Option<T> filter(ThrowablePredicate<? super T, E> predicate) throws E
        {
            if(predicate.testOrThrow(this.unwrap()))
                return this;
            else
                return none();
        }
        @Override
        public int hashCode()
        {
            return this.value.hashCode();
        }

        @Override
        public boolean equals(Object obj)
        {
            if(obj == this)
                return true;
            if(!(obj instanceof Some))
                return false;
            return this.value.equals(((Some<?>) obj).value);
        }

        @Override
        @Nonnull
        public Iterator<T> iterator()
        {
            return new Itr();
        }

        class Itr implements Iterator<T>
        {
            boolean hasNext = true;
            @Override
            public boolean hasNext()
            {
                return this.hasNext;
            }
            @Override
            public T next()
            {
                if(!this.hasNext())
                    throw new NoSuchElementException();
                this.hasNext = false;
                return Some.this.value;
            }
        }
    }

    protected static class None<T> extends Option<T>
    {
        static None<?> instance = new None<>();

        @Override
        public boolean isSome()
        {
            return false;
        }
        @Override
        public boolean isSome(Object value)
        {
            return false;
        }
        @Override
        public boolean isNone()
        {
            return true;
        }
        @Override
        public T toNullable()
        {
            return null;
        }
        @Override
        public Optional<T> toOptional()
        {
            return Optional.empty();
        }

        @Nonnull
        @Override
        public T unwrap()
        {
            throw new NoSuchElementException();
        }
        @Override
        public T unwrapOr(T defaultValue)
        {
            return defaultValue;
        }
        @Override
        public <E extends Throwable> T unwrapOrGet(ThrowableSupplier<? extends T, E> supplier) throws E
        {
            return supplier.getOrThrow();
        }

        @Override
        public <U> Option<U> and(Option<U> other)
        {
            return none();
        }

        @Override
        public Option<T> or(Option<T> other)
        {
            return other;
        }

        @Override
        public <U, E extends Throwable> Option<U> flatMap(ThrowableFunction<? super T, ? extends Option<? extends U>, E> mapper)
        {
            return none();
        }
        @Override
        public <U, E extends Throwable> Option<U> then(ThrowableFunction<? super T, Option<U>, E> mapper)
        {
            return none();
        }

        @Override
        public <E extends Throwable> Option<T> filter(ThrowablePredicate<? super T, E> predicate) throws E
        {
            return this;
        }
        @Override
        public int hashCode()
        {
            return 0;
        }

        public boolean equals(Object obj)
        {
            return obj instanceof None;
        }

        @Override
        @Nonnull
        public Iterator<T> iterator()
        {
            return new Itr();
        }

        class Itr implements Iterator<T>
        {
            @Override
            public boolean hasNext()
            {
                return false;
            }
            @Override
            public T next()
            {
                throw new NoSuchElementException();
            }
        }
    }
}