#import "/lib/lib.typ": *;
#let title = [Cache];
#show: template.with(title: title);

`Cache` 提供了缓存功能，用于存储计算结果以提高性能。

= 创建 Cache

```java
// 创建简单缓存
Cache<String, Integer> cache = new Cache<>();

// 创建带过期时间的缓存（秒）
Cache<String, Integer> timedCache = new Cache<>(60);
```

= 操作 Cache

```java
Cache<String, Integer> cache = new Cache<>();

// 存储值
cache.put("key", 42);

// 获取值
Option<Integer> value = cache.get("key");

// 计算并缓存
Integer result = cache.computeIfAbsent("key2", k -> expensiveCalculation(k));

// 移除值
cache.remove("key");

// 清空缓存
cache.clear();
```

= 使用示例

```java
public class ExampleCache
{
    private Cache<String, Player> playerCache = new Cache<>(300);  // 5分钟过期

    public Player getPlayer(String name)
    {
        return playerCache.computeIfAbsent(name, n ->
        {
            // 从数据库或其他地方加载玩家
            return loadPlayerFromDatabase(n);
        });
    }

    public void invalidatePlayer(String name)
    {
        playerCache.remove(name);
    }
}
```

= 缓存策略

```java
// 设置最大大小
cache.setMaxSize(1000);

// 设置过期时间（秒）
cache.setExpireAfterWrite(60);

// 设置访问后过期时间（秒）
cache.setExpireAfterAccess(30);

// 获取缓存统计
Cache.Stats stats = cache.getStats();
System.out.println("Hits: " + stats.hitCount());
System.out.println("Misses: " + stats.missCount());
```

= 注意事项

- 缓存值可能被自动清理
- 使用 `computeIfAbsent` 避免重复计算
- 注意缓存大小，避免内存溢出