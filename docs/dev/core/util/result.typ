#import "/lib/lib.typ": *;
#let title = [Result];
#show: template.with(title: title);

`Result` 是一个结果类型，用于表示可能失败的操作结果。

= 创建 Result

```java
// 创建成功的结果
Result<String, String> success = Result.success("Hello");

// 创建失败的结果
Result<String, String> failure = Result.failure("Error occurred");

// 使用 Option 作为错误类型
Result<String, Option<String>> result = Result.failure(Option.none());
```

= 操作 Result

```java
Result<String, String> result = ...;

// 判断是否成功
if (result.isSuccess())
{
    // 处理成功情况
    String value = result.getValue().unwrap();
}

// 判断是否失败
if (result.isFailure())
{
    // 处理失败情况
    String error = result.getError().unwrap();
}

// 获取值或默认值
String value = result.getValue().unwrapOr("default");
```

= 链式操作

```java
Result<Integer, String> result = Result.success(5)
    .map(x -> x * 2)
    .map(x -> x + 1);  // 结果为 11

Result<Integer, String> failed = Result.<Integer, String>failure("error")
    .map(x -> x * 2);  // 仍然是失败
```

= 使用示例

```java
public Result<ItemStack, String> createItem(String id)
{
    try
    {
        Item item = Items.byId(id);
        if (item == null)
            return Result.failure("Item not found: " + id);
        return Result.success(ItemStack.newInstance(item));
    }
    catch (Exception e)
    {
        return Result.failure(e.getMessage());
    }
}
```

= 注意事项

- Result 是不可变的
- 使用 `unwrap()` 获取值时，如果失败会抛出异常
- 使用 `unwrapOr()` 提供默认值更安全