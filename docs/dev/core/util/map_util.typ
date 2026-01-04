#import "/lib/lib.typ": *;
#let title = [MapUtil];
#show: template.with(title: title);

`MapUtil` 提供了 Map 操作的各种工具方法。

= 创建 Map

```java
// 创建 HashMap
Map<String, Integer> map = MapUtil.hashMap();

// 创建 LinkedHashMap
Map<String, Integer> linkedMap = MapUtil.linkedHashMap();

// 创建 TreeMap
Map<String, Integer> treeMap = MapUtil.treeMap();
```

= Map 操作

```java
Map<String, Integer> map = ...;

// 获取值或默认值
int value = MapUtil.getOrDefault(map, "key", 0);

// 获取值或计算
int value = MapUtil.getOrCompute(map, "key", k -> k.length());

// 获取值或抛出异常
int value = MapUtil.getOrThrow(map, "key", () -> new RuntimeException("Not found"));

// 检查键是否存在
boolean containsKey = MapUtil.containsKey(map, "key");
```

= 合并 Map

```java
Map<String, Integer> map1 = ...;
Map<String, Integer> map2 = ...;

// 合并 Map（map2 覆盖 map1）
Map<String, Integer> merged = MapUtil.merge(map1, map2);

// 合并 Map（使用函数处理冲突）
Map<String, Integer> merged = MapUtil.merge(map1, map2, (v1, v2) -> v1 + v2);
```

= 转换操作

```java
Map<String, Integer> map = ...;

// 转换键
Map<Integer, Integer> keyTransformed = MapUtil.transformKeys(map, Integer::parseInt);

// 转换值
Map<String, String> valueTransformed = MapUtil.transformValues(map, String::valueOf);

// 过滤
Map<String, Integer> filtered = MapUtil.filter(map, (k, v) -> v > 10);
```

= 使用示例

```java
// 创建初始化的 Map
public Map<String, Integer> createConfig()
{
    return MapUtil.hashMap()
        .put("port", 8080)
        .put("timeout", 30)
        .put("maxConnections", 100);
}

// 安全获取值
public int getValue(Map<String, Integer> map, String key)
{
    return MapUtil.getOrDefault(map, key, 0);
}

// 统计
public Map<String, Integer> countWords(List<String> words)
{
    Map<String, Integer> counts = MapUtil.hashMap();
    for (String word : words)
    {
        counts.put(word, MapUtil.getOrDefault(counts, word, 0) + 1);
    }
    return counts;
}
```

= 注意事项

- 所有方法都处理 null 输入
- 返回的 Map 是可修改的
- 合并操作不会修改原始 Map