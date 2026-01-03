package mz.mzlib.minecraft.recipe;

import mz.mzlib.minecraft.Identifier;
import mz.mzlib.minecraft.MinecraftServer;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.util.CollectionUtil;

import java.util.*;

@VersionRange(begin = 2102)
public class RegistrarRecipeVanillaV2102 extends RegistrarRecipeVanilla
{
    public static RegistrarRecipeVanillaV2102 instance;

    @Override
    protected void updateOriginal(RecipeManager manager)
    {
        Map<RecipeType, Map<Identifier, Recipe>> result = new HashMap<>();
        for(RecipeEntryV2002 recipe : manager.getPreparedRecipesV2102()
            .recipes())
        {
            RecipeMojang r = recipe.getValue().autoCast();
            result.computeIfAbsent(r.getType(), k -> new HashMap<>())
                .put(recipe.getId(), r);
        }
        for(Map.Entry<RecipeType, Map<Identifier, Recipe>> entry : result.entrySet())
        {
            entry.setValue(Collections.unmodifiableMap(result.get(entry.getKey())));
        }
        this.originalRecipes = Collections.unmodifiableMap(result);
    }

    @Override
    public synchronized void flush(RecipeManager manager)
    {
        super.flush(manager);
        List<RecipeEntryV2002> result = new ArrayList<>();
        for(Map.Entry<Identifier, Recipe> entry : CollectionUtil.asIterable(
            this.getEnabledRecipes().values().stream().map(Map::entrySet).flatMap(Set::stream).iterator()))
        {
            result.add(RecipeEntryV2002.of(RecipeRegistration.of(entry.getKey(), (RecipeMojang) entry.getValue())));
        }
        manager.setPreparedRecipesV2102(PreparedRecipesV2102.of(result));
        manager.initializeV2102(MinecraftServer.instance.getSavePropertiesV1600().getEnabledFeaturesV1903());
    }
}
