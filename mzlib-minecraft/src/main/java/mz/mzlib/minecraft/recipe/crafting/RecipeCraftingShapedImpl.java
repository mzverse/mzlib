package mz.mzlib.minecraft.recipe.crafting;

import mz.mzlib.minecraft.Identifier;
import mz.mzlib.minecraft.MinecraftPlatform;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.minecraft.item.ItemStack;
import mz.mzlib.minecraft.item.Items;
import mz.mzlib.minecraft.recipe.Ingredient;
import mz.mzlib.minecraft.recipe.IngredientVanilla;
import mz.mzlib.minecraft.recipe.RecipeMojang;
import mz.mzlib.minecraft.recipe.book.RecipeCraftingCategoryV1903;
import mz.mzlib.util.Option;
import mz.mzlib.util.compound.Compound;
import mz.mzlib.util.compound.CompoundOverride;
import mz.mzlib.util.compound.PropAccessor;
import mz.mzlib.util.wrapper.SpecificImpl;
import mz.mzlib.util.wrapper.WrapConstructor;
import mz.mzlib.util.wrapper.WrapperFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Compound
public interface RecipeCraftingShapedImpl extends RecipeCraftingAbstract, RecipeCraftingShaped
{
    WrapperFactory<RecipeCraftingShapedImpl> FACTORY = WrapperFactory.of(RecipeCraftingShapedImpl.class);

    @Override
    @PropAccessor("display")
    RecipeCraftingShapedVanilla getDisplay();
    @PropAccessor("display")
    void setDisplay(RecipeCraftingShapedVanilla value);

    @PropAccessor("result")
    ItemStack getResult();
    @PropAccessor("result")
    void setResult(ItemStack value);

    @PropAccessor("width")
    int getWidth();
    @PropAccessor("width")
    void setWidth(int value);

    @PropAccessor("height")
    int getHeight();
    @PropAccessor("height")
    void setHeight(int value);

    @PropAccessor("ingredients")
    List<? extends Option<? extends Ingredient>> getIngredients();
    @PropAccessor("ingredients")
    void setIngredients(List<? extends Option<? extends Ingredient>> value);

    @PropAccessor("group")
    Option<String> getGroup();
    @PropAccessor("group")
    void setGroup(Option<String> value);

    @Override
    @PropAccessor("idV1300_2002")
    Identifier getIdV1300_2002();
    @PropAccessor("idV1300_2002")
    void setIdV1300_2002(Identifier value);

    @Override
    @VersionRange(begin = 1903)
    @PropAccessor("categoryV1903")
    RecipeCraftingCategoryV1903 getCategoryV1903();
    @VersionRange(begin = 1903)
    @PropAccessor("categoryV1903")
    void setCategoryV1903(RecipeCraftingCategoryV1903 value);

    @Override
    @VersionRange(begin = 1904)
    @PropAccessor("notificationEnabledV1904")
    @CompoundOverride(parent = RecipeMojang.class, method = "isNotificationEnabledV1904")
    boolean isNotificationEnabledV1904();
    @VersionRange(begin = 1904)
    @PropAccessor("notificationEnabledV1904")
    void setNotificationEnabledV1904(boolean value);


    static RecipeCraftingShapedImpl of(Builder builder)
    {
        return FACTORY.getStatic().static$of(builder);
    }

    @Override
    default boolean matches(Input input)
    {
        if(input.getWidth() != this.getWidth() || input.getHeight() != this.getHeight())
            return false;
        for(int i = 0; i < this.getWidth(); i++)
        {
            l1:
            for(int j = 0; j < this.getHeight(); j++)
            {
                int index = i + j * this.getWidth();
                ItemStack itemStack = input.get(index);
                for(Ingredient ingredient : this.getIngredients().get(index))
                {
                    if(!ingredient.test(itemStack))
                        return false;
                    continue l1;
                }
                if(!itemStack.isEmpty())
                    return false;
            }
        }
        return true;
    }

    @Override
    default ItemStack getResult(Input input)
    {
        return this.getResult().copy();
    }


    RecipeCraftingShapedImpl static$of(Builder builder);
    default RecipeCraftingShapedImpl static$of$common(Builder builder)
    {
        if(builder.result == null)
            throw new IllegalArgumentException("result must be set");
        if(builder.width == null || builder.height == null)
            throw new IllegalArgumentException("width and height must be set");
        if(builder.ingredients == null)
            throw new IllegalArgumentException("ingredients must be set");
        RecipeCraftingShapedImpl result = this.static$of();
        Builder display = builder.clone();
        display.ingredients = display.ingredients.stream().map(it ->
        {
            for(Ingredient ingredient : it)
            {
                if(ingredient instanceof IngredientVanilla)
                    return Option.some((IngredientVanilla) ingredient);
                Collection<ItemStack> examples = ingredient.getExamples();
                if(examples.isEmpty())
                    return Option.some(IngredientVanilla.of(ItemStack.builder().item(Items.BARRIER).build()));
                if(MinecraftPlatform.instance.getVersion() < 1200)
                    return Option.some(IngredientVanilla.of(examples.iterator().next()));
                else
                    return Option.some(IngredientVanilla.ofV1200(new ArrayList<>(examples)));
            }
            return Option.<IngredientVanilla>none();
        }).collect(Collectors.toList());
        result.setDisplay(display.buildVanilla());
        result.setGroup(builder.group);
        result.setResult(builder.result);
        result.setWidth(builder.width);
        result.setHeight(builder.height);
        result.setIngredients(builder.ingredients);
        return result;
    }
    @VersionRange(begin = 1903)
    default RecipeCraftingShapedImpl static$of$commonV1903(Builder builder)
    {
        RecipeCraftingShapedImpl result = this.static$of$common(builder);
        result.setCategoryV1903(builder.categoryV1903);
        return result;
    }
    @SpecificImpl("static$of")
    @VersionRange(end = 1200)
    default RecipeCraftingShapedImpl static$ofV_1200(Builder builder)
    {
        return static$of$common(builder);
    }
    @SpecificImpl("static$of")
    @VersionRange(begin = 1200, end = 1300)
    default RecipeCraftingShapedImpl static$ofV1200_1300(Builder builder)
    {
        return this.static$of$common(builder);
    }
    @SpecificImpl("static$of")
    @VersionRange(begin = 1300, end = 1903)
    default RecipeCraftingShapedImpl static$ofV1300_1903(Builder builder)
    {
        RecipeCraftingShapedImpl result = this.static$of$common(builder);
        result.setIdV1300_2002(builder.getId());
        return result;
    }
    @SpecificImpl("static$of")
    @VersionRange(begin = 1903, end = 1904)
    default RecipeCraftingShapedImpl static$ofV1903_1904(Builder builder)
    {
        RecipeCraftingShapedImpl result = static$of$commonV1903(builder);
        result.setIdV1300_2002(builder.getId());
        return result;
    }
    @SpecificImpl("static$of")
    @VersionRange(begin = 1904, end = 2002)
    default RecipeCraftingShapedImpl static$ofV1904_2002(Builder builder)
    {
        RecipeCraftingShapedImpl result = static$of$commonV1903(builder);
        result.setIdV1300_2002(builder.getId());
        result.setNotificationEnabledV1904(builder.notificationEnabledV1904);
        return result;
    }
    @SpecificImpl("static$of")
    @VersionRange(begin = 2002)
    default RecipeCraftingShapedImpl static$ofV2002(Builder builder)
    {
        RecipeCraftingShapedImpl result = static$of$commonV1903(builder);
        result.setNotificationEnabledV1904(builder.notificationEnabledV1904);
        return result;
    }

    @WrapConstructor
    RecipeCraftingShapedImpl static$of();

    @Override
    default Option<String> getGroupV1200()
    {
        return this.getGroup();
    }
    @Override
    default Option<String> getGroupV1700()
    {
        return this.getGroup();
    }
}
