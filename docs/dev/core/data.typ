#import "/lib/lib.typ": *;
#let title = [数据系统];
#show: template.with(title: title);

数据系统提供了一种类型安全的数据存储和访问机制。

= DataKey

`DataKey` 是数据的键，用于标识和访问特定类型的数据。

```java
// 创建一个 DataKey
DataKey<Player, String, ?> playerNameKey = DataKey.of("player_name", String.class);

// 设置数据
playerNameKey.set(player, "Steve");

// 获取数据
Option<String> name = playerNameKey.get(player);

// 移除数据
playerNameKey.remove(player);
```

= DataHandler

`DataHandler` 是数据的处理器，负责管理数据的存储和访问。

```java
// 获取 DataHandler
DataHandler handler = player.getDataHandler();

// 使用 DataHandler 操作数据
handler.set(key, value);
Option<T> value = handler.get(key);
```

= 使用示例

```java
public class ExampleModule extends MzModule
{
    public static DataKey<Player, Integer, ?> scoreKey = DataKey.of("score", Integer.class);

    @Override
    public void onLoad()
    {
        this.register(scoreKey);
    }

    public void setPlayerScore(Player player, int score)
    {
        scoreKey.set(player, score);
    }

    public Option<Integer> getPlayerScore(Player player)
    {
        return scoreKey.get(player);
    }
}
```

= 注意事项

- DataKey 需要在模块的 `onLoad` 方法中注册
- 数据是类型安全的，编译时会检查类型
- 使用 `Option` 包装返回值，避免空指针异常