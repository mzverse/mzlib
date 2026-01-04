#import "/lib/lib.typ": *;
#let title = [方块系统];
#show: template.with(title: title);

方块系统提供了对方块和方块实体的操作功能。

= BlockState

`BlockState` 表示方块状态。

```java
// 获取方块 ID
Identifier id = blockState.getId();

// 获取方块属性
Map<String, String> properties = blockState.getProperties();

// 检查属性
boolean hasProperty = blockState.hasProperty("facing");

// 获取属性值
String value = blockState.getProperty("facing");

// 设置属性
BlockState newState = blockState.withProperty("facing", "north");

// 获取方块类型
Block block = blockState.getBlock();
```

= BlockEntity

`BlockEntity` 是方块实体。

```java
// 获取方块实体类型
Identifier type = blockEntity.getType();

// 获取位置
int x = blockEntity.getPos().getX();
int y = blockEntity.getPos().getY();
int z = blockEntity.getPos().getZ();

// 获取世界
World world = blockEntity.getWorld();

// 标记为脏
blockEntity.markDirty();

// 移除方块实体
blockEntity.remove();
```

= BlockEntityChest

`BlockEntityChest` 是箱子方块实体。

```java
// 获取箱子库存
Inventory chestInventory = chest.getInventory();

// 获取查看者数量
int viewerCount = chest.getViewerCount();

// 检查是否被打开
boolean isOpen = chest.isOpen();
```

= 注意事项

- 方块状态包含方块类型和属性
- 方块实体有额外数据（如箱子内容）
- 修改后需要标记为脏