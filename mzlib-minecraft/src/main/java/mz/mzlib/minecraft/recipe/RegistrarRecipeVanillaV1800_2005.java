package mz.mzlib.minecraft.recipe;

import mz.mzlib.minecraft.Identifier;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.util.CollectionUtil;
import mz.mzlib.util.RuntimeUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@VersionRange(begin = 1800, end = 2005)
public class RegistrarRecipeVanillaV1800_2005 extends RegistrarRecipeVanillaV1400_2005
{
    public static RegistrarRecipeVanillaV1800_2005 instance;

    @Override
    public synchronized void flush(RecipeManager manager)
    {
        super.flush(manager);
        Map<Object, Object> result = new HashMap<>();
        for(Map.Entry<Identifier, RecipeMojang> entry : CollectionUtil.asIterable(
            RuntimeUtil.<Map<RecipeType, Map<Identifier, RecipeMojang>>>cast(this.getEnabledRecipes()).values().stream().map(Map::entrySet).flatMap(Set::stream).iterator()))
        {
            result.put(entry.getKey().getWrapped(), toData.apply(RecipeRegistration.of(entry.getKey(), entry.getValue())).getWrapped());
        }
        manager.setIdRecipes0V1800_2102(Collections.unmodifiableMap(result));
    }
}
