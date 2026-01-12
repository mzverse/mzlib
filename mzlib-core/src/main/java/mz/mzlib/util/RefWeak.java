package mz.mzlib.util;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class RefWeak<T> implements Ref<T>
{
    WeakReference<T> delegate;
    int hashCode;

    public RefWeak(T value)
    {
        this.set(value);
    }

    @Override
    public T get()
    {
        return this.delegate.get();
    }

    @Override
    public void set(T value)
    {
        this.delegate = new WeakReference<>(value);
        this.hashCode = System.identityHashCode(value);
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof Ref && this.get() == ((Ref<?>) obj).get();
    }

    @Override
    public int hashCode()
    {
        return this.hashCode;
    }

    @Override
    public String toString()
    {
        return Objects.toString(this.get());
    }
}
