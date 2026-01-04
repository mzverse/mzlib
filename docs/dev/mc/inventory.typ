#import "/lib/lib.typ": *;
#let title = [库存系统];
#show: template.with(title: title);

库存系统提供了对各种库存的操作和管理功能。

= Inventory

`Inventory` 是库存的基类。

```java
// 获取库存大小
int size = inventory.size();

// 获取槽位物品
ItemStack stack = inventory.getStack(slot);

// 设置槽位物品
inventory.setStack(slot, stack);

// 清空库存
inventory.clear();

// 移除物品
inventory.removeStack(slot);

// 标记库存为脏
inventory.markDirty();
```

= InventoryPlayer

`InventoryPlayer` 是玩家库存。

```java
// 获取玩家库存
InventoryPlayer inventory = player.getInventory();

// 获取主手物品
ItemStack mainHand = inventory.getMainHandStack();

// 设置主手物品
inventory.setMainHandStack(stack);

// 获取副手物品
ItemStack offHand = inventory.getOffHandStack();

// 设置副手物品
inventory.setOffHandStack(stack);

// 获取盔甲槽
ItemStack helmet = inventory.getArmor(3);  // 头盔
ItemStack chestplate = inventory.getArmor(2);  // 胸甲
ItemStack leggings = inventory.getArmor(1);  // 护腿
ItemStack boots = inventory.getArmor(0);  // 靴子

// 获取物品栏（0-35）
ItemStack hotbar = inventory.getSlot(0);  // 快捷栏第1格
ItemStack inventorySlot = inventory.getSlot(9);  // 物品栏第1格

// 添加物品
boolean added = inventory.offerOrDrop(stack);

// 检查是否为玩家库存
boolean isPlayer = inventory.isPlayerInventory();
```

= InventoryCrafting

`InventoryCrafting` 是合成台库存。

```java
// 创建合成台库存
InventoryCrafting crafting = new InventoryCrafting(width, height);

// 获取合成结果
ItemStack result = crafting.getCraftingResult();

// 设置合成结果
crafting.setCraftingResult(result);

// 检查是否可以合成
boolean canCraft = crafting.canCraft(recipe);

// 执行合成
crafting.craft(recipe);
```

= InventorySimple

`InventorySimple` 是简单的库存实现。

```java
// 创建简单库存
InventorySimple inventory = new InventorySimple(size);

// 获取库存大小
int size = inventory.size();

// 获取和设置物品
ItemStack stack = inventory.getStack(slot);
inventory.setStack(slot, stack);
```

= 使用示例

```java
public class InventoryExample
{
    // 给玩家物品
    public void giveItem(EntityPlayer player, ItemStack stack)
    {
        InventoryPlayer inventory = player.getInventory();
        inventory.offerOrDrop(stack);
    }

    // 移除玩家物品
    public void removeItem(EntityPlayer player, ItemStack stack, int count)
    {
        InventoryPlayer inventory = player.getInventory();
        for (int i = 0; i < inventory.size() && count > 0; i++)
        {
            ItemStack slotStack = inventory.getStack(i);
            if (ItemStack.isStackable(slotStack, stack))
            {
                int toRemove = Math.min(count, slotStack.getCount());
                slotStack.shrink(toRemove);
                count -= toRemove;
                if (slotStack.isEmpty())
                {
                    inventory.removeStack(i);
                }
            }
        }
    }

    // 检查玩家是否有足够物品
    public boolean hasItem(EntityPlayer player, ItemStack stack, int count)
    {
        InventoryPlayer inventory = player.getInventory();
        int total = 0;
        for (int i = 0; i < inventory.size(); i++)
        {
            ItemStack slotStack = inventory.getStack(i);
            if (ItemStack.isStackable(slotStack, stack))
            {
                total += slotStack.getCount();
            }
        }
        return total >= count;
    }

    // 创建合成配方
    public boolean canCraft(RecipeCrafting recipe, InventoryCrafting crafting)
    {
        return crafting.canCraft(recipe);
    }
}
```

= 注意事项

- 库存索引从 0 开始
- 玩家库存：0-8 快捷栏，9-35 物品栏，36-39 盔甲，40 副手
- 修改库存后记得调用 `markDirty()`
- 使用 `offerOrDrop()` 安全地添加物品