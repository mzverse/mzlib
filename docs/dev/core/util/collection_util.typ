#import "/lib/lib.typ": *;
#let title = [CollectionUtil];
#show: template.with(title: title);

`CollectionUtil` 提供了集合操作的工具方法。

= 创建集合

```java
// 创建 ArrayList
List<String> list = CollectionUtil.newArrayList("a", "b", "c");

// 创建 HashSet
Set<Integer> set = CollectionUtil.newHashSet(1, 2, 3);

// 创建 HashMap
Map<String, Integer> map = CollectionUtil.newHashMap();
map.put("key", 1);
```

= 集合操作

```java
List<String> list = ...;

// 判断是否为空
boolean empty = CollectionUtil.isEmpty(list);

// 判断是否不为空
boolean notEmpty = CollectionUtil.isNotEmpty(list);

// 获取大小
int size = CollectionUtil.size(list);

// 转换为数组
String[] array = CollectionUtil.toArray(list, String.class);
```

= 列表操作

```java
List<String> list = ...;

// 添加所有元素
CollectionUtil.addAll(list, "d", "e");

// 获取第一个元素
Option<String> first = CollectionUtil.getFirst(list);

// 获取最后一个元素
Option<String> last = CollectionUtil.getLast(list);

// 反转列表
List<String> reversed = CollectionUtil.reverse(list);

// 随机打乱
CollectionUtil.shuffle(list);
```

= Map 操作

```java
Map<String, Integer> map = ...;

// 获取值或默认值
int value = CollectionUtil.getOrDefault(map, "key", 0);

// 获取值或计算
int value = CollectionUtil.getOrCompute(map, "key", k -> computeValue(k));

// 获取所有键
Set<String> keys = CollectionUtil.getKeys(map);

// 获取所有值
Collection<Integer> values = CollectionUtil.getValues(map);
```

= 使用示例

```java
// 安全的集合操作
public void processList(List<String> list)
{
    if (CollectionUtil.isEmpty(list))
    {
        return;
    }

    String first = CollectionUtil.getFirst(list).unwrap();
    System.out.println("First: " + first);
}

// 创建并初始化
public Map<String, Integer> createScoreMap()
{
    Map<String, Integer> map = CollectionUtil.newHashMap();
    map.put("Alice", 100);
    map.put("Bob", 95);
    return map;
}
```

= 注意事项

- 所有方法都处理 null 输入
- 返回的集合是可修改的
- 使用 Option 包装可能为空的结果