package mz.mzlib.minecraft.recipe;

import mz.mzlib.minecraft.Identifier;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.minecraft.item.ItemStack;
import mz.mzlib.minecraft.recipe.smelting.RecipeFurnace;
import mz.mzlib.minecraft.recipe.smelting.RecipeFurnaceV_1300;
import mz.mzlib.minecraft.recipe.smelting.SmeltingManagerV_1300;
import mz.mzlib.util.Option;
import mz.mzlib.util.RuntimeUtil;

import java.util.*;

@VersionRange(end = 1300)
public abstract class RegistrarRecipeVanillaV_1300 extends RegistrarRecipeVanilla
{
    public static RegistrarRecipeVanillaV_1300 instance;

    @Override
    protected void updateOriginal(RecipeManager manager)
    {
        this.updateOriginal(SmeltingManagerV_1300.getInstance());
    }
    protected void updateOriginal(SmeltingManagerV_1300 smeltingManager)
    {
        HashMap<RecipeType, Map<Identifier, Recipe>> result =
            this.originalRecipes == null ? new HashMap<>() : new HashMap<>(this.originalRecipes);
        HashMap<Identifier, Recipe> smeltingRecipes = new HashMap<>();
        Map<ItemStack, Float> xps = smeltingManager.getExperiences();
        for(Map.Entry<ItemStack, ItemStack> e : smeltingManager.getResults().entrySet())
        {
            RecipeFurnaceV_1300 recipe = (RecipeFurnaceV_1300) RecipeFurnace.builder()
                .ingredient(e.getKey())
                .result(e.getValue())
                .experience(Option.fromNullable(xps.get(e.getValue())).unwrapOrGet(() ->
                {
                    new IllegalStateException("Missing experience for " + e.getKey()).printStackTrace(System.err);
                    return 0.f;
                }))
                .build();
            Recipe last = smeltingRecipes.put(recipe.calcId(), recipe);
            if(last != null)
                System.err.println("Duplicate recipe: " + recipe.calcId() + " " + last);
        }
        result.put(RecipeType.FURNACE, Collections.unmodifiableMap(smeltingRecipes));
        this.originalRecipes = Collections.unmodifiableMap(result);
    }

    @Override
    public synchronized void flush(RecipeManager manager)
    {
        super.flush(manager);
        Map<Object, Object> results0 = new HashMap<>();
        Map<Object, Float> experiences0 = new HashMap<>();
        for(RecipeFurnaceV_1300 recipe : RuntimeUtil.<Collection<RecipeFurnaceV_1300>>cast(
            this.getEnabledRecipes().getOrDefault(RecipeType.FURNACE, Collections.emptyMap()).values()))
        {
            results0.put(recipe.getIngredient().getWrapped(), recipe.getResult().getWrapped());
            experiences0.put(recipe.getResult().getWrapped(), recipe.getExperience());
        }
        SmeltingManagerV_1300.getInstance().setExperiences0(experiences0);
        SmeltingManagerV_1300.getInstance().setResults0(results0);
    }
}
