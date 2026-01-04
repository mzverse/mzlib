#import "/lib/lib.typ": *;
#let title = [Cache];
#show: template.with(title: title);


`Cache` 提供了基于键值对的缓存功能，使用软引用（SoftReference）存储值，支持弱键和默认值提供者。

= 核心特性

- 使用 `SoftReference` 存储值，内存不足时会被 GC 回收
- 支持弱键（Weak Key），键可以被 GC 回收
- 支持默认值提供者
- 线程安全

= 创建 Cache

使用 Builder 模式创建 Cache：

```java
// 创建简单的 Cache
Cache<String, Integer> cache = Cache.<String, Integer>builder().build();

// 创建带弱键的 Cache
Cache<String, Integer> weakCache = Cache.<String, Integer>builder()
    .weakKey()
    .build();

// 创建带默认值提供者的 Cache
Cache<String, Integer> cacheWithDefault = Cache.<String, Integer>builder()
    .defaultSupplier(key -> expensiveCalculation(key))
    .build();

// 创建带弱键和默认值的 Cache
Cache<String, Integer> fullCache = Cache.<String, Integer>builder()
    .weakKey()
    .defaultSupplier(key -> expensiveCalculation(key))
    .build();
```

= 操作 Cache

== 获取值

```java
Cache<String, Integer> cache = Cache.<String, Integer>builder().build();

// 直接获取值（如果不存在返回 null）
Integer value = cache.get("key");

// 获取值，如果不存在则使用 Supplier 提供
Integer value = cache.get("key", () -> 42);
```

== 存储值

```java
// 存储值
cache.put("key", 42);
```

== 清空缓存

```java
// 清空所有缓存
cache.clear();
```

= 使用示例

== 基本使用

```java
public class ExampleCache
{
    private Cache<String, Player> playerCache = Cache.<String, Player>builder()
        .defaultSupplier(name -> loadPlayerFromDatabase(name))
        .build();

    public Player getPlayer(String name)
    {
        // 如果缓存中没有，会自动调用 defaultSupplier
        return playerCache.get(name);
    }

    private Player loadPlayerFromDatabase(String name)
    {
        // 从数据库加载玩家
        return database.loadPlayer(name);
    }
}
```

== 使用弱键

```java
public class WeakKeyExample
{
    // 使用弱键，当键对象不再被引用时，缓存条目会被自动清理
    private Cache<Object, String> cache = Cache.<Object, String>builder()
        .weakKey()
        .build();

    public void cacheObject(Object obj, String data)
    {
        cache.put(obj, data);
        // 当 obj 不再被引用时，缓存条目会被自动清理
    }
}
```

== 使用 Supplier

```java
public class SupplierExample
{
    private Cache<String, Integer> cache = Cache.<String, Integer>builder().build();

    public Integer getValue(String key)
    {
        // 如果缓存中没有，使用 Supplier 提供值
        return cache.get(key, () -> expensiveCalculation(key));
    }

    private Integer expensiveCalculation(String key)
    {
        // 耗时计算
        return key.hashCode();
    }
}
```

= 注意事项

/ #[= 软引用]:

    Cache 使用 `SoftReference` 存储值，当内存不足时，GC 可能会回收这些值。

/ #[= 弱键]:

    使用弱键时，当键对象不再被引用时，缓存条目会被自动清理。

/ #[= 线程安全]:

    Cache 是线程安全的，可以在多线程环境中使用。

/ #[= 默认值]:

    设置默认值提供者后，调用 `get(key)` 会自动使用默认值填充缓存。

/ #[= 内存管理]:

    由于使用软引用，缓存值可能在任何时候被 GC 回收，使用时要注意处理 null 值。

= 性能考虑

/ #[= 适合缓存]:

    - 计算成本高的操作
    - 频繁访问但不常变化的数据
    - 可以容忍缓存丢失的场景

/ #[= 不适合缓存]:

    - 需要精确控制过期时间的场景
    - 需要统计缓存命中率的场景
    - 需要限制缓存大小的场景

#cardInfo[
    Cache 使用软引用和弱键，适合缓存可以容忍丢失的数据。
    
    如果需要更复杂的缓存功能（如过期时间、大小限制、统计等），建议使用其他缓存库（如 Caffeine、Guava Cache）。
]