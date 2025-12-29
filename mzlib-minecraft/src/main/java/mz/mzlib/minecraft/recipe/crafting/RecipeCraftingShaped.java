package mz.mzlib.minecraft.recipe.crafting;

import mz.mzlib.minecraft.Identifier;
import mz.mzlib.minecraft.MinecraftPlatform;
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

import java.util.*;
import java.util.stream.Collectors;

@WrapSameClass(RecipeCrafting.class)
public interface RecipeCraftingShaped extends RecipeCrafting
{
    WrapperFactory<RecipeCraftingShaped> FACTORY = WrapperFactory.of(RecipeCraftingShaped.class);

    int getWidth();
    int getHeight();
    List<? extends Option<? extends Ingredient>> getIngredients();

    static Builder builder()
    {
        return new Builder();
    }
    class Builder extends SimpleCloneable<Builder>
    {
        Identifier id;
        ItemStack result;
        Integer width, height;
        List<? extends Option<? extends Ingredient>> ingredients;
        Option<String> group = Option.none();
        RecipeCraftingCategoryV1903 categoryV1903 = CATEGORY_DEFAULT_V1903;
        boolean notificationEnabledV1904 = true;
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
        public Builder group(String value)
        {
            this.group = Option.some(value);
            return this;
        }
        public Builder categoryV1903(RecipeCraftingCategoryV1903 value)
        {
            this.categoryV1903 = value;
            return this;
        }
        public Builder notificationEnabledV1904(boolean value)
        {
            this.notificationEnabledV1904 = value;
            return this;
        }

        public RecipeCraftingShaped build()
        {
            if(this.isVanilla())
                return this.buildVanilla();
            this.check();
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
            this.check();
            return RecipeCraftingShapedVanilla.of(this);
        }
        public RecipeRegistration<RecipeCraftingShapedVanilla> buildVanillaRegistration()
        {
            if(this.id == null)
                throw new IllegalArgumentException("id must be set");
            return RecipeRegistration.of(this.id, this.buildVanilla());
        }
        public void check()
        {
            for(Ingredient countRequired : Option.fromOptional(
                this.ingredients.stream().flatMap(Option::stream).filter(Ingredient::isCountRequired).findAny()))
            {
                throw new IllegalArgumentException("count required: " + countRequired);
            }
        }
        public boolean isVanilla()
        {
            return checkVanilla(false);
        }
        public void checkVanilla()
        {
            this.checkVanilla(true);
        }
        public boolean checkVanilla(boolean doAssert)
        {
            if(this.group.isSome() && MinecraftPlatform.instance.getVersion() < 1200)
            {
                if(doAssert)
                    throw new IllegalArgumentException("group in vanilla is supported when version >= 1.12");
                return false;
            }
            for(Ingredient ingredient : Option.fromOptional(this.ingredients.stream().flatMap(Option::stream)
                .filter(ThrowablePredicate.of(IngredientVanilla.class::isInstance).negate()).findAny()))
            {
                if(doAssert)
                    throw new IllegalArgumentException("ingredient is not vanilla: " + ingredient);
                return false;
            }
            return true;
        }

        public Identifier getId()
        {
            if(this.id == null)
                throw new IllegalStateException("id is not set");
            return this.id;
        }
        List<Option<IngredientVanilla>> getIngredientsVanilla()
        {
            this.checkVanilla();
            return RuntimeUtil.cast(this.ingredients);
        }
        public String getGroup0V1200()
        {
            return this.group.unwrapOr("");
        }
        DefaultedListV1100<?> getIngredientsV1200_2003()
        {
            return DefaultedListV1100.fromWrapper(
                this.getIngredientsVanilla().stream().map(IngredientVanilla::fromOptionV_2102)
                    .collect(Collectors.toList()),
                IngredientVanilla.EMPTY_V_2102
            );
        }

        /**
         * Set width, height, and ingredients from a pattern.
         */
        public StepPattern pattern(String... pattern)
        {
            return new StepPattern(this, pattern);
        }
        public static class StepPattern
        {
            Builder builder;
            String[] pattern;
            Map<Character, Ingredient> key = new HashMap<>();
            public StepPattern(Builder builder, String[] pattern)
            {
                this.builder = builder;
                this.pattern = pattern;
            }
            public StepPattern where(char c, Ingredient ingredient)
            {
                this.key.put(c, ingredient);
                return this;
            }
            public Builder finish()
            {
                if(this.pattern.length == 0)
                    return this.builder.width(0).height(0).ingredients(Collections.emptyList());
                int width = this.pattern[0].length();
                if(!Arrays.stream(this.pattern).allMatch(s -> s.length() == width))
                    throw new IllegalArgumentException(
                        "all rows must have the same length: " + Arrays.toString(this.pattern));
                List<Option<Ingredient>> result = new ArrayList<>();
                for(String row : this.pattern)
                {
                    for(int x = 0; x < width; x++)
                    {
                        char c = row.charAt(x);
                        if(c == ' ')
                            result.add(Option.none());
                        else
                        {
                            Ingredient ingredient = this.key.get(c);
                            if(ingredient == null)
                                throw new IllegalArgumentException("no ingredient for key: " + c);
                            result.add(Option.some(ingredient));
                        }
                    }
                }
                return this.builder.width(width).height(this.pattern.length).ingredients(result);
            }
        }
    }
}
