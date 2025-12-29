package mz.mzlib.minecraft.recipe;

import mz.mzlib.minecraft.item.ItemStack;
import mz.mzlib.util.Option;

import java.util.List;

public interface Recipe
{
    RecipeType getType();

    List<ItemStack> getIcons();

    default Option<?> getGroup()
    {
        return Option.none();
    }
}
