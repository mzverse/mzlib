#import "/lib/lib.typ": *;
#set raw(lang: "java");
#let title = [平台适配];
#show: template.with(title: title);


MzLib-Minecraft 支持多个 Minecraft 平台，提供统一的 API 接口，让你的代码可以在不同平台上运行。

= 支持的平台

MzLib-Minecraft 支持以下平台：

/ #[= Fabric]:

    Minecraft 的模组加载器，支持 Minecraft 1.14+

/ #[= NeoForge]:

    Minecraft 的模组加载器，从 Forge 分支而来，支持 Minecraft 1.20.1+

/ #[= Bukkit/Spigot]:

    Minecraft 服务端插件平台，支持 Minecraft 1.7.10+

/ #[= Paper]:

    Bukkit 的高性能分支，支持 Minecraft 1.7.10+

/ #[= Folia]:

    Paper 的多线程分支，支持区域化多线程

/ #[= Vanilla]:

    原版 Minecraft 服务器，支持 Minecraft 1.11+

= 平台标签

MzLib 使用平台标签来区分不同的平台。

```java
public class MinecraftPlatform
{
    public static class Tag
    {
        public static final String FABRIC = "fabric";
        public static final String NEOFORGE = "neoforge";
        public static final String BUKKIT = "bukkit";
        public static final String PAPER = "paper";
        public static final String FOLIA = "folia";
    }
}
```

= 平台检测

使用 `MinecraftPlatform` 检测当前运行的平台。

```java
import mz.mzlib.minecraft.MinecraftPlatform;

// 检测平台
if (MinecraftPlatform.isFabric())
{
    System.out.println("运行在 Fabric 平台");
}

if (MinecraftPlatform.isNeoForge())
{
    System.out.println("运行在 NeoForge 平台");
}

if (MinecraftPlatform.isBukkit())
{
    System.out.println("运行在 Bukkit 平台");
}

if (MinecraftPlatform.isPaper())
{
    System.out.println("运行在 Paper 平台");
}

if (MinecraftPlatform.isFolia())
{
    System.out.println("运行在 Folia 平台");
}
```

= 平台特定代码

使用 `@Enabled` 和 `@Disabled` 注解控制代码在不同平台的启用和禁用。

```java
import mz.mzlib.minecraft.MinecraftPlatform;
import mz.mzlib.minecraft.MinecraftPlatform.Enabled;
import mz.mzlib.minecraft.MinecraftPlatform.Disabled;

// 只在 Fabric 平台启用
@Enabled(MinecraftPlatform.Tag.FABRIC)
public class FabricSpecificFeature
{
    // Fabric 特定代码
}

// 只在 NeoForge 平台启用
@Enabled(MinecraftPlatform.Tag.NEOFORGE)
public class NeoForgeSpecificFeature
{
    // NeoForge 特定代码
}

// 在 Bukkit 平台禁用
@Disabled(MinecraftPlatform.Tag.BUKKIT)
public class NonBukkitFeature
{
    // 非 Bukkit 特定代码
}
```

= 平台入口

不同平台需要不同的入口类。

== Fabric 入口

```java
import net.fabricmc.api.ModInitializer;

public class MzLibFabricInitializer implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        // 初始化 MzLib
        MzLibMinecraft.instance.onLoad();
        MzLibMinecraft.instance.onEnable();
    }
}
```

在 `fabric.mod.json` 中配置：

```json
{
  "entrypoints": {
    "main": [
      "mz.mzlib.minecraft.fabric.MzLibFabricInitializer"
    ]
  }
}
```

== NeoForge 入口

```java
import net.neoforged.fml.common.Mod;

@Mod("mzlib")
public class MzLibNeoForge
{
    public MzLibNeoForge()
    {
        // 初始化 MzLib
        MzLibMinecraft.instance.onLoad();
        MzLibMinecraft.instance.onEnable();
    }
}
```

在 `META-INF/neoforge.mods.toml` 中配置：

```toml
[[mods]]
modId="mzlib"
version="${version}"
displayName="MzLib"
description="MzLib Minecraft"
[[mods.dependencies]]
modId="neoforge"
type="required"
versionRange="[21,)"
ordering="NONE"
side="BOTH"
```

== Bukkit 入口

```java
import org.bukkit.plugin.java.JavaPlugin;

public class MzLibBukkitPlugin extends JavaPlugin
{
    @Override
    public void onLoad()
    {
        // 初始化 MzLib
        MzLibMinecraft.instance.onLoad();
    }

    @Override
    public void onEnable()
    {
        MzLibMinecraft.instance.onEnable();
    }

    @Override
    public void onDisable()
    {
        MzLibMinecraft.instance.onDisable();
    }
}
```

在 `plugin.yml` 中配置：

```yaml
name: MzLib
version: ${version}
main: mz.mzlib.minecraft.bukkit.MzLibBukkitPlugin
api-version: 1.13
depend: []
```

= 平台特定功能

不同平台可能提供不同的功能，使用平台检测来处理。

```java
public class PlatformSpecificFeature
{
    public void doSomething()
    {
        if (MinecraftPlatform.isFabric())
        {
            // Fabric 特定功能
            doFabricSpecific();
        }
        else if (MinecraftPlatform.isNeoForge())
        {
            // NeoForge 特定功能
            doNeoForgeSpecific();
        }
        else if (MinecraftPlatform.isBukkit())
        {
            // Bukkit 特定功能
            doBukkitSpecific();
        }
    }

    private void doFabricSpecific()
    {
        // Fabric 代码
    }

    private void doNeoForgeSpecific()
    {
        // NeoForge 代码
    }

    private void doBukkitSpecific()
    {
        // Bukkit 代码
    }
}
```

= 跨平台开发

使用统一的 API 开发跨平台插件。

```java
import mz.mzlib.minecraft.Player;
import mz.mzlib.minecraft.text.Text;
import mz.mzlib.minecraft.command.Command;

public class CrossPlatformPlugin extends MzModule
{
    public static CrossPlatformPlugin instance = new CrossPlatformPlugin();

    @Override
    public void onLoad()
    {
        // 注册命令（跨平台）
        this.register(new Command("hello")
            .setHandler(context ->
            {
                CommandSource source = context.source;
                source.sendMessage(Text.literal("Hello from MzLib!"));
            })
        );
    }
}
```

= 平台限制

不同平台可能有不同的限制：

/ #[= Fabric/NeoForge]:

    - 可以访问 Minecraft 的内部 API
    - 可以修改游戏行为
    - 需要使用映射（Yarn/Mojang）

/ #[= Bukkit/Spigot]:

    - 只能使用 Bukkit API
    - 不能访问内部 API
    - 需要使用 Spigot 映射

/ #[= Paper]:

    - 继承 Bukkit API
    - 提供额外的优化和功能
    - 支持 Folia 多线程

/ #[= Folia]:

    - 多线程平台
    - 需要使用区域化调度
    - 某些 API 不可用

= 最佳实践

/ #[= 使用统一 API]:

    尽量使用 MzLib 提供的统一 API，避免直接使用平台特定 API。

/ #[= 平台检测]:

    在需要平台特定功能时，使用平台检测。

/ #[= 版本兼容]:

    使用版本注解确保代码在不同版本上的兼容性。

/ #[= 测试]:

    在所有支持的平台上测试你的插件。

= 示例

完整的跨平台插件示例：

```java
import mz.mzlib.module.MzModule;
import mz.mzlib.minecraft.command.Command;
import mz.mzlib.minecraft.command.CommandContext;
import mz.mzlib.minecraft.command.CommandSource;
import mz.mzlib.minecraft.text.Text;
import mz.mzlib.minecraft.MinecraftPlatform;

public class MyPlugin extends MzModule
{
    public static MyPlugin instance = new MyPlugin();

    @Override
    public void onLoad()
    {
        // 注册命令
        this.register(new Command("platform")
            .setHandler(this::handlePlatform)
        );
    }

    private void handlePlatform(CommandContext context)
    {
        CommandSource source = context.source;

        // 检测平台
        if (MinecraftPlatform.isFabric())
        {
            source.sendMessage(Text.literal("运行在 Fabric 平台"));
        }
        else if (MinecraftPlatform.isNeoForge())
        {
            source.sendMessage(Text.literal("运行在 NeoForge 平台"));
        }
        else if (MinecraftPlatform.isBukkit())
        {
            source.sendMessage(Text.literal("运行在 Bukkit 平台"));
        }
        else if (MinecraftPlatform.isPaper())
        {
            source.sendMessage(Text.literal("运行在 Paper 平台"));
        }
        else if (MinecraftPlatform.isFolia())
        {
            source.sendMessage(Text.literal("运行在 Folia 平台"));
        }
        else
        {
            source.sendMessage(Text.literal("运行在未知平台"));
        }
    }
}
```

#cardInfo[
    平台适配是 MzLib 的核心特性之一，让你的代码可以在多个平台上运行。
    
    主要功能：
    - 统一的 API 接口
    - 平台检测
    - 平台特定代码
    - 跨平台开发
]