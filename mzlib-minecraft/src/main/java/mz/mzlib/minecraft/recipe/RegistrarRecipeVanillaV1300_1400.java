package mz.mzlib.minecraft.recipe;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import mz.mzlib.minecraft.Identifier;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.util.CollectionUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@VersionRange(begin = 1300, end = 1400)
public class RegistrarRecipeVanillaV1300_1400 extends RegistrarRecipeVanilla
{
    public static RegistrarRecipeVanillaV1300_1400 instance;

    Map<Object, Object> rawRecipes0;

    @Override
    protected void updateOriginal(RecipeManager recipeManager)
    {
        this.rawRecipes0 = recipeManager.getRecipes0V1300_1400();
        Map<RecipeType, Map<Identifier, Recipe>> result = new HashMap<>();
        for(Map.Entry<Identifier, RecipeMojang> e : recipeManager.getRecipesV1300_1400().entrySet())
        {
            RecipeMojang recipe = e.getValue().autoCast();
            result.computeIfAbsent(recipe.getType(), k -> new HashMap<>())
                .put(e.getKey(), recipe);
        }
        for(Map.Entry<RecipeType, Map<Identifier, Recipe>> entry : result.entrySet())
        {
            entry.setValue(Collections.unmodifiableMap(result.get(entry.getKey())));
        }
        this.originalRecipes = Collections.unmodifiableMap(result);
    }

    @Override
    public synchronized void flush(RecipeManager recipeManager)
    {
        super.flush(recipeManager);
        Map<Object, Object> result = new Object2ObjectLinkedOpenHashMap<>(); // adapt for Bukkit
        for(Map.Entry<Identifier, Recipe> entry : CollectionUtil.asIterable(
            this.getEnabledRecipes().values().stream().map(Map::entrySet).flatMap(Set::stream).iterator()))
        {
            result.put(entry.getKey().getWrapped(), ((RecipeMojang) entry.getValue()).getWrapped());
        }
        recipeManager.setRecipes0V1300_1400(result);
    }
}
