package mz.mzlib.util;

import java.util.function.Supplier;

public class LazyConstant<T> implements Supplier<T>
{
    public Option<T> value;
    public Supplier<T> initizer;
    public LazyConstant(Supplier<T> initizer)
    {
        this.initizer = initizer;
    }

    public static <T> LazyConstant<T> of(Supplier<T> initizer)
    {
        return new LazyConstant<>(initizer);
    }

    public void init()
    {
        this.value = Option.fromNullable(initizer.get());
    }

    @Override
    public T get()
    {
        if(this.value != null)
            return this.value.toNullable();
        synchronized(this)
        {
            if(this.value != null)
                return this.value.toNullable();
            this.init();
            return this.value.toNullable();
        }
    }
}
