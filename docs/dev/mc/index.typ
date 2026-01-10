#import "/lib/lib.typ": *;
#set raw(lang: "java");
#let title = [Minecraft开发文档];
#show: template.with(title: title);

这里是MzLib-Minecraft的开发文档

= 依赖mzlib-minecraft

如果你要写mzlib-minecraft的下游程序（附属插件），依赖它

对于初学者，直接下载-all.jar然后导入

高手，请使用gradle(kts)导入，在build.gradle.kts中添加：

== 中心仓库（推荐）

```kts
repositories {
    mavenCentral()
    maven {
        name = "CentralPortalSnapshots"
        url = uri("https://central.sonatype.com/repository/maven-snapshots/")
    }
    mavenLocal()
}
dependencies {
    compileOnly("org.mzverse:mzlib-minecraft:latest.release")
}
```

以上步骤依赖了最新版，如需依赖最新快照版请将`latest.release`改为`latest.integration`

如需依赖固定版本，请将`latest.release`替换为具体版本号，如`10.0.1-beta.17`

== GitHub Packages

如果不想使用中心仓库，可使用 GitHub Packages：

```kts
repositories {
    var actionGithub: MavenArtifactRepository.() -> Unit = {
        credentials {
            username = if (System.getenv("CI") != null)
                System.getenv("GITHUB_ACTOR")
            else
                System.getenv("GITHUB_USERNAME")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
    maven("https://maven.pkg.github.com/mzverse/mzlib", actionGithub)
}
dependencies {
    compileOnly("org.mzverse:mzlib-minecraft:latest.release")
}
```

确保你的环境变量中有`GITHUB_USERNAME`和`GITHUB_TOKEN`，并且token具有`read:packages`权限。

= 版本表示和名称约定

为了简单起见，我们使用一个整数表示一个MC版本，即第二位版本号乘100加上第三位版本号

若MC版本"1.x.y"，用整数表示为`x*100+y`。
例如"1.12.2"表示为`1202`，"1.14"表示为`1400`，"1.21.5"表示为`2105`

从MC版本"26.1"起，"x.y"表示为`x*100+y*10`，"x.y.z"表示为`x*100+y*10+z`。
例如"26.1"表示为`2610`，"26.1.1"表示为`2611`

如果一个元素只在特定的版本段生效，则在标识符后加上"v"和版本段，若有多个版本段，使用两个下划线隔开

一个版本段是一个左开右闭区间[a,b)，表示为`a_b`；[a,+∞)表示为a，(+∞,b)表示为`_b`

例如，`exampleV1300`表示这个元素从1.13开始有效，即支持$[1.13, +infinity)$；`exampleV_1400`表示在1.14之前有效，即支持$[0, 1.14)$；`exampleV1300_1400`表示从1.13开始有效，从1.14开始失效，即支持$[1.13, 1.14)$

例如，`exampleV_1300__1400_1600`表示在(-∞, 1.13) ∪ [1.14, 1.16)有效；`exampleV_1600__1903`表示1.16之前和从1.19.3开始都有效，在[1.16, 1.19.3)无效

= 文档导航

/ #[= 核心功能]:
    #link("core")[核心类] - MzLibMinecraft, MinecraftServer, MinecraftPlatform, Player, PlayerManager, Identifier, GlobalConstants, VersionName, VersionRange

/ #[= 物品系统]:
    #link("item/index")[物品系统] - Item, ItemStack, ItemType, Items, ItemPlayerHead, ItemWrittenBook, ItemTagsV1300
    #link("item/item")[自定义数据] - 物品自定义数据
    #link("item/player_head")[玩家头颅] - 玩家头颅物品

/ #[= 实体系统]:
    #link("entity")[实体] - Entity, EntityLiving, EntityPlayer, EntityType, EntityItem, EntityDataTracker, DamageSource, DisplayEntity

/ #[= 库存与窗口]:
    #link("inventory")[库存] - Inventory, InventoryPlayer, InventoryCrafting, InventorySimple
    #link("window")[窗口] - Window 系统
    #link("ui")[UI] - UiStack, UiWrittenBook, UiAbstractWindow, UiWindowAnvil

/ #[= 配方系统]:
    #link("recipe")[配方] - RecipeManager, RecipeCrafting, Ingredient

/ #[= 命令系统]:
    #link("command")[命令] - Command, CommandManager, CommandSource, ArgumentParser

/ #[= 权限系统]:
    #link("permission")[权限] - Permission, PermissionHelp, EventCheckPermission

/ #[= 事件系统]:
    #link("event")[事件] - MinecraftEventModule, EventPlayer, EventEntity, EventServer

/ #[= 数据处理]:
    #link("nbt")[NBT] - NbtCompound, NbtList, NbtElement
    #link("component")[组件] - ComponentMapV2005, ComponentKeyV2005 (1.20.5+)
    #link("serialization")[序列化] - CodecV1600, DynamicV1300, JsonOpsV1300
    #link("datafixer")[数据修复] - DataFixerV1300, DataUpdaterV900_1300

/ #[= 文本系统]:
    #link("text")[文本组件] - Text, TextLiteral, TextTranslatable, TextStyle, TextColor

/ #[= 网络系统]:
    #link("network_packet")[网络数据包] - Packet, PacketHandler, PacketListener, C2S/S2C 数据包

/ #[= 其他系统]:
    #link("i18n")[国际化] - I18nMinecraft, MinecraftI18n, VanillaI18nV_1300
    #link("mappings")[映射] - Mappings, MappingsMerged, MinecraftMappingsFetcher
    #link("authlib")[Authlib] - GameProfile, Property, PropertyMap
    #link("block")[方块] - BlockState, BlockEntity
    #link("world")[世界] - World, WorldServer
    #link("mzitem")[自定义物品] - MzItem, MzItemUsable, RegistrarMzItem
    #link("wrapper")[Wrapper] - WrapMinecraftClass, WrapMinecraftMethod, WrapMinecraftFieldAccessor

/ #[= 教程]:
    #link("tutorial/index")[教程] - 从基础到进阶的完整教程

/ #[= 示例]:
    #link("demo/index")[示例] - 代码示例和演示