#import "/lib/lib.typ": *;
#let title = [Config];
#show: template.with(title: title);

`Config` 提供了配置文件加载和管理功能。

= 加载配置

```java
// 加载 JavaScript 配置
Config config = Config.loadJs(
    scope,
    inputStream,
    file
);

// 加载 JSON 配置
Config config = Config.loadJson(inputStream);
```

= 访问配置

```java
Config config = ...;

// 获取字符串
String value = config.getString("key");

// 获取整数
int number = config.getInt("number");

// 获取布尔值
boolean flag = config.getBoolean("enabled");

// 获取嵌套值
String nested = config.getString("parent.child.key");

// 获取列表
List<String> list = config.getStringList("items");
```

= 设置配置

```java
// 设置值
config.set("key", "value");
config.set("number", 42);
config.set("enabled", true);

// 设置嵌套值
config.set("parent.child.key", "nested value");
```

= 保存配置

```java
// 保存到文件
config.save(file);

// 保存为 JSON
String json = config.toJson();
```

= 使用示例

```java
public class MyModule extends MzModule
{
    public Config config;

    @Override
    public void onLoad()
    {
        try (InputStream is = IOUtil.openFileInZip(jar, "config.js"))
        {
            this.config = Config.loadJs(
                MinecraftJsUtil.initScope(),
                is,
                new File(dataFolder, "config.js")
            );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean isEnabled()
    {
        return config.getBoolean("enabled", false);
    }
}
```

= 配置文件格式

JavaScript 格式：

```javascript
{
    enabled: true,
    port: 8080,
    messages: {
        welcome: "Welcome!",
        goodbye: "Goodbye!"
    }
}
```

= 注意事项

- 配置文件支持 JavaScript 和 JSON 格式
- 使用默认值避免空指针异常
- 修改配置后记得保存