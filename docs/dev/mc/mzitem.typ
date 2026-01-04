#import "/lib/lib.typ": *;
#let title = [自定义物品];
#show: template.with(title: title);

MzLib 提供了自定义物品系统。

= MzItem

`MzItem` 是自定义物品基类。

```java
public class MyCustomItem extends MzItem
{
    @Override
    public Identifier getId()
    {
        return Identifier.of("myplugin", "custom_item");
    }

    @Override
    public String getTranslationKey()
    {
        return "item.myplugin.custom_item";
    }
}
```

= MzItemUsable

`MzItemUsable` 是可使用的自定义物品。

```java
public class MyUsableItem extends MzItemUsable
{
    @Override
    public ActionResult use(World world, EntityPlayer player, Hand hand)
    {
        // 使用物品的逻辑
        player.sendMessage(Text.literal("物品被使用了！"));
        return ActionResult.SUCCESS;
    }
}
```

= RegistrarMzItem

`RegistrarMzItem` 是自定义物品注册器。

```java
@RegistrarMzItem
public class MyItems
{
    public static MyCustomItem CUSTOM_ITEM = new MyCustomItem();
    public static MyUsableItem USABLE_ITEM = new MyUsableItem();
}
```

= 使用示例

```java
public class CustomItemExample
{
    // 创建自定义物品堆叠
    public ItemStack createCustomItem()
    {
        ItemStack stack = ItemStack.newInstance(MyItems.CUSTOM_ITEM);
        stack.setCount(1);
        return stack;
    }

    // 给玩家自定义物品
    public void giveCustomItem(EntityPlayer player)
    {
        ItemStack stack = createCustomItem();
        player.getInventory().addItem(stack);
    }
}
```

= 注意事项

- 自定义物品需要注册
- 使用注册器管理所有自定义物品
- 物品 ID 需要唯一