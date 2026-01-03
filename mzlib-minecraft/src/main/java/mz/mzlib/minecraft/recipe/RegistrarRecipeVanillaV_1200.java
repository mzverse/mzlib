package mz.mzlib.minecraft.recipe;

import mz.mzlib.minecraft.Identifier;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.util.wrapper.WrapperObject;

import java.util.*;

@VersionRange(end = 1200)
public class RegistrarRecipeVanillaV_1200 extends RegistrarRecipeVanillaV_1300
{
    public static RegistrarRecipeVanillaV_1200 instance;

    @Override
    protected void onReloadEnd(RecipeManager manager)
    {
    }

    @Override
    protected void updateOriginal(RecipeManager manager)
    {
        super.updateOriginal(manager);
        HashMap<RecipeType, Map<Identifier, Recipe>> result =
            this.originalRecipes == null ? new HashMap<>() : new HashMap<>(this.originalRecipes);
        HashMap<Identifier, Recipe> craftingRecipes = new HashMap<>();
        for(RecipeMojang recipe : manager.getRecipesV_1200())
        {
            recipe = recipe.autoCast();
            Recipe last = craftingRecipes.put(recipe.calcIdV_1200(), recipe);
            if(last != null)
                System.err.println("Duplicate recipe: " + recipe.calcIdV_1200() + " " + WrapperObject.debugInfo(recipe));
        }
        result.put(RecipeType.CRAFTING, Collections.unmodifiableMap(craftingRecipes));
        this.originalRecipes = Collections.unmodifiableMap(result);
    }

    @Override
    public synchronized void flush(RecipeManager manager)
    {
        super.flush(manager);
        List<Object> recipes0 = new ArrayList<>();
        for(Recipe recipe : this.getEnabledRecipes().getOrDefault(RecipeType.CRAFTING, Collections.emptyMap()).values())
        {
            recipes0.add(((RecipeMojang)recipe).getWrapped());
        }
        manager.setRecipes0V_1200(recipes0);
    }
}
