#import "/lib/lib.typ": *;
#let title = [物品系统];
#show: template.with(title: title);

物品系统提供了完整的物品操作和管理功能。

= Item

`Item` 是物品的基本接口，表示一种物品类型。

```java
// 获取物品 ID
Identifier id = item.getId();

// 获取翻译键
String key = item.getTranslationKeyV_2102(itemStack);

// 获取物品名称
Text name = item.getNameV1300(itemStack);

// 获取最大堆叠数
int maxCount = item.getMaxCount();

// 检查是否可堆叠
boolean stackable = item.isStackable();

// 检查是否有标签
boolean hasTag = item.hasTagV1903(tagKey);

// 检查物品类型
boolean isOf = item.isOfV1700(otherItem);
```

= ItemStack

`ItemStack` 表示物品堆叠，包含物品类型和数量。

= 创建 ItemStack

```java
// 创建空物品堆叠
ItemStack empty = ItemStack.EMPTY;

// 从物品创建
ItemStack stack = ItemStack.newInstance(item);
ItemStack stack = ItemStack.newInstance(item, 64);

// 从物品类型创建
ItemStack stack = ItemType.of("minecraft:diamond").toItemStack(1);

// 使用构建器
ItemStack stack = ItemStack.builder()
    .fromId("minecraft:diamond_sword")
    .count(1)
    .customName(Text.literal("传奇之剑"))
    .build();

// 从现有物品复制
ItemStack copy = ItemStack.builder(stack).build();
```

= 基本操作

```java
ItemStack stack = ...;

// 检查是否为空
boolean empty = stack.isEmpty();

// 获取物品
Item item = stack.getItem();

// 获取 ID
Identifier id = stack.getId();

// 获取数量
int count = stack.getCount();

// 设置数量
stack.setCount(64);

// 增加数量
stack.grow(10);

// 减少数量
stack.shrink(5);

// 分割物品
ItemStack split = stack.split(10);

// 复制
ItemStack copy = stack.copy();
ItemStack copy = stack.copy(32);

// 获取最大堆叠数
int maxStack = stack.getMaxStackCount();

// 获取名称
Text name = stack.getName();

// 检查可堆叠
boolean stackable = stack.isStackable();
```

= NBT 操作

```java
ItemStack stack = ...;

// 获取 NBT（1.20.5 之前）
Option<NbtCompound> tag = stack.getTagV_2005();

// 获取或创建 NBT
NbtCompound tag = stack.tagV_2005();

// 设置 NBT
stack.setTagV_2005(Option.some(tag));

// 读取 NBT
Option<String> string = stack.tagV_2005().getString("key");
Option<Integer> integer = stack.tagV_2005().getInt("key");
```

= 组件操作（1.20.5+）

```java
ItemStack stack = ...;

// 获取组件映射
ComponentMapDefaultedV2005 components = stack.getComponentsV2005();

// 设置组件
stack.setComponentV2005(key, value);

// 获取组件
Option<T> component = components.get(key);
```

= 序列化

```java
ItemStack stack = ...;

// 编码为 NBT
Result<Option<NbtCompound>, String> encoded = stack.encode();

// 从 NBT 解码
Result<Option<ItemStack>, String> decoded = ItemStack.decode(nbt);

// 升级旧版本数据
NbtCompound upgraded = ItemStack.upgrade(nbt, fromVersion);
```

= 版本兼容

```java
// 获取耐久度（1.13 之前）
int damage = stack.getDamageV_1300();
stack.setDamageV_1300(5);

// 检查物品类型（1.17+）
boolean isOf = stack.isOfV1700(item);

// 检查标签（1.19.3+）
boolean hasTag = stack.hasTagV1903(tagKey);
```

= ItemType

`ItemType` 表示物品的类型，用于标识和分类物品。

```java
// 从 ItemStack 创建
ItemType type = new ItemType(itemStack);

// 从 ID 创建
ItemType type = ItemType.of("minecraft:diamond");

// 转换为 ItemStack
ItemStack stack = type.toItemStack(1);

// 获取 ID
Identifier id = type.getId();

// 检查是否相等
boolean equals = type.equals(otherType);
boolean equals = type.equals("minecraft:diamond");
```

= Items

`Items` 提供了常用物品的常量。

```java
// 获取物品
Item diamond = Items.DIAMOND;
Item sword = Items.DIAMOND_SWORD;

// 通过 ID 获取
Item item = Items.byId("minecraft:diamond");

// 检查是否存在
boolean exists = Items.byId("minecraft:custom") != null;
```

= ItemPlayerHead

`ItemPlayerHead` 是玩家头颅物品。

```java
// 创建玩家头颅
ItemStack head = ItemStack.builder()
    .fromId("minecraft:player_head")
    .playerHead()
    .texturesUrl(uuid, textureUrl)
    .build();

// 使用 GameProfile
ItemStack head = ItemStack.builder()
    .fromId("minecraft:player_head")
    .playerHead()
    .gameProfile(GameProfile.Description.texturesUrl(name, uuid, texture))
    .build();
```

= ItemWrittenBook

`ItemWrittenBook` 是成书物品。

```java
// 创建成书
ItemStack book = ItemStack.builder()
    .fromId("minecraft:written_book")
    .customName(Text.literal("我的书"))
    .build();

// 设置书的内容（使用 NBT）
book.tagV_2005().put("pages", pages);
book.tagV_2005().put("title", "标题");
book.tagV_2005().put("author", "作者");
```

= ItemTagsV1300

`ItemTagsV1300` 提供了物品标签功能（1.13+）。

```java
// 创建标签键
TagKeyV1903<Item> tagKey = TagKeyV1900.of(Registry.ITEM, Identifier.minecraft("logs"));

// 检查物品是否有标签
boolean hasTag = itemStack.hasTagV1903(tagKey);

// 获取所有标签
// ...
```

= 使用示例

```java
public class ItemExample
{
    // 创建自定义物品
    public ItemStack createCustomSword()
    {
        return ItemStack.builder()
            .fromId("minecraft:diamond_sword")
            .count(1)
            .customName(Text.literal("神圣之剑").setColor(TextColor.GOLD))
            .lore()
                .line(Text.literal("一把传说中的武器").setColor(TextColor.AQUA))
                .line(Text.literal("攻击力: 10").setColor(TextColor.RED))
                .finish()
            .build();
    }

    // 给物品添加自定义数据
    public void addCustomData(ItemStack stack, String key, String value)
    {
        for (NbtCompound customData : Item.CUSTOM_DATA.revise(stack))
        {
            customData.put(key, value);
        }
    }

    // 读取自定义数据
    public Option<String> getCustomData(ItemStack stack, String key)
    {
        return Item.CUSTOM_DATA.get(stack).flatMap(nbt -> nbt.getString(key));
    }

    // 克隆并修改物品
    public ItemStack cloneAndModify(ItemStack original)
    {
        return ItemStack.builder(original)
            .count(original.getCount() * 2)
            .build();
    }
}
```

= 注意事项

- ItemStack 是可变的，使用 `copy()` 创建副本
- 1.20.5+ 使用组件系统，之前版本使用 NBT
- 使用 `revise` 修改数据，不要直接修改 `get` 返回的数据
- 版本兼容使用 `@VersionRange` 注解
- 序列化时注意数据版本