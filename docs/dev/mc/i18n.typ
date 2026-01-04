#import "/lib/lib.typ": *;
#let title = [国际化];
#show: template.with(title: title);

Minecraft 国际化系统提供了多语言支持和文本翻译功能。

= I18nMinecraft

`I18nMinecraft` 是 Minecraft 国际化的核心类。

```java
// 获取翻译文本
String text = I18nMinecraft.resolve("en_us", "key.example");

// 带参数的翻译
String text = I18nMinecraft.resolve("en_us", "key.welcome", "Steve");
```

= MinecraftI18n

`MinecraftI18n` 提供了文本组件的翻译。

```java
// 解析文本组件
Text text = MinecraftI18n.resolveText(player, "key.example");

// 带参数
Text text = MinecraftI18n.resolveText(player, "key.welcome", player.getName());

// 使用 Map 参数
Map<String, Object> args = Map.of("name", "Steve");
Text text = MinecraftI18n.resolveText(player, "key.welcome", args);
```

= VanillaI18nV_1300

`VanillaI18nV_1300` 是原版国际化实现（1.13+）。

```java
// 获取原版翻译
String translation = VanillaI18nV_1300.translate("key.example");

// 检查是否有翻译
boolean hasTranslation = VanillaI18nV_1300.hasTranslation("key.example");

// 获取语言代码
String lang = VanillaI18nV_1300.getLanguage();
```

= LangEditor

`LangEditor` 提供了语言文件的编辑功能。

```java
// 创建语言编辑器
LangEditor editor = new LangEditor();

// 添加翻译
editor.put("key.example", "Example text");

// 移除翻译
editor.remove("key.example");

// 保存到文件
editor.save(file);

// 从文件加载
editor.load(file);
```

= 使用示例

```java
@RegistrarEventListener
public class I18nListener
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

- 语言文件放在 `resources/lang` 目录
- 文件名格式为 `语言代码.json`
- 使用 `%s`, `%d` 等占位符支持参数