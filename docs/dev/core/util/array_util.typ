#import "/lib/lib.typ": *;
#let title = [ArrayUtil];
#show: template.with(title: title);

`ArrayUtil` 提供了数组操作的工具方法。

= 创建数组

```java
// 创建空数组
String[] array = ArrayUtil.emptyArray(String.class);

// 创建指定长度的数组
int[] array = ArrayUtil.newArray(int.class, 10);

// 从列表创建数组
String[] array = ArrayUtil.toArray(list, String.class);
```

= 数组操作

```java
int[] array = {1, 2, 3, 4, 5};

// 判断是否为空
boolean empty = ArrayUtil.isEmpty(array);

// 获取长度
int length = ArrayUtil.length(array);

// 转换为列表
List<Integer> list = ArrayUtil.toList(array);

// 复制数组
int[] copy = ArrayUtil.copyOf(array);

// 反转数组
int[] reversed = ArrayUtil.reverse(array);
```

= 查找操作

```java
String[] array = {"a", "b", "c", "d"};

// 查找元素
int index = ArrayUtil.indexOf(array, "c");

// 查找最后一个出现的位置
int lastIndex = ArrayUtil.lastIndexOf(array, "a");

// 检查是否包含
boolean contains = ArrayUtil.contains(array, "b");
```

= 修改操作

```java
int[] array = {1, 2, 3, 4, 5};

// 添加元素
int[] newArray = ArrayUtil.add(array, 6);

// 移除元素
int[] newArray = ArrayUtil.remove(array, 3);

// 连接数组
int[] combined = ArrayUtil.concat(array1, array2);

// 填充数组
ArrayUtil.fill(array, 0);
```

= 使用示例

```java
// 安全的数组操作
public void processArray(String[] array)
{
    if (ArrayUtil.isEmpty(array))
    {
        return;
    }

    String first = array[0];
    System.out.println("First: " + first);
}

// 动态构建数组
public int[] buildArray(int... values)
{
    return values;
}

// 过滤数组
public String[] filter(String[] array, Predicate<String> predicate)
{
    List<String> list = new ArrayList<>();
    for (String s : array)
    {
        if (predicate.test(s))
        {
            list.add(s);
        }
    }
    return ArrayUtil.toArray(list, String.class);
}
```

= 注意事项

- 所有方法都处理 null 输入
- 返回的新数组是独立的副本
- 基本类型数组需要使用对应的类型