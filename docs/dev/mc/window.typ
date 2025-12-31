#import "./../../lib/lib.typ": *

#let title = [窗口]

#show: template.with(title: title)



在此之前，请确保你已经学习了#link("./../core/compound")[Compound类]

在Bukkit中，一个物品栏可以被玩家直接打开，但在原版中并非如此

= 窗口

玩家实际上打开的是一个窗口，你也可以管它叫物品栏界面，Mojang叫它菜单，Fabric(yarn)叫它屏幕，Spigot叫它容器

窗口是玩家可以访问的界面，玩家通过窗口操作物品栏

窗口中有若干个槽位(WindowSlot)，槽位一般对应物品栏中的一个索引，你也可以自定义它的行为

== 创建窗口

例如我们可以创建一个箱子界面，也就是WindowChest的实例

```java
Inventory inventory=InventorySimple.newInstance(9*5);
Window window=WindowChest.newInstance(UnionWindowType.GENERIC_9x5.typeV1400, syncId, player.getInventory(), inventory, 5);
```

`WindowTypeV1400`表示从1.14开始的窗口类型，可以通过`UnionWindowType#typeV1400`得到

其中`UnionWindowType.GENERIC_9x5`表示一个9*5的箱子界面

现在你发现你没有`syncId`来创建窗口，但你先别急

== 打开窗口

即使你创建了一个窗口，你也不能让玩家直接打开它，`AbstractEntityPlayer#openWindow`的参数是`WindowFactory`，你需要提供`WindowFactory`实例来创建这个窗口

窗口的标题由`WindowFactory`提供

虽然这个设计可能很愚蠢，但Mojang代码就是这样写的（

因此我们提供了一个`WindowFactorySimple`让你可以很容易创建`WindowFactory`的实例

注意：在1.14之前，窗口的类型id由WindowFactory提供；从1.14开始，窗口的类型由Window提供

```java
Inventory inventory=InventorySimple.newInstance(9*5);
WindowFactory windowFactory=WindowFactorySimple.newInstance(UnionWindowType.GENERIC_9x5.typeV1400, Text.literal("标题"), (syncId, inventoryPlayer)->
{
    // 在这里创建窗口
    return WindowChest.newInstance(UnionWindowType.GENERIC_9x5.typeV1400, syncId, inventoryPlayer, inventory, 5);
});
// 让玩家打开WindowFactory
player.openWindow(windowFactory);
```

我们将箱子界面进行了简单封装，因此上面的代码可以简化为

```java
Inventory inventory=InventorySimple.newInstance(9*5);
WindowFactory windowFactory=WindowFactorySimple.chest(Text.literal("标题"), inventory, 5, window->
{
    // 可以对窗口进行设置
});
player.openWindow(windowFactory);
```

== 设置槽位

我们可以设置某个槽位的行为，它是`WindowSlot`的实例

我们先以WindowSlotButton为例，它的canPlace和canTake始终返回false

```java
Inventory inventory=InventorySimple.newInstance(9*5);
// 设置0号物品
inventory.setItemStack(0, new ItemStackBuilder("minecraft:stick").build());
WindowFactory windowFactory=WindowFactorySimple.chest(Text.literal("标题"), inventory, 5, window->
{
    // 设置0号槽位
    window.setSlot(0, WindowSlotButton.newInstance(inventory, 0));
});
player.openWindow(windowFactory);
```

这样玩家就不能在0号槽位放入或拿出物品

== 自定义WindowSlot

你已经迫不及待想要实现自己的WindowSlot了，它是一个封装类，我们需要用Compound类继承

```java
// 必须加上这个注解
@Compound
public interface WindowSlotButton extends WindowSlot
{
    // 作为一个WrapperObject的子类，应有creator
    @WrapperCreator
    static WindowSlotButton create(Object wrapped)
    {
        return WrapperObject.create(WindowSlotButton.class, wrapped);
    }

    /**
     * 构造器会直接原封不同继承
     * 对其包装即可
     */
    @WrapConstructor
    WindowSlot staticNewInstance(Inventory inventory, int index, int x, int y);
    static WindowSlot newInstance(Inventory inventory, int index)
    {
        return create(null).staticNewInstance(inventory, index, 0, 0);
    }

    /**
     * 继承一个方法需要加@CompoundOverride注解
     * parent表示这个方法所在的包装类
     * method表示对应的包装方法
     * 参数和返回值类型必须与被继承方法完全一致
     */
    @Override
    @CompoundOverride(parent=WindowSlot.class, method="canPlace")
    default boolean canPlace(ItemStack itemStack)
    {
        return false;
    }

    @Override
    @CompoundOverride(parent=WindowSlot.class, method="canTake")
    default boolean canTake(AbstractEntityPlayer player)
    {
        return false;
    }
}
```

= 行动（操作）

玩家对窗口的操作称为`WindowAction`

== `slotIndex`

操作时鼠标所在的槽位索引

== `data`

操作的额外参数

= 内部细节

== TODO

在版本[1.14, 1.17)，打开一个窗口始终会使鼠标移动到客户端中央，即使鼠标未被锁定

此为服务端特性导致，我们暂未修复

高版本似乎是Paper修复的此问题，Fabric中仍保持原状。待验证

== action

自1.21.5起：

- `slotIndex`类型由`int`改为`short`

- `data`类型由`int`改为`byte`

== 同步

1.17前的同步机制意义不明，大致规则为action包中有onAction方法的返回值（一个物品），若与服务端的返回值不一致则更新

从1.17开始，玩家发送的action包中有客户端认为的更改列表（修改后的物品，以及cursor），若服务端发现不一致则发送对应更新包

1.17.1开始包中增加了revision字段，用处应该不大

1.21.5起，action包中仅包含哈希对象而非完整的物品，哈希相等则服务端认为相等
