package mz.mzlib.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.RetryOnFailure;

import java.util.HashMap;
import java.util.WeakHashMap;

public class TestClassCache
{
    static class Key
    {
    }
    static class Value
    {
        Key key;
        Value(Key key)
        {
            this.key = key;
        }
    }
    @Test
    public void testWeakHashMap()
    {
        WeakHashMap<Key, Value> map = new WeakHashMap<>();

        Key key = new Key();
        map.put(key, new Value(key));
        //noinspection UnusedAssignment
        key = null;
        System.gc();

        Assertions.assertEquals(1, new HashMap<>(map).size());
    }

    @Test
    @RetryOnFailure
    public void testClassCache()
    {
        ClassCache<Class<?>, RefStrong<Class<?>>> cache = new ClassCache<>(RefStrong::new);
        Class<?> clazz = new SimpleClassLoader().defineClass1(Key.class.getName(), ClassUtil.getByteCode(Key.class));
        cache.get(clazz);
        System.gc();
        Assertions.assertEquals(1, new HashMap<>(cache.delegate).size());
        //noinspection UnusedAssignment
        clazz = null;
        System.gc();
        Assertions.assertEquals(0, new HashMap<>(cache.delegate).size());
    }
}
