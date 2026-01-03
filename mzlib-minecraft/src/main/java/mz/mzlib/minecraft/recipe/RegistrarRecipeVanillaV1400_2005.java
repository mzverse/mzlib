package mz.mzlib.minecraft.recipe;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import mz.mzlib.minecraft.Identifier;
import mz.mzlib.minecraft.MinecraftPlatform;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.util.RuntimeUtil;
import mz.mzlib.util.wrapper.WrapperObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@VersionRange(begin = 1400, end = 2005)
public class RegistrarRecipeVanillaV1400_2005 extends RegistrarRecipeVanilla
{
    public static RegistrarRecipeVanillaV1400_2005 instance;

    Function<RecipeRegistration<? extends RecipeMojang>, WrapperObject> toData = MinecraftPlatform.instance.getVersion() < 2002 ?
        RecipeRegistration::getRecipeV1300 :
        RecipeEntryV2002::of;
    Function<WrapperObject, RecipeMojang> toRecipe = MinecraftPlatform.instance.getVersion() < 2002 ?
        o -> o.as(RecipeMojang.FACTORY) :
        o -> o.as(RecipeEntryV2002.FACTORY).getValue();

    @Override
    protected void updateOriginal(RecipeManager manager)
    {
        this.originalRecipes = Collections.unmodifiableMap(
            manager.getRecipes0V1400_2005().entrySet().stream()
                .collect(Collectors.toMap(
                    entry -> RecipeTypeV1400.FACTORY.create(entry.getKey()), entry -> Collections.unmodifiableMap(
                        entry.getValue().entrySet().stream().collect(Collectors.toMap(
                            e -> Identifier.FACTORY.create(e.getKey()),
                            e -> toRecipe.apply(WrapperObject.FACTORY.create(e.getValue())).autoCast()
                        )))
                )));
    }

    @Override
    public synchronized void flush(RecipeManager manager)
    {
        super.flush(manager);
        Map<Object, Map<Object, Object>> result = new HashMap<>();
        for(Map.Entry<RecipeType, Map<Identifier, RecipeMojang>> entry : RuntimeUtil.<Map<RecipeType, Map<Identifier, RecipeMojang>>>cast(this.getEnabledRecipes()).entrySet())
        {
            Map<Object, Object> map = result.computeIfAbsent(
                ((RecipeTypeV1400) entry.getKey()).getWrapped(), k -> new Object2ObjectLinkedOpenHashMap<>());
            for(Map.Entry<Identifier, RecipeMojang> e : entry.getValue().entrySet())
            {
                map.put(e.getKey().getWrapped(), toData.apply(RecipeRegistration.of(e.getKey(), e.getValue())).getWrapped());
            }
        }
        manager.setRecipes0V1400_2005(Collections.unmodifiableMap(result));
    }
}
