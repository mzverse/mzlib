#import "/lib/lib.typ": *;
#let title = [Ref];
#show: template.with(title: title);

`Ref` 系列提供了引用类型的包装，用于在不同场景下存储和传递引用。

= Ref

`Ref` 是引用接口，定义了基本的引用操作。

```java
public interface Ref<T>
{
    T get();
    void set(T value);
}
```

= RefStrong

`RefStrong` 是强引用，会阻止对象被垃圾回收。

```java
// 创建强引用
RefStrong<String> ref = new RefStrong<>("Hello");

// 获取值
String value = ref.get();

// 设置值
ref.set("World");

// 判断是否为空
boolean isEmpty = ref.isEmpty();
```

= RefWeak

`RefWeak` 是弱引用，不阻止对象被垃圾回收。

```java
// 创建弱引用
Object obj = new Object();
RefWeak<Object> ref = new RefWeak<>(obj);

// 获取值（可能已被回收）
Option<Object> value = ref.get();

// 设置值
ref.set(new Object());
```

= 使用示例

```java
// 用于修改局部变量
public void example()
{
    RefStrong<Integer> counter = new RefStrong<>(0);

    Runnable task = () ->
    {
        counter.set(counter.get() + 1);
        System.out.println("Count: " + counter.get());
    };

    task.run();  // Count: 1
    task.run();  // Count: 2
}

// 用于缓存
public class Cache<K, V>
{
    private Map<K, RefWeak<V>> cache = new HashMap<>();

    public void put(K key, V value)
    {
        cache.put(key, new RefWeak<>(value));
    }

    public Option<V> get(K key)
    {
        RefWeak<V> ref = cache.get(key);
        if (ref == null)
            return Option.none();
        return ref.get();
    }
}
```

= 注意事项

- RefStrong 会阻止对象被回收
- RefWeak 不阻止对象被回收，get() 可能返回 Option.none()
- 在多线程环境中使用时需要注意同步