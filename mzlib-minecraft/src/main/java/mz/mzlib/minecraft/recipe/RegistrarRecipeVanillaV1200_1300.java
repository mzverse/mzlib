package mz.mzlib.minecraft.recipe;

import mz.mzlib.minecraft.Identifier;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.minecraft.registry.RegistrySimple;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@VersionRange(begin = 1200, end = 1300)
public class RegistrarRecipeVanillaV1200_1300 extends RegistrarRecipeVanillaV_1300
{
    public static RegistrarRecipeVanillaV1200_1300 instance;

    @Override
    protected void updateOriginal(RecipeManager manager)
    {
        super.updateOriginal(manager);
        Map<RecipeType, Map<Identifier, Recipe>> result = new HashMap<>(this.originalRecipes);
        HashMap<Identifier, Recipe> craftingRecipes = new HashMap<>();
        RegistrySimple<?> registry = RecipeManager.getRegistryV1200_1300();
        for(Identifier id : registry.getIdsV_1300())
        {
            craftingRecipes.put(id, registry.get(id).as(RecipeMojang.FACTORY).autoCast());
        }
        result.put(RecipeType.CRAFTING, Collections.unmodifiableMap(craftingRecipes));
        this.originalRecipes = Collections.unmodifiableMap(result);
    }

    @Override
    public synchronized void flush(RecipeManager manager)
    {
        super.flush(manager);
        RecipeManager.setRegistryV1200_1300(RegistrySimple.ofV_1600()); // FIXME: 原始顺序（raw id）是重要的，对于配方书
        for(Map.Entry<Identifier, Recipe> e : this.getEnabledRecipes()
            .getOrDefault(RecipeType.CRAFTING, Collections.emptyMap()).entrySet())
        {
            RecipeManager.registerV1200_1300(e.getKey(), (RecipeMojang) e.getValue());
        }
    }
}
