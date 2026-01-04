package mz.mzlib.util;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Cache<K, V>
{
    V get(K key);
    V get(K key, Supplier<V> supplier);
    void put(K key, V value);
    void clear();

    static <K, V> Builder<K, V> builder()
    {
        return new Builder<>();
    }
    class Builder<K, V>
    {
        boolean weakKey = false;
        Function<K, V> defaultSupplier = ThrowableSupplier.constant((V) null).ignore();
        public Builder<K, V> weakKey(boolean value)
        {
            this.weakKey = value;
            return this;
        }
        public Builder<K, V> weakKey()
        {
            return this.weakKey(true);
        }
        public Builder<K, V> defaultSupplier(Function<K, V> value)
        {
            this.defaultSupplier = value;
            return this;
        }
        public Cache<K, V> build()
        {
            return new Impl<>(weakKey ? new MapConcurrentWeakHash<>() : new ConcurrentHashMap<>(), this.defaultSupplier);
        }
    }

    class Impl<K, V> implements Cache<K, V>
    {
        Map<K, SoftReference<V>> data;
        Function<K, V> defaultSupplier;

        Impl(Map<K, SoftReference<V>> data, Function<K, V> defaultSupplier)
        {
            this.data = data;
            this.defaultSupplier = defaultSupplier;
        }

        @Override
        public V get(K key)
        {
            return this.get(key, () -> this.defaultSupplier.apply(key));
        }

        @Override
        public V get(K key, Supplier<V> supplier)
        {
            Ref<V> result = new RefStrong<>(null);
            this.data.compute(key, (k, v) ->
            {
                if(v != null)
                {
                    result.set(v.get());
                    if(result.get() != null)
                        return v;
                }
                result.set(supplier.get());
                return new SoftReference<>(result.get());
            });
            return result.get();
        }

        @Override
        public void put(K key, V value)
        {
            this.data.put(key, new SoftReference<>(value));
        }

        @Override
        public void clear()
        {
            this.data.clear();
        }
    }
}
