package mz.mzlib.minecraft.recipe.crafting;

import mz.mzlib.minecraft.Identifier;
import mz.mzlib.minecraft.MinecraftPlatform;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.minecraft.item.ItemStack;
import mz.mzlib.minecraft.recipe.Ingredient;
import mz.mzlib.minecraft.recipe.IngredientVanilla;
import mz.mzlib.minecraft.recipe.RecipeRegistration;
import mz.mzlib.minecraft.recipe.book.RecipeCraftingCategoryV1903;
import mz.mzlib.minecraft.util.collection.DefaultedListV1100;
import mz.mzlib.util.Option;
import mz.mzlib.util.RuntimeUtil;
import mz.mzlib.util.SimpleCloneable;
import mz.mzlib.util.ThrowablePredicate;
import mz.mzlib.util.wrapper.WrapSameClass;
import mz.mzlib.util.wrapper.WrapperFactory;

import java.util.List;
import java.util.stream.Collectors;

@WrapSameClass(RecipeCrafting.class)
public interface RecipeCraftingShaped extends RecipeCrafting
{
    WrapperFactory<RecipeCraftingShaped> FACTORY = WrapperFactory.of(RecipeCraftingShaped.class);

    int getWidth();
    int getHeight();
    List<? extends Option<? extends Ingredient>> getIngredients();

    @VersionRange(begin = 1200)
    Option<String> getGroupV1200();

    static Builder builder()
    {
        return new Builder();
    }
    class Builder extends SimpleCloneable<Builder>
    {
        Identifier id;
        ItemStack result;
        Integer width, height;
        List<? extends Option< ? extends Ingredient>> ingredients;
        Option<String> groupV1200 = Option.none();
        boolean notificationEnabledV1904 = true;
        RecipeCraftingCategoryV1903 categoryV1903 = CATEGORY_DEFAULT_V1903;
        static RecipeCraftingCategoryV1903 CATEGORY_DEFAULT_V1903 =
            MinecraftPlatform.instance.getVersion() < 1903 ? null : RecipeCraftingCategoryV1903.MISC;
        public Builder id(Identifier value)
        {
            this.id = value;
            return this;
        }
        public Builder result(ItemStack value)
        {
            this.result = value;
            return this;
        }
        public Builder width(int value)
        {
            this.width = value;
            return this;
        }
        public Builder height(int value)
        {
            this.height = value;
            return this;
        }
        public Builder ingredients(List<? extends Option<? extends Ingredient>> value)
        {
            this.ingredients = value;
            return this;
        }
        public Builder groupV1200(String value)
        {
            this.groupV1200 = Option.some(value);
            return this;
        }
        public Builder notificationEnabledV1904(boolean value)
        {
            this.notificationEnabledV1904 = value;
            return this;
        }
        public Builder categoryV1903(RecipeCraftingCategoryV1903 value)
        {
            this.categoryV1903 = value;
            return this;
        }

        public RecipeCraftingShaped build()
        {
            if(this.isVanilla())
                return this.buildVanilla();
            return RecipeCraftingShapedImpl.of(this);
        }
        public RecipeRegistration<RecipeCraftingShaped> buildRegistration()
        {
            if(this.id == null)
                throw new IllegalArgumentException("id must be set");
            return RecipeRegistration.of(this.id, this.build());
        }
        public RecipeCraftingShapedVanilla buildVanilla()
        {
            return RecipeCraftingShapedVanilla.of(this);
        }
        public RecipeRegistration<RecipeCraftingShapedVanilla> buildVanillaRegistration()
        {
            if(this.id == null)
                throw new IllegalArgumentException("id must be set");
            return RecipeRegistration.of(this.id, this.buildVanilla());
        }
        public boolean isVanilla()
        {
            return this.ingredients.stream().flatMap(Option::stream).allMatch(IngredientVanilla.class::isInstance);
        }

        public Identifier getId()
        {
            if(this.id == null)
                throw new IllegalStateException("id is not set");
            return this.id;
        }
        List<Option<IngredientVanilla>> getIngredientsVanilla()
        {
            for(Ingredient ingredient : Option.fromOptional(this.ingredients.stream().flatMap(Option::stream)
                .filter(ThrowablePredicate.of(IngredientVanilla.class::isInstance).negate()).findAny()))
            {
                throw new IllegalArgumentException("ingredient is not vanilla: "+ingredient);
            }
            return RuntimeUtil.cast(this.ingredients);
        }
        public String getGroup0V1200()
        {
            return this.groupV1200.unwrapOr("");
        }
        DefaultedListV1100<?> getIngredientsV1200_2003()
        {
            return DefaultedListV1100.fromWrapper(
                this.getIngredientsVanilla().stream().map(IngredientVanilla::fromOptionV_2102).collect(Collectors.toList()),
                IngredientVanilla.EMPTY_V_2102
            );
        }
    }
}
