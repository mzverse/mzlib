package mz.mzlib.demo;

import mz.mzlib.minecraft.Identifier;
import mz.mzlib.minecraft.MinecraftServer;
import mz.mzlib.minecraft.item.Item;
import mz.mzlib.minecraft.item.ItemStack;
import mz.mzlib.minecraft.recipe.IngredientVanilla;
import mz.mzlib.minecraft.recipe.RegistrarRecipeVanilla;
import mz.mzlib.minecraft.recipe.crafting.RecipeCraftingShaped;
import mz.mzlib.minecraft.recipe.smelting.RecipeFurnace;
import mz.mzlib.module.MzModule;
import mz.mzlib.util.Option;

import java.util.Collections;

public class DemoRecipe extends MzModule
{
    public static final DemoRecipe instance = new DemoRecipe();

    @Override
    public void onLoad()
    {
        this.register(RecipeCraftingShaped.builder()
            .id(Identifier.of("mzlib:test"))
            .width(1).height(1).ingredients(
                Collections.singletonList(
                    Option.some(IngredientVanilla.of(ItemStack.newInstance(Item.fromId("stick"))))))
            .result(ItemStack.builder().fromId("apple").build()).buildVanillaRegistration());
        this.register(RecipeCraftingShaped.builder()
            .id(Identifier.of("mzlib:test11"))
            .width(1).height(1).ingredients(
                Collections.singletonList(
                    Option.some(is -> !is.isEmpty())))
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
