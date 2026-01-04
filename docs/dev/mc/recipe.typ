#import "/lib/lib.typ": *;
#let title = [配方系统];
#show: template.with(title: title);

配方系统提供了配方的注册、查询和管理功能。

= RecipeManager

`RecipeManager` 管理所有配方。

```java
// 获取配方管理器
RecipeManager manager = MinecraftServer.instance.getRecipeManager();

// 获取所有配方
Collection<Recipe> recipes = manager.values();

// 通过 ID 获取配方
Option<Recipe> recipe = manager.get(Identifier.of("minecraft:diamond_sword"));

// 检查配方是否存在
boolean exists = manager.containsKey(id);

// 注册配方
manager.register(recipe);

// 移除配方
manager.remove(id);
```

= RecipeCrafting

`RecipeCrafting` 是合成配方。

```java
// 创建有序合成
RecipeCraftingShaped shaped = RecipeCraftingShaped.create(
    pattern,
    ingredients,
    result
);

// 创建无序合成
RecipeCraftingShapeless shapeless = RecipeCraftingShapeless.create(
    ingredients,
    result
);

// 获取配方 ID
Identifier id = recipe.getId();

// 获取配方结果
ItemStack result = recipe.getResult();

// 检查是否匹配
boolean matches = recipe.matches(inventory);

// 合成
ItemStack output = recipe.craft(inventory);
```

= Ingredient

`Ingredient` 表示配方材料。

```java
// 创建单个物品材料
Ingredient ingredient = Ingredient.ofItems(item);

// 创建多个物品材料
Ingredient ingredient = Ingredient.ofItems(item1, item2, item3);

// 创建标签材料
Ingredient ingredient = Ingredient.ofTag(tagKey);

// 创建空材料
Ingredient empty = Ingredient.empty();

// 检查是否匹配
boolean matches = ingredient.test(stack);

// 获取所有匹配物品
Collection<ItemStack> stacks = ingredient.getMatchingStacks();

// 检查是否为空
boolean isEmpty = ingredient.isEmpty();
```

= 使用示例

```java
public class RecipeExample
{
    // 注册自定义配方
    public void registerCustomRecipe()
    {
        RecipeManager manager = MinecraftServer.instance.getRecipeManager();

        // 有序合成
        RecipeCraftingShaped shaped = RecipeCraftingShaped.create(
            new String[]{" D ", " S ", " S "},  // 图案
            Map.of(
                'D', Ingredient.ofItems(Items.DIAMOND),
                'S', Ingredient.ofItems(Items.STICK)
            ),  // 材料
            ItemStack.builder()
                .fromId("minecraft:diamond_sword")
                .count(1)
                .build()  // 结果
        );

        manager.register(shaped);
    }

    // 检查玩家是否可以合成
    public boolean canCraft(EntityPlayer player, Recipe recipe)
    {
        InventoryPlayer inventory = player.getInventory();
        InventoryCrafting crafting = new InventoryCrafting(3, 3);

        // 从玩家库存获取材料
        // ...

        return recipe.matches(crafting);
    }

    // 获取所有合成配方
    public Collection<RecipeCrafting> getAllCraftingRecipes()
    {
        RecipeManager manager = MinecraftServer.instance.getRecipeManager();
        Collection<Recipe> all = manager.values();
        List<RecipeCrafting> crafting = new ArrayList<>();

        for (Recipe recipe : all)
        {
            if (recipe instanceof RecipeCrafting)
            {
                crafting.add((RecipeCrafting) recipe);
            }
        }

        return crafting;
    }
}
```

= 注意事项

- 配方 ID 必须唯一
- 配方需要在服务器启动时注册
- 使用 `Ingredient` 而不是直接使用 `ItemStack`
- 有序合成的图案必须匹配