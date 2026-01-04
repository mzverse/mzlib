#import "/lib/lib.typ": *;
#let title = [窗口];
#show: template.with(title: title);



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

== 窗口类型

Minecraft提供了多种窗口类型，通过`UnionWindowType`枚举访问

=== 常用窗口类型

```java
// 箱子界面
UnionWindowType.GENERIC_9x1  // 9槽
UnionWindowType.GENERIC_9x2  // 18槽
UnionWindowType.GENERIC_9x3  // 27槽
UnionWindowType.GENERIC_9x4  // 36槽
UnionWindowType.GENERIC_9x5  // 45槽
UnionWindowType.GENERIC_9x6  // 54槽

// 熔炉
UnionWindowType.FURNACE

// 工作台
UnionWindowType.CRAFTING

// 附魔台
UnionWindowType.ENCHANTMENT

// 酿造台
UnionWindowType.BREWING_STAND

// 末影箱
UnionWindowType.GENERIC_3x3

// 铁砧
UnionWindowType.ANVIL

// 信标
UnionWindowType.BEACON

// 染料台
UnionWindowType.DYE

// 羊毛床
UnionWindowType.BED

// 末影水晶
UnionWindowType.END_CRYSTAL
```

=== 获取窗口类型

```java
WindowTypeV1400 type = UnionWindowType.GENERIC_9x5.typeV1400;
```

== 槽位索引

窗口中的槽位有固定的索引范围，了解这些索引对于正确操作窗口至关重要

=== 玩家背包槽位

- `0-8`：快捷栏
- `9-35`：主背包
- `36-39`：盔甲槽（从上到下：头盔、胸甲、护腿、靴子）
- `40`：副手槽

=== 箱子窗口槽位

- `0-53`：箱子槽位（取决于箱子大小）
- `54-62`：玩家快捷栏
- `63-89`：玩家主背包
- `90-93`：玩家盔甲槽
- `94`：玩家副手槽

=== 获取窗口大小

```java
int size = window.getSlots().size();
```

== 窗口事件

可以通过监听窗口事件来响应玩家的操作

=== 监听窗口打开

```java
this.register(new PacketListener<>(PacketS2cOpenWindow.FACTORY, packetEvent->
{
    Window window = packetEvent.getPacket().getWindow();
    Text title = window.getTitle();
    // 处理窗口打开事件
}));
```

=== 监听窗口关闭

```java
this.register(new PacketListener<>(PacketC2sCloseWindow.FACTORY, packetEvent->
{
    int syncId = packetEvent.getPacket().getSyncId();
    // 处理窗口关闭事件
}));
```

=== 监听槽位点击

```java
this.register(new PacketListener<>(PacketC2sClickWindow.FACTORY, packetEvent->
{
    int syncId = packetEvent.getPacket().getSyncId();
    int slotIndex = packetEvent.getPacket().getSlotIndex();
    int button = packetEvent.getPacket().getButton();
    // 处理槽位点击事件
}));
```

== 窗口同步机制

窗口同步确保客户端和服务端的物品状态一致

=== 1.17前的同步机制

大致规则为action包中有`onAction`方法的返回值（一个物品），若与服务端的返回值不一致则更新

```java
@Override
public ItemStack onAction(EntityPlayer player, int slotIndex, ItemStack itemStack, WindowAction action)
{
    // 返回服务端认为的物品状态
    return itemStack;
}
```

=== 1.17+的同步机制

玩家发送的action包中有客户端认为的更改列表（修改后的物品，以及cursor），若服务端发现不一致则发送对应更新包

1.17.1开始包中增加了`revision`字段，用于跟踪版本

=== 1.21.5+的同步机制

action包中仅包含哈希对象而非完整的物品，哈希相等则服务端认为相等，这大大减少了网络传输量

```java
// 1.21.5+的action处理
@Override
public void onActionV2105(EntityPlayer player, int slotIndex, short slotIndexV2105, byte data, WindowAction action)
{
    // slotIndex类型改为short
    // data类型改为byte
    // 使用哈希比较而非完整物品比较
}
```

== 已知问题

=== 鼠标位置问题

在版本[1.14, 1.17)，打开一个窗口始终会使鼠标移动到客户端中央，即使鼠标未被锁定

此为服务端特性导致，我们暂未修复

高版本似乎是Paper修复的此问题，Fabric中仍保持原状。待验证

=== 版本兼容性

自1.21.5起：

- `slotIndex`类型由`int`改为`short`
- `data`类型由`int`改为`byte`

在编写跨版本代码时需要注意这些类型变化

== 高级用法

=== 自定义窗口标题

```java
Text title = Text.literal("我的箱子")
    .setColor(TextColor.GOLD)
    .setBold(true);

WindowFactory windowFactory = WindowFactorySimple.chest(
    title,
    inventory,
    5,
    window->
    {
        // 可以在这里进行额外的窗口配置
    }
);
```

=== 动态更新窗口内容

```java
// 定时更新窗口中的物品
MinecraftServer.instance.schedule(()->
{
    for(int i = 0; i < inventory.getSize(); i++)
    {
        ItemStack newItem = createDynamicItem(i);
        inventory.setItemStack(i, newItem);
    }
    // 通知玩家更新窗口
    player.updateWindowSlot(window, i, newItem);
}, new SleepTicks(20));
```

=== 窗口间物品转移

```java
// 将物品从一个窗口转移到另一个窗口
public void transferItem(EntityPlayer player, Window fromWindow, Window toWindow, int fromSlot, int toSlot)
{
    ItemStack item = fromWindow.getSlot(fromSlot).getItemStack();
    if(item.isEmpty())
        return;
    
    // 检查目标槽位是否可以放置
    if(toWindow.getSlot(toSlot).canPlace(item))
    {
        fromWindow.getSlot(fromSlot).setCanTake(true);
        toWindow.getSlot(toSlot).setCanPlace(true);
        
        // 执行转移
        fromWindow.getSlot(fromSlot).setItemStack(ItemStack.empty());
        toWindow.getSlot(toSlot).setItemStack(item);
    }
}
```

=== 窗口验证

```java
// 验证窗口状态
public boolean validateWindow(EntityPlayer player, Window window)
{
    // 检查窗口是否仍然打开
    if(player.getOpenWindow().isNone())
        return false;
    
    // 检查窗口ID是否匹配
    if(player.getOpenWindow().unwrap().getSyncId() != window.getSyncId())
        return false;
    
    // 检查窗口类型是否匹配
    if(player.getOpenWindow().unwrap().getType() != window.getType())
        return false;
    
    return true;
}
```

== 性能优化

=== 减少窗口更新频率

```java
// 使用防抖机制减少更新
private long lastUpdateTime = 0;
private static final long UPDATE_INTERVAL = 5; // ticks

public void updateWindowIfNeeded(Window window)
{
    long currentTime = MinecraftServer.instance.getTicks();
    if(currentTime - lastUpdateTime >= UPDATE_INTERVAL)
    {
        // 执行更新
        lastUpdateTime = currentTime;
    }
}
```

=== 批量更新槽位

```java
// 批量更新多个槽位
public void batchUpdateSlots(EntityPlayer player, Window window, Map<Integer, ItemStack> updates)
{
    for(Map.Entry<Integer, ItemStack> entry : updates.entrySet())
    {
        int slot = entry.getKey();
        ItemStack item = entry.getValue();
        window.getSlot(slot).setItemStack(item);
    }
    // 单次通知所有更新
    player.updateWindowSlots(window, updates);
}
```

#cardTip[
  窗口操作通常在主线程执行，避免在异步线程中直接操作窗口
]

#cardAttention[
  自定义窗口时，务必正确处理槽位的`canPlace`和`canTake`方法，否则可能导致物品丢失或重复
]
