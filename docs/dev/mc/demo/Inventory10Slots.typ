#import "/lib/lib.typ": *;
#let title = [Inventory10Slots];
#show: template.with(title: title);



= 需求分析

众所周知，MC中的物品栏界面的格子数量总是9的倍数

但是你偶然间发现，合成界面拥有一个额外的成品槽

如果这个槽位也能存放物品，你将得到一个十个格子的物品栏

= 代码

该示例位于MzLibDemo的Inventory10Slots模块中

= 结构

首先我们需要一个类继承了UIWindow

```java
public static class UIInventory10Slots extends UIWindow
{
    public UIInventory10Slots()
    {
        super(UnionWindowType.CRAFTING, 10);
    }

    @Override
    public Text getTitle(EntityPlayer player)
    {
        return Text.literal("10个格子的物品栏");
    }
}
```

调用父类构造器，窗口类型是合成界面，物品栏的size是10

然后我们简单注册一个命令让玩家打开这个窗口，比较简单，这里不做说明

= 覆写quickMove

其实到这一步玩家已经可以往这个物品栏里存放东西了，但是在quickMove(按住shift点击物品)时会有点小问题

当玩家要快速移动背包中的物品时quickMove的默认行为是优先移动到0号槽位

但由于0号槽位是成品槽，客户端认为物品会优先移动到从1开始的槽位，导致同步时出现一点问题（物品会在错误的位置闪一下）

因此我们覆写quickMove方法，使得默认行为与客户端一致，即先考虑1\~8，最后再考虑0号位

```java
@Override
public ItemStack quickMove(WindowUIWindow window, EntityPlayer player, int index)
{
    int upperSize = window.getSlots().size()-36; // 其实就是10
    // 从物品栏移动到背包不用管，使用默认行为
    if(index<upperSize)
        return super.quickMove(window, player, index);
    WindowSlot slot = window.getSlot(index);
    // 不做更改时，返回ItemStack.empty()
    if(!slot.isPresent() || slot.getItemStack().isEmpty())
        return ItemStack.empty();
    ItemStack is = slot.getItemStack();
    ItemStack copy = is.copy();
    ItemStack result = ItemStack.empty();
    // 先考虑放进1~8
    if(window.placeIn(is, 1, upperSize, false))
        result = copy; // 如果更改，返回值是原始拷贝
    // 再考虑成品槽
    if(window.placeIn(is, 0, 1, false))
        result = copy;
    // markDirty
    if(!result.isEmpty())
    {
        if(is.isEmpty())
            slot.setItemStackByPlayer(ItemStack.empty());
        else
            slot.markDirty();
    }
    return result;
}
```

经过测试，将背包物品快速移动到物品栏确实没问题了，但从成品槽中快速拿出物品其实不是很同步

在客户端默认行为中，从成品槽快速拿出物品会倒序放入背包，即placeIn的最后一个参数应该设置成false

因此我们还要考虑从物品栏移动到背包的情况

```java
@Override
public ItemStack quickMove(WindowUIWindow window, EntityPlayer player, int index)
{
    WindowSlot slot = window.getSlot(index);
    if(!slot.isPresent() || slot.getItemStack().isEmpty())
        return ItemStack.empty();
    ItemStack is = slot.getItemStack();
    ItemStack copy = is.copy();
    ItemStack result = ItemStack.empty();
    int upperSize = window.getSlots().size()-36;
    if(index<upperSize) // 点击的是物品栏，移动到背包
    {
        if(window.placeIn(is, upperSize, window.getSlots().size(), index==0 /*仅当成品槽(0)时inverted为true*/))
            result = copy;
    }
    else // 否则从背包移动到物品栏
    {
        if(window.placeIn(is, 1, upperSize, false))
            result = copy;
        if(window.placeIn(is, 0, 1, false))
            result = copy;
    }
    if(!result.isEmpty())
    {
        if(is.isEmpty())
            slot.setItemStackByPlayer(ItemStack.empty());
        else
            slot.markDirty();
    }
    return result;
}
```

完成

= 总结

通过这个demo，你知道如何覆写`UIWindow#quickMove`，并应该将效果尽量做得与客户端同步