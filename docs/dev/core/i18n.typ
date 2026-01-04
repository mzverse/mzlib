#import "/lib/lib.typ": *;
#let title = [国际化];
#show: template.with(title: title);

国际化系统支持多语言，自动根据玩家客户端语言设置显示对应文本。

= I18n

`I18n` 是国际化的核心类，负责加载和管理语言文件。

```java
// 加载语言文件
I18n i18n = I18n.load(inputStream, "lang", 0);

// 获取翻译文本
String text = I18n.resolve("en_us", "key.example");

// 带参数的翻译
String text = I18n.resolve("en_us", "key.example", "arg1", "arg2");
```

= RegistrarI18n

`RegistrarI18n` 是语言注册器，用于注册语言文件。

```java
public class MyModule extends MzModule
{
    @Override
    public void onLoad()
    {
        this.register(RegistrarI18n.instance);
        // 注册语言文件
        this.register(I18n.load(getClass().getClassLoader(), "lang", 0));
    }
}
```

= 语言文件格式

语言文件使用 JSON 格式：

```json
{
  "key.example": "示例文本",
  "key.welcome": "欢迎, %s!",
  "key.count": "数量: %d"
}
```

= MinecraftI18n

`MinecraftI18n` 提供了 Minecraft 特定的国际化支持。

```java
// 解析文本组件
Text text = MinecraftI18n.resolveText(player, "key.example");

// 带参数
Text text = MinecraftI18n.resolveText(player, "key.welcome", player.getName());
```

= 使用示例

```java
@RegistrarEventListener
public class MyListener
{
    @RegistrarEventClass
    public static void onPlayerJoin(EventPlayerJoin event)
    {
        Player player = event.player;
        Text welcome = MinecraftI18n.resolveText(player, "key.welcome", player.getName());
        player.sendMessage(welcome);
    }
}
```

= 注意事项

- 语言文件应放在 `resources/lang` 目录下
- 文件名格式为 `语言代码.json`，如 `en_us.json`, `zh_cn.json`
- 支持参数替换，使用 `%s`, `%d` 等占位符