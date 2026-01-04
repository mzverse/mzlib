#import "/lib/lib.typ": *;
#let title = [API 参考索引];
#show: template.with(title: title);

= API 参考索引

本文档提供了 MzLib-Minecraft 所有 API 的快速索引。

= 按字母顺序索引

/ #[= A]:
    - `AsyncFunction` - 异步函数
    - `Authlib` - 认证库
    - `ArgumentParser` - 参数解析器

/ #[= B]:
    - `BlockEntity` - 方块实体
    - `BlockState` - 方块状态
    - `Bukkit` - Bukkit 平台相关

/ #[= C]:
    - `Cache` - 缓存
    - `ClassCache` - 类缓存
    - `Command` - 命令
    - `CommandContext` - 命令上下文
    - `CommandManager` - 命令管理器
    - `CommandSource` - 命令源
    - `ComponentMapV2005` - 组件映射（1.20.5+）
    - `Config` - 配置
    - `Copyable` - 可复制
    - `DataHandler` - 数据处理器
    - `DataKey` - 数据键
    - `DataFixerV1300` - 数据修复器（1.13+）
    - `DataUpdaterV900_1300` - 数据更新器（1.9-1.13）
    - `DisplayEntity` - 显示实体
    - `DamageSource` - 伤害源
    - `DynamicV1300` - 动态值（1.13+）
    - `Either` - 二选一类型
    - `Editor` - 编辑器
    - `Entity` - 实体
    - `EntityLiving` - 生物实体
    - `EntityPlayer` - 玩家实体
    - `EntityType` - 实体类型
    - `EntityItem` - 物品实体
    - `EntityDataTracker` - 实体数据追踪器
    - `Event` - 事件
    - `EventListener` - 事件监听器
    - `EventPlayer` - 玩家事件
    - `EventEntity` - 实体事件
    - `EventServer` - 服务器事件
    - `Fabric` - Fabric 平台相关
    - `GameProfile` - 玩家配置文件
    - `GlobalConstants` - 全局常量
    - `Hand` - 手
    - `Identifier` - 命名空间标识符
    - `I18n` - 国际化
    - `Inventory` - 库存
    - `InventoryPlayer` - 玩家库存
    - `InventoryCrafting` - 合成台库存
    - `InventorySimple` - 简单库存
    - `IOUtil` - IO 工具
    - `Item` - 物品
    - `ItemStack` - 物品堆叠
    - `ItemType` - 物品类型
    - `Items` - 物品常量
    - `ItemPlayerHead` - 玩家头颅
    - `ItemWrittenBook` - 成书
    - `ItemTagsV1300` - 物品标签（1.13+）
    - `JsUtil` - JavaScript 工具
    - `JsonUtil` - JSON 工具
    - `LangEditor` - 语言编辑器
    - `Mappings` - 映射
    - `MappingsMerged` - 合并映射
    - `MinecraftI18n` - Minecraft 国际化
    - `MinecraftPlatform` - Minecraft 平台
    - `MinecraftServer` - Minecraft 服务器
    - `MzItem` - 自定义物品
    - `MzItemUsable` - 可使用的自定义物品
    - `MzLibMinecraft` - MzLib Minecraft 主类
    - `MzModule` - MzLib 模块
    - `NeoForge` - NeoForge 平台相关
    - `NbtByte` - NBT 字节
    - `NbtShort` - NBT 短整型
    - `NbtInt` - NBT 整型
    - `NbtLong` - NBT 长整型
    - `NbtFloat` - NBT 浮点型
    - `NbtDouble` - NBT 双精度浮点型
    - `NbtString` - NBT 字符串
    - `NbtCompound` - NBT 复合标签
    - `NbtList` - NBT 列表
    - `NbtByteArray` - NBT 字节数组
    - `NbtIntArray` - NBT 整型数组
    - `NbtLongArrayV1200` - NBT 长整型数组（1.12+）
    - `NbtElement` - NBT 元素
    - `NbtIo` - NBT IO
    - `NbtScanner` - NBT 扫描器
    - `Option` - Optional 类型
    - `Packet` - 数据包
    - `PacketHandler` - 数据包处理器
    - `PacketListener` - 数据包监听器
    - `PacketCallbacksV1901` - 数据包回调（1.19.1+）
    - `PacketBundle` - 数据包捆绑（1.19.4+）
    - `Permission` - 权限
    - `PermissionHelp` - 权限帮助
    - `Player` - 玩家
    - `PlayerManager` - 玩家管理器
    - `Plugin` - 插件
    - `PluginManager` - 插件管理器
    - `Priority` - 优先级
    - `Property` - 属性
    - `PropertyMap` - 属性映射
    - `RecipeManager` - 配方管理器
    - `RecipeCrafting` - 合成配方
    - `RecipeCraftingShaped` - 有序合成配方
    - `RecipeCraftingShapeless` - 无序合成配方
    - `Ingredient` - 配方材料
    - `Ref` - 引用
    - `RefWeak` - 弱引用
    - `RefStrong` - 强引用
    - `Result` - 结果类型
    - `Registrable` - 可注册
    - `RegistrarEventListener` - 注册事件监听器
    - `RegistrarEventClass` - 注册事件类
    - `RegistrarMzItem` - 自定义物品注册器
    - `SimpleTester` - 简单测试器
    - `Tester` - 测试器
    - `TesterContext` - 测试上下文
    - `Text` - 文本组件
    - `TextLiteral` - 字面文本
    - `TextTranslatable` - 可翻译文本
    - `TextScore` - 分数文本
    - `TextSelector` - 选择器文本
    - `TextKeybindV1200` - 按键绑定文本（1.12+）
    - `TextNbtV1400` - NBT 文本（1.14+）
    - `TextObjectV2109` - 对象文本（1.21+）
    - `TextStyle` - 文本样式
    - `TextColor` - 文本颜色
    - `TextClickEvent` - 文本点击事件
    - `TextHoverEvent` - 文本悬停事件
    - `UiStack` - UI 栈
    - `UiWrittenBook` - 成书 UI
    - `UiAbstractWindow` - 抽象窗口 UI
    - `UiWindowAnvil` - 铁砧 UI
    - `VanillaI18nV_1300` - 原版国际化（1.13+）
    - `VersionName` - 版本名称
    - `VersionRange` - 版本范围
    - `VersionRanges` - 版本范围集合
    - `Window` - 窗口
    - `World` - 世界
    - `WorldServer` - 服务端世界
    - `WrapperObject` - 包装对象
    - `WrapperFactory` - 包装器工厂
    - `WrapMinecraftClass` - 包装 Minecraft 类
    - `WrapMinecraftMethod` - 包装 Minecraft 方法
    - `WrapMinecraftFieldAccessor` - 包装 Minecraft 字段访问器

= 按功能分类索引

/ #[= 核心功能]:
    - #link("core")[核心类] - MzLibMinecraft, MinecraftServer, MinecraftPlatform, Player, PlayerManager
    - #link("module")[模块系统] - MzModule, IRegistrar, Registrable
    - #link("event")[事件系统] - Event, EventListener, EventPlayer, EventEntity, EventServer

/ #[= 物品与库存]:
    - #link("item/index")[物品系统] - Item, ItemStack, ItemType, Items
    - #link("item/item")[自定义数据] - 物品自定义数据
    - #link("item/player_head")[玩家头颅] - 玩家头颅物品
    - #link("inventory")[库存] - Inventory, InventoryPlayer, InventoryCrafting
    - #link("window")[窗口] - Window 系统
    - #link("recipe")[配方] - RecipeManager, RecipeCrafting, Ingredient

/ #[= 实体与玩家]:
    - #link("entity")[实体] - Entity, EntityLiving, EntityPlayer, EntityType
    - #link("entity")[实体数据] - EntityDataTracker, DisplayEntity
    - #link("core")[玩家] - Player, PlayerManager

/ #[= 命令与权限]:
    - #link("command")[命令] - Command, CommandManager, CommandSource, ArgumentParser
    - #link("permission")[权限] - Permission, PermissionHelp

/ #[= 数据处理]:
    - #link("nbt")[NBT] - NbtCompound, NbtList, NbtElement
    - #link("component")[组件] - ComponentMapV2005, ComponentKeyV2005 (1.20.5+)
    - #link("serialization")[序列化] - CodecV1600, DynamicV1300, JsonOpsV1300
    - #link("datafixer")[数据修复] - DataFixerV1300, DataUpdaterV900_1300

/ #[= 文本与显示]:
    - #link("text")[文本组件] - Text, TextLiteral, TextTranslatable, TextStyle

/ #[= 网络通信]:
    - #link("network_packet")[网络数据包] - Packet, PacketHandler, PacketListener

/ #[= 世界与方块]:
    - #link("world")[世界] - World, WorldServer
    - #link("block")[方块] - BlockState, BlockEntity

/ #[= 工具类]:
    - #link("i18n")[国际化] - I18nMinecraft, MinecraftI18n
    - #link("mappings")[映射] - Mappings, MappingsMerged
    - #link("authlib")[Authlib] - GameProfile, Property
    - #link("ui")[UI] - UiStack, UiWrittenBook, UiAbstractWindow
    - #link("mzitem")[自定义物品] - MzItem, MzItemUsable

/ #[= 版本兼容]:
    - #link("core")[版本] - VersionName, VersionRange, VersionRanges
    - #link("core")[全局常量] - GlobalConstants
    - #link("wrapper")[Wrapper] - WrapMinecraftClass, WrapMinecraftMethod

= 按版本分类

/ #[= 1.12.2 (1202)]:
    - TextKeybindV1200
    - NbtLongArrayV1200

/ #[= 1.13 (1300)]:
    - VanillaI18nV_1300
    - DynamicV1300
    - JsonOpsV1300
    - NbtOpsV1300
    - ItemTagsV1300
    - DataFixerV1300

/ #[= 1.14 (1400)]:
    - TextNbtV1400

/ #[= 1.16 (1600)]:
    - CodecV1600

/ #[= 1.17 (1700)]:
    - isOfV1700

/ #[= 1.19.3 (1903)]:
    - hasTagV1903

/ #[= 1.20.5 (2005)]:
    - ComponentMapV2005
    - ComponentKeyV2005
    - ComponentsAccessV2105

/ #[= 1.21 (2109)]:
    - TextObjectV2109