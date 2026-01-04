#import "/lib/lib.typ": *;
#let title = [开发者指南];
#show: template.with(title: title);

= 开发者指南

本指南面向想要基于 MzLib 开发插件或功能的开发者。

= 开发环境设置

== 安装开发工具

1. 安装 Java 8 或更高版本
2. 安装 IDE（推荐 IntelliJ IDEA）
3. 安装 Gradle

== 创建项目

```kotlin
// build.gradle.kts
plugins {
    kotlin("jvm") version "1.9.0"
}

repositories {
    maven("https://maven.pkg.github.com/mzverse/mzlib") {
        credentials {
            username = System.getenv("GITHUB_USERNAME")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
    mavenCentral()
}

dependencies {
    compileOnly("org.mzverse:mzlib-minecraft:latest.integration")
}
```

== 配置环境变量

设置环境变量：

- `GITHUB_USERNAME` - GitHub 用户名
- `GITHUB_TOKEN` - GitHub Token（需要 `read:packages` 权限）

= 模块开发

== 创建模块

```java
import mz.mzlib.module.MzModule;
import mz.mzlib.module.RegistrarEventListener;

@RegistrarEventListener
public class MyModule extends MzModule
{
    public static MyModule instance = new MyModule();

    @Override
    public void onLoad()
    {
        // 初始化代码
    }

    @Override
    public void onEnable()
    {
        // 启用代码
    }

    @Override
    public void onDisable()
    {
        // 禁用代码
    }
}
```

== 注册组件

```java
@Override
public void onLoad()
{
    // 注册命令
    this.register(myCommand);

    // 注册事件监听器
    this.register(MyListener.class);

    // 注册其他组件
    this.register(MyComponent.instance);
}
```

= 事件处理

== 监听事件

```java
import mz.mzlib.module.RegistrarEventListener;
import mz.mzlib.event.RegistrarEventClass;
import mz.mzlib.minecraft.event.player.EventPlayerJoin;
import mz.mzlib.minecraft.Player;
import mz.mzlib.minecraft.text.Text;

@RegistrarEventListener
public class MyListener
{
    @RegistrarEventClass
    public static void onPlayerJoin(EventPlayerJoin event)
    {
        Player player = event.player;
        player.sendMessage(Text.literal("欢迎！"));
    }
}
```

== 取消事件

```java
@RegistrarEventClass
public static void onEvent(CancellableEvent event)
{
    if (shouldCancel(event))
    {
        event.cancelled = true;
    }
}
```

== 事件优先级

```java
@RegistrarEventClass(priority = Priority.HIGHEST)
public static void onEvent(MyEvent event)
{
    // 高优先级处理
}
```

= 命令开发

== 创建命令

```java
import mz.mzlib.minecraft.command.Command;
import mz.mzlib.minecraft.command.CommandContext;
import mz.mzlib.minecraft.command.CommandSource;
import mz.mzlib.minecraft.text.Text;

public Command myCommand = new Command("mycommand", "mc")
    .setPermissionChecker(source ->
    {
        if (!source.getPlayer().isSome())
            return Text.literal("只有玩家可以使用此命令");
        return null;
    })
    .setHandler(context ->
    {
        CommandSource source = context.source;
        source.sendMessage(Text.literal("命令执行成功！"));
    });
```

== 处理参数

```java
.setHandler(context ->
{
    String arg0 = new ArgumentParserString("arg0", false).handle(context);
    if (!context.successful)
        return;

    int arg1 = new ArgumentParserInt("arg1").handle(context);
    if (!context.successful)
        return;

    // 使用参数
    context.source.sendMessage(Text.literal("参数: " + arg0 + ", " + arg1));
})
```

== 子命令

```java
public Command parentCommand = new Command("parent")
    .addChild(new Command("child1").setHandler(ctx ->
    {
        ctx.source.sendMessage(Text.literal("子命令 1"));
    }))
    .addChild(new Command("child2").setHandler(ctx ->
    {
        ctx.source.sendMessage(Text.literal("子命令 2"));
    }));
```

= 物品操作

== 创建物品

```java
import mz.mzlib.minecraft.item.ItemStack;
import mz.mzlib.minecraft.item.Items;
import mz.mzlib.minecraft.text.Text;

ItemStack diamond = ItemStack.builder()
    .fromId("minecraft:diamond")
    .count(64)
    .customName(Text.literal("我的钻石"))
    .build();
```

== 修改物品

```java
// 使用 Editor 修改
for (NbtCompound tag : itemStack.tagV_2005().revise())
{
    tag.put("custom_data", "value");
}
```

== 检查物品

```java
// 检查物品类型
if (itemStack.getItem().equals(Items.DIAMOND))
{
    // 处理钻石
}

// 检查物品是否为空
if (itemStack.isEmpty())
{
    // 处理空物品
}
```

= 数据存储

== 使用 DataKey

```java
import mz.mzlib.data.DataKey;
import mz.mzlib.util.Option;

public static final DataKey<Player, String, ?> CUSTOM_DATA = DataKey.of("custom_data");

// 设置数据
CUSTOM_DATA.set(player, "value");

// 获取数据
Option<String> data = CUSTOM_DATA.get(player);

// 删除数据
CUSTOM_DATA.remove(player);
```

== 使用 NBT

```java
import mz.mzlib.minecraft.nbt.NbtCompound;

NbtCompound nbt = NbtCompound.newInstance();
nbt.put("key", "value");

// 读取
Option<String> value = nbt.getString("key");
```

= 国际化

== 添加翻译

在 `lang/zh_cn.json` 中：

```json
{
    "myplugin.message": "我的消息",
    "myplugin.welcome": "欢迎 %s"
}
```

== 使用翻译

```java
import mz.mzlib.minecraft.text.TextTranslatable;

Text message = TextTranslatable.newInstance("myplugin.message");
Text welcome = TextTranslatable.newInstance("myplugin.welcome", "玩家");
```

= 测试

== 创建测试

```java
import mz.mzlib.tester.Tester;
import mz.mzlib.tester.TesterContext;

@Tester
public class MyTests
{
    @TesterFunction
    public void testSomething(TesterContext context)
    {
        // 测试代码
        assert condition : "条件不满足";
    }
}
```

== 运行测试

```bash
./gradlew test
```

= 调试

== 启用调试模式

在 `config.js` 中：

```javascript
{
    "debug": true
}
```

== 使用日志

```java
import java.util.logging.Logger;

Logger logger = Logger.getLogger("MyPlugin");

logger.info("信息");
logger.warning("警告");
logger.severe("错误");
```

= 打包发布

== 构建

```bash
./gradlew build
```

== 发布

1. 在 GitHub Releases 创建新版本
2. 上传构建产物
3. 更新版本号

= 最佳实践

1. 使用版本注解确保兼容性
2. 使用 Option 和 Result 处理可能为空或失败的情况
3. 使用 Editor 模式修改数据
4. 异步处理耗时操作
5. 编写测试
6. 添加文档
7. 遵循代码规范

= 获取帮助

- 查看 #link("tutorial/index")[教程]
- 阅读完整 #link("api_reference")[API 参考]
- 加入 QQ 群: 750455476
- 提交 Issue: https://github.com/mzverse/mzlib/issues