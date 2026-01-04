#import "/lib/lib.typ": *;
#let title = [Either];
#show: template.with(title: title);

`Either` 是一个二选一类型，可以持有两种不同类型的值之一。

= 创建 Either

```java
// 创建左值
Either<String, Integer> left = Either.left("error");

// 创建右值
Either<String, Integer> right = Either.right(42);
```

= 操作 Either

```java
Either<String, Integer> either = ...;

// 判断是左值还是右值
if (either.isLeft())
{
    String value = either.getLeft().unwrap();
}

if (either.isRight())
{
    Integer value = either.getRight().unwrap();
}

// 获取值或默认值
String value = either.getLeft().unwrapOr("default");
Integer value = either.getRight().unwrapOr(0);
```

= 映射操作

```java
Either<String, Integer> either = Either.right(5);

// 映射右值
Either<String, String> mapped = either.mapRight(x -> "Value: " + x);

// 映射左值
Either<Integer, Integer> mappedLeft = either.mapLeft(x -> x.length());

// 双向映射
Either<String, String> both = either.bimap(
    left -> left.toUpperCase(),
    right -> "Number: " + right
);
```

= 使用示例

```java
// 用于错误处理
public Either<String, Player> findPlayer(String name)
{
    Player player = PlayerManager.instance.getPlayerByName(name);
    if (player == null)
        return Either.left("Player not found");
    return Either.right(player);
}

// 使用
Either<String, Player> result = findPlayer("Steve");
if (result.isRight())
{
    Player player = result.getRight().unwrap();
    player.sendMessage("Found you!");
}
else
{
    String error = result.getLeft().unwrap();
    System.out.println(error);
}
```

= 注意事项

- Either 是不可变的
- 左值通常用于错误，右值用于成功结果
- 使用 `isLeft()` 和 `isRight()` 判断类型