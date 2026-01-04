#import "/lib/lib.typ": *;
#let title = [Pair];
#show: template.with(title: title);

`Pair` 是一个简单的键值对容器，用于存储两个相关的值。

= 创建 Pair

```java
// 创建 Pair
Pair<String, Integer> pair = Pair.of("age", 25);

// 使用构造器
Pair<String, Integer> pair2 = new Pair<>("name", "Alice");
```

= 访问值

```java
Pair<String, Integer> pair = Pair.of("count", 10);

// 获取第一个值
String first = pair.first;

// 获取第二个值
Integer second = pair.second;

// 使用 getter 方法
String first2 = pair.getFirst();
Integer second2 = pair.getSecond();
```

= 比较和哈希

```java
Pair<String, Integer> p1 = Pair.of("a", 1);
Pair<String, Integer> p2 = Pair.of("a", 1);

// 比较
boolean equal = p1.equals(p2);  // true

// 哈希
int hash = p1.hashCode();
```

= 使用示例

```java
// 返回多个值
public Pair<String, Integer> parseNameAndAge(String input)
{
    String[] parts = input.split(",");
    return Pair.of(parts[0].trim(), Integer.parseInt(parts[1].trim()));
}

// 使用
Pair<String, Integer> result = parseNameAndAge("Alice, 25");
String name = result.first;
int age = result.second;
```

= 静态方法

```java
// 创建比较器
Comparator<Pair<Integer, String>> comparator = Pair.comparingByFirst();

// 反向比较
Comparator<Pair<Integer, String>> reversed = Pair.comparingByFirst(Comparator.reverseOrder());

// 根据第二个元素比较
Comparator<Pair<String, Integer>> bySecond = Pair.comparingBySecond();
```

= 注意事项

- Pair 是不可变的
- 两个元素都可为 null
- 适合临时存储两个相关值