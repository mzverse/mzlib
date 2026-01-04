#import "/lib/lib.typ": *;
#let title = [核心类];
#show: template.with(title: title);

这里介绍 MzLibMinecraft 的核心类和接口。

= MzLibMinecraft

`MzLibMinecraft` 是 Minecraft 模块的主类，负责初始化和注册所有子功能。

```java
public class MzLibMinecraft extends MzModule
{
    public static MzLibMinecraft instance = new MzLib();

    public final String MOD_ID = "mzlib";

    public Config config;
    public Command command;

    @Override
    public void onLoad()
    {
        // 注册各种功能模块
        this.register(I18n.load(...));
        this.register(MinecraftI18n.instance);
        this.register(ModulePacketListener.instance);
        this.register(MinecraftEventModule.instance);
        // ...
    }
}
```

= MinecraftServer

`MinecraftServer` 是服务器的抽象接口，提供服务器级别的操作。

```java
// 获取服务器实例
MinecraftServer server = MinecraftServer.instance;

// 获取所有世界
Iterable<WorldServer> worlds = server.getWorlds();

// 获取所有玩家
List<EntityPlayer> players = server.getPlayers();

// 获取玩家管理器
PlayerManager playerManager = server.getPlayerManager();

// 获取命令管理器
CommandManager commandManager = server.getCommandManager();

// 获取配方管理器
RecipeManager recipeManager = server.getRecipeManager();

// 获取数据版本
int dataVersion = server.getDataVersion();

// 获取注册表
RegistryManagerV1602 registries = server.getRegistriesV1602();
```

= MinecraftPlatform

`MinecraftPlatform` 提供平台相关的信息和操作。

```java
// 获取平台实例
MinecraftPlatform platform = MinecraftPlatform.instance;

// 获取平台类型
PlatformType type = platform.getPlatformType();  // BUKKIT, FABRIC, NEOFORGE

// 获取 Minecraft 版本
int version = platform.getVersion();  // 1202, 1300, etc.

// 检查平台标签
boolean isBukkit = platform.hasTag(MinecraftPlatform.Tag.BUKKIT);
boolean isFabric = platform.hasTag(MinecraftPlatform.Tag.FABRIC);
boolean isNeoForge = platform.hasTag(MinecraftPlatform.Tag.NEOFORGE);

// 获取 MzLib JAR 文件
File jar = platform.getMzLibJar();

// 获取数据文件夹
File dataFolder = platform.getMzLibDataFolder();
```

= Player

`Player` 是玩家的持久化表示，不依赖于实体是否存在。

```java
// 通过 UUID 创建
Player player = Player.of(uuid);

// 获取 UUID
UUID uuid = player.getUuid();

// 获取实体（如果在线）
Option<EntityPlayer> entity = player.getEntity();

// 检查是否在线
boolean online = player.isOnline();

// 发送消息
player.sendMessage(Text.literal("Hello!"));
```

= PlayerManager

`PlayerManager` 管理所有在线玩家。

```java
// 获取玩家管理器
PlayerManager manager = MinecraftServer.instance.getPlayerManager();

// 获取所有在线玩家
List<EntityPlayer> players = manager.getPlayers();

// 通过 UUID 获取玩家
Option<EntityPlayer> player = manager.getPlayer(uuid);

// 通过名称获取玩家
Option<EntityPlayer> player = manager.getPlayerByName("Steve");

// 广播消息
manager.broadcast(Text.literal("Server message"));
```

= Identifier

`Identifier` 表示命名空间标识符，格式为 `namespace:path`。

```java
// 创建标识符
Identifier id = Identifier.of("minecraft:diamond");
Identifier id2 = Identifier.of("mod", "custom_item");

// 创建 Minecraft 命名空间的标识符
Identifier mcId = Identifier.minecraft("stone");

// 获取命名空间
String namespace = id.getNamespace();

// 获取路径
String path = id.getPath();

// 转换为字符串
String str = id.toString();  // "minecraft:diamond"
```

= VersionName

`VersionName` 用于标记版本特定的名称。

```java
// 使用注解标记版本
@WrapMinecraftMethod(@VersionName(name = "getCommandManager"))
CommandManager getCommandManager();

// 多版本名称
@WrapMinecraftMethod({
    @VersionName(name = "method_2971", end = 1400),
    @VersionName(name = "getCommandManager", begin = 1400)
})
CommandManager getCommandManager();
```

= VersionRange

`VersionRange` 用于标记版本范围。

```java
// 使用注解标记版本范围
@VersionRange(begin = 1300, end = 1400)
void methodV1300_1400();

// 从某个版本开始
@VersionRange(begin = 1600)
void methodV1600();

// 到某个版本结束
@VersionRange(end = 1200)
void methodV_1200();
```

= GlobalConstants

`GlobalConstants` 提供全局常量。

```java
// 获取版本信息
VersionInfo version = GlobalConstants.getMinecraftVersionV1400_1800();

// 获取数据版本
int dataVersion = version.getDataVersion().getNumber();

// 其他常量
// ...
```

= 使用示例

```java
public class ExampleModule extends MzModule
{
    @Override
    public void onLoad()
    {
        // 获取服务器信息
        MinecraftServer server = MinecraftServer.instance;
        System.out.println("Data version: " + server.getDataVersion());

        // 获取平台信息
        MinecraftPlatform platform = MinecraftPlatform.instance;
        System.out.println("Platform: " + platform.getPlatformType());
        System.out.println("Version: " + platform.getVersion());

        // 获取玩家管理器
        PlayerManager playerManager = server.getPlayerManager();
        List<EntityPlayer> players = playerManager.getPlayers();
        System.out.println("Online players: " + players.size());
    }
}
```

= 注意事项

- `MinecraftServer.instance` 可能为 `NothingMinecraftServer`，需要检查
- `Player` 是持久化表示，`EntityPlayer` 是实体表示
- 版本号使用整数表示，如 1202 表示 1.12.2
- 使用 `@VersionRange` 和 `@VersionName` 实现版本兼容