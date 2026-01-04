package mz.mzlib.util;

import java.util.Map;
import java.util.Objects;

/**
 * @see Pair
 * @see java.util.AbstractMap.SimpleEntry
 */
@Deprecated
public class MapEntry<K, V> implements Map.Entry<K, V>
{
    public K key;
    public V value;

    public MapEntry(K key, V value)
    {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey()
    {
        return this.key;
    }

    public void setKey(K key)
    {
        this.key = key;
    }

    @Override
    public V getValue()
    {
        return this.value;
    }

    @Override
    public V setValue(V value)
    {
        V result = this.getValue();
        this.value = value;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
        {
            return true;
        }
        if(!(obj instanceof Map.Entry))
        {
            return false;
        }
        return Objects.equals(this.getKey(), ((Map.Entry<?, ?>) obj).getKey()) &&
            Objects.equals(this.getValue(), ((Map.Entry<?, ?>) obj).getValue());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(key, value);
    }
}
