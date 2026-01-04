#import "/lib/lib.typ": *;
#let title = [5.配置文件];
#show: template.with(title: title);



= 创建和加载

类似Bukkit的`saveDefaultConfig`，我们也可以自动保存和加载配置

首先在项目的资源文件中添加配置，我们使用json

```shell
resources
├── config.json
└── plugin.yml
```

然后你可以在你的主模块加载时加载这个配置

```java
public class Demo extends MzModule
{
    public static Demo instance = new Demo();

    public File dataFolder;

    @Override
    public void onLoad()
    {
        try
        {
            this.config = Config.load(Objects.requireNonNull(this.getClass().getResourceAsStream("/config.json")), new File(this.dataFolder, "config.json"));
        }
        catch(Throwable e)
        {
            throw RuntimeUtil.sneakilyThrow(e);
        }
    }
}
```

记得提前给dataFolder赋值

方法`Config#load`的第一个参数是你默认配置的`InputStream`，直接从jar的`ClassLoader`中获得

第二个参数就是配置保存的位置，没有会自动生成，用户可以修改它

你可以为插件添加一个reload命令来重新执行`Config#load`

= 读取配置

假设你的配置文件结构如下

```json
{
  "test": "Demo",
  "a":
  {
    "b": "c"
  }
}
```

欲访问其中test字段，只需使用

```java
this.config.getString("test");
// 或
this.config.getString("test", "default");
```

欲得到字段a的值（一个JsonObject），只需使用

```java
this.config.get("a").getAsJsonObject();
```

你也可以直接得到a中b的值

```java
this.config.getString("a.b");
```