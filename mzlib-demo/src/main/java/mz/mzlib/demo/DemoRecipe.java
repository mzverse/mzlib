package mz.mzlib.demo;

import mz.mzlib.minecraft.Identifier;
import mz.mzlib.minecraft.MinecraftServer;
import mz.mzlib.minecraft.item.Item;
import mz.mzlib.minecraft.item.ItemStack;
import mz.mzlib.minecraft.mzitem.MzItemDebugStick;
import mz.mzlib.minecraft.mzitem.RegistrarMzItem;
import mz.mzlib.minecraft.recipe.Ingredient;
import mz.mzlib.minecraft.recipe.IngredientVanilla;
import mz.mzlib.minecraft.recipe.RegistrarRecipeVanilla;
import mz.mzlib.minecraft.recipe.crafting.RecipeCraftingShaped;
import mz.mzlib.minecraft.recipe.smelting.RecipeFurnace;
import mz.mzlib.module.MzModule;

public class DemoRecipe extends MzModule
{
    public static final DemoRecipe instance = new DemoRecipe();

    @Override
    public void onLoad()
    {
        this.register(RecipeCraftingShaped.builder()
            .id(Identifier.of("mzlibdemo:test"))
            .pattern(
                "AAA",
                "A A",
                "AAA"
            )
            .where('A', IngredientVanilla.of(ItemStack.newInstance(Item.fromId("stick"))))
            .finish()
            .result(ItemStack.builder().fromId("apple").build()).buildRegistration());
        this.register(RecipeCraftingShaped.builder()
            .id(Identifier.of("mzlibdemo:test1"))
            .pattern(
                "AAA",
                "ABA",
                "AAA"
            )
            .where('A', Ingredient.of(RegistrarMzItem.instance.newMzItem(MzItemDebugStick.class)))
            .where('B', Ingredient.builder().predicate(is -> !is.isEmpty()).examples(ItemStack.newInstance(Item.fromId("diamond"))).build())
            .finish()
            .result(ItemStack.builder().fromId("apple").build()).buildRegistration());
        this.register(RecipeFurnace.builder()
            .id(Identifier.of("mzlib:test_smelting"))
            .ingredient(ItemStack.newInstance(Item.fromId("stick")))
            .result(ItemStack.builder().fromId("apple").build())
            .experience(100.f)
            .buildRegistration());
        MinecraftServer.instance.schedule(
            () -> System.out.println(RegistrarRecipeVanilla.instance.getEnabledRecipes()));
    }
}
