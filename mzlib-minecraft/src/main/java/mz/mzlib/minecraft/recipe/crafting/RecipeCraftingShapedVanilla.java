package mz.mzlib.minecraft.recipe.crafting;

import mz.mzlib.minecraft.Identifier;
import mz.mzlib.minecraft.VersionName;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.minecraft.incomprehensible.recipe.RawShapedRecipeV2003;
import mz.mzlib.minecraft.incomprehensible.recipe.RecipeSerializerV1300;
import mz.mzlib.minecraft.item.ItemStack;
import mz.mzlib.minecraft.nbt.NbtIo;
import mz.mzlib.minecraft.recipe.IngredientVanilla;
import mz.mzlib.minecraft.recipe.RecipeMojang;
import mz.mzlib.minecraft.recipe.book.RecipeCraftingCategoryV1903;
import mz.mzlib.minecraft.util.collection.DefaultedListV1100;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.minecraft.wrapper.WrapMinecraftFieldAccessor;
import mz.mzlib.minecraft.wrapper.WrapMinecraftInnerClass;
import mz.mzlib.util.FunctionInvertible;
import mz.mzlib.util.Option;
import mz.mzlib.util.ThrowablePredicate;
import mz.mzlib.util.proxy.ListProxy;
import mz.mzlib.util.wrapper.SpecificImpl;
import mz.mzlib.util.wrapper.WrapConstructor;
import mz.mzlib.util.wrapper.WrapperArray;
import mz.mzlib.util.wrapper.WrapperFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@WrapMinecraftClass(
    {
        @VersionName(name = "net.minecraft.recipe.ShapedRecipeType", end = 1400),
        @VersionName(name = "net.minecraft.recipe.ShapedRecipe", begin = 1400)
    }
)
public interface RecipeCraftingShapedVanilla extends RecipeMojang, RecipeCraftingShaped
{
    WrapperFactory<RecipeCraftingShapedVanilla> FACTORY = WrapperFactory.of(RecipeCraftingShapedVanilla.class);

    static RecipeCraftingShapedVanilla of(Builder builder)
    {
        return FACTORY.getStatic().static$of(builder);
    }
    
    @Override
    int getWidth();
    @Override
    int getHeight();
    
    @Override
    List<Option<IngredientVanilla>> getIngredients();

    @VersionRange(begin = 2003)
    @WrapMinecraftFieldAccessor(@VersionName(name = "raw"))
    RawShapedRecipeV2003 getRawV2003();

    @Override
    default Identifier calcIdV_1200()
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try(DataOutputStream dataOutput = new DataOutputStream(stream))
        {
            dataOutput.writeInt(this.getWidth());
            dataOutput.writeInt(this.getHeight());
            for(Option<IngredientVanilla> ingredient : this.getIngredients())
            {
                NbtIo.write(
                    ingredient.map(i -> i.as(ItemStack.FACTORY)).unwrapOrGet(() -> ItemStack.EMPTY).encode()
                        .getOrThrow(IllegalStateException::new).unwrap(),
                    dataOutput
                );
            }
        }
        catch(IOException e)
        {
            throw new IllegalStateException(e);
        }
        return Identifier.ofMinecraft(UUID.nameUUIDFromBytes(stream.toByteArray()).toString());
    }

    @VersionRange(begin = 1200)
    default Option<String> getGroupV1200()
    {
        return Option.some(this.getGroup0V1200()).filter(ThrowablePredicate.of(String::isEmpty).negate());
    }

    @VersionRange(begin = 1300)
    @WrapMinecraftInnerClass(outer = RecipeCraftingShapedVanilla.class, name = {
        @VersionName(name = "class_3581", end = 1400),
        @VersionName(name = "Serializer", begin = 1400)
    })
    interface SerializerV1300 extends RecipeSerializerV1300
    {
        WrapperFactory<SerializerV1300> FACTORY = WrapperFactory.of(SerializerV1300.class);
    }


    RecipeCraftingShapedVanilla static$of(Builder builder);

    @SpecificImpl("static$of")
    @VersionRange(end = 1200)
    default RecipeCraftingShapedVanilla static$ofV_1200(Builder builder)
    {
        return this.static$ofV_1200(
            builder.width, builder.height,
            builder.getIngredientsVanilla().stream().map(IngredientVanilla::fromOptionV_2102).map(i -> i.as(ItemStack.FACTORY))
                .collect(WrapperArray.collector(ItemStack.Array.FACTORY)), builder.result
        );
    }
    @VersionRange(end = 1200)
    @WrapConstructor
    RecipeCraftingShapedVanilla static$ofV_1200(int width, int height, ItemStack.Array ingredients, ItemStack result);

    @SpecificImpl("static$of")
    @VersionRange(begin = 1200, end = 1300)
    default RecipeCraftingShapedVanilla static$ofV1200_1300(Builder builder)
    {
        return this.static$of0V1200_1300(
            builder.getGroup0V1200(), builder.width, builder.height, builder.getIngredientsV1200_2003(), builder.result);
    }
    @VersionRange(begin = 1200, end = 1300)
    @WrapConstructor
    RecipeCraftingShapedVanilla static$of0V1200_1300(
        String group,
        int width,
        int height,
        DefaultedListV1100<?> ingredients,
        ItemStack result);

    @SpecificImpl("static$of")
    @VersionRange(begin = 1300, end = 1903)
    default RecipeCraftingShapedVanilla static$ofV1300_1903(Builder builder)
    {
        return this.static$of0V1300_1903(
            builder.getId(), builder.getGroup0V1200(), builder.width, builder.height,
            builder.getIngredientsV1200_2003(), builder.result
        );
    }
    @VersionRange(begin = 1300, end = 1903)
    @WrapConstructor
    RecipeCraftingShapedVanilla static$of0V1300_1903(
        Identifier id,
        String group,
        int width,
        int height,
        DefaultedListV1100<?> ingredients,
        ItemStack result);

    @SpecificImpl("static$of")
    @VersionRange(begin = 1903, end = 1904)
    default RecipeCraftingShapedVanilla static$ofV1903_1904(Builder builder)
    {
        return this.static$of0V1903_1904(
            builder.getId(), builder.getGroup0V1200(), builder.categoryV1903, builder.width, builder.height,
            builder.getIngredientsV1200_2003(), builder.result
        );
    }
    @VersionRange(begin = 1903, end = 1904)
    @WrapConstructor
    RecipeCraftingShapedVanilla static$of0V1903_1904(
        Identifier id,
        String group,
        RecipeCraftingCategoryV1903 category,
        int width,
        int height,
        DefaultedListV1100<?> ingredients,
        ItemStack result
    );

    @SpecificImpl("static$of")
    @VersionRange(begin = 1904, end = 2002)
    default RecipeCraftingShapedVanilla static$ofV1904_2002(Builder builder)
    {
        return this.static$of0V1904_2002(
            builder.getId(), builder.getGroup0V1200(), builder.categoryV1903, builder.width, builder.height,
            builder.getIngredientsV1200_2003(), builder.result, builder.notificationEnabledV1904
        );
    }
    @VersionRange(begin = 1904, end = 2002)
    @WrapConstructor
    RecipeCraftingShapedVanilla static$of0V1904_2002(
        Identifier id,
        String group,
        RecipeCraftingCategoryV1903 category,
        int width,
        int height,
        DefaultedListV1100<?> ingredients,
        ItemStack result,
        boolean showNotification
    );

    @SpecificImpl("static$of")
    @VersionRange(begin = 2002, end = 2003)
    default RecipeCraftingShapedVanilla static$ofV2002_2003(Builder builder)
    {
        return this.static$of0V2002_2003(
            builder.getGroup0V1200(), builder.categoryV1903, builder.width, builder.height,
            builder.getIngredientsV1200_2003(), builder.result, builder.notificationEnabledV1904
        );
    }
    @VersionRange(begin = 2002, end = 2003)
    @WrapConstructor
    RecipeCraftingShapedVanilla static$of0V2002_2003(
        String group,
        RecipeCraftingCategoryV1903 category,
        int width,
        int height,
        DefaultedListV1100<?> ingredients,
        ItemStack result,
        boolean showNotification);

    @SpecificImpl("static$of")
    @VersionRange(begin = 2003)
    default RecipeCraftingShapedVanilla static$ofV2003(Builder builder)
    {
        return this.static$ofV2003(
            builder.getGroup0V1200(), builder.categoryV1903,
            RawShapedRecipeV2003.of(builder.width, builder.height, builder.getIngredientsVanilla(), Option.none()),
            builder.result, builder.notificationEnabledV1904
        );
    }
    @VersionRange(begin = 2003)
    @WrapConstructor
    RecipeCraftingShapedVanilla static$ofV2003(
        String group,
        RecipeCraftingCategoryV1903 category,
        RawShapedRecipeV2003 raw,
        ItemStack result,
        boolean showNotification);

    @SpecificImpl("getWidth")
    @VersionRange(end = 2003)
    @WrapMinecraftFieldAccessor(@VersionName(name = "width"))
    int getWidthV_2003();
    @SpecificImpl("getHeight")
    @VersionRange(end = 2003)
    @WrapMinecraftFieldAccessor(@VersionName(name = "height"))
    int getHeightV_2003();

    @SpecificImpl("getWidth")
    @VersionRange(begin = 2003)
    default int getWidthV2003()
    {
        return this.getRawV2003().getWidth();
    }
    @SpecificImpl("getHeight")
    @VersionRange(begin = 2003)
    default int getHeightV2003()
    {
        return this.getRawV2003().getHeight();
    }

    @SpecificImpl("getIngredients")
    @VersionRange(end = 1200)
    default List<Option<IngredientVanilla>> getIngredientsV_1200()
    {
        return new ListProxy<>(
            this.getIngredients0V_1200().asList(),
            FunctionInvertible.of(IngredientVanilla::toOptionV_2102, IngredientVanilla::fromOptionV_2102)
        );
    }
    @VersionRange(end = 1200)
    @WrapMinecraftFieldAccessor(@VersionName(name = "ingredients"))
    IngredientVanilla.Array getIngredients0V_1200();

    @SpecificImpl("getIngredients")
    @VersionRange(begin = 1200, end = 2003)
    default List<Option<IngredientVanilla>> getIngredientsV1200_2003()
    {
        return new ListProxy<>(
            this.getIngredients0V1200_2003().getWrapped(),
            FunctionInvertible.wrapper(IngredientVanilla.FACTORY)
                .thenApply(IngredientVanilla::toOptionV_2102, IngredientVanilla::fromOptionV_2102)
        );
    }
    @VersionRange(begin = 1200, end = 2003)
    @WrapMinecraftFieldAccessor(
        {
            @VersionName(name = "field_15686", end = 1400),
            @VersionName(name = "field_9052", begin = 1400)
        }
    )
    DefaultedListV1100<Object> getIngredients0V1200_2003();

    @SpecificImpl("getIngredients")
    @VersionRange(begin = 2003)
    default List<Option<IngredientVanilla>> getIngredientsV2003()
    {
        return this.getRawV2003().getIngredients();
    }

    @WrapMinecraftFieldAccessor({
        @VersionName(name = "field_15687", end = 1400),
        @VersionName(name = "group", begin = 1400)
    })
    String getGroup0V1200();
}
