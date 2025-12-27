package mz.mzlib.minecraft.recipe;

import mz.mzlib.minecraft.*;
import mz.mzlib.minecraft.bukkit.BukkitNamespacedKeyV1200;
import mz.mzlib.minecraft.bukkit.inventory.BukkitRecipe;
import mz.mzlib.minecraft.incomprehensible.context.MojangContextV2102;
import mz.mzlib.minecraft.incomprehensible.context.SlotDisplayContextsV2102;
import mz.mzlib.minecraft.incomprehensible.recipe.IngredientPlacementV2102;
import mz.mzlib.minecraft.incomprehensible.recipe.RecipeSerializerV1300;
import mz.mzlib.minecraft.incomprehensible.registry.RegistryManagerV1602;
import mz.mzlib.minecraft.inventory.Inventory;
import mz.mzlib.minecraft.inventory.InventoryCrafting;
import mz.mzlib.minecraft.item.ItemStack;
import mz.mzlib.minecraft.recipe.crafting.RecipeCrafting;
import mz.mzlib.minecraft.recipe.crafting.RecipeCraftingShapedImpl;
import mz.mzlib.minecraft.recipe.crafting.RecipeCraftingShapedVanilla;
import mz.mzlib.minecraft.recipe.crafting.RecipeCraftingShapelessVanilla;
import mz.mzlib.minecraft.recipe.display.RecipeDisplayV2102;
import mz.mzlib.minecraft.recipe.input.RecipeInputV2100;
import mz.mzlib.minecraft.recipe.smelting.RecipeFurnaceV1300;
import mz.mzlib.minecraft.registry.entry.RegistryWrapperV1903;
import mz.mzlib.minecraft.util.collection.DefaultedListV1100;
import mz.mzlib.minecraft.world.World;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.minecraft.wrapper.WrapMinecraftMethod;
import mz.mzlib.util.FunctionInvertible;
import mz.mzlib.util.Option;
import mz.mzlib.util.proxy.ListProxy;
import mz.mzlib.util.wrapper.SpecificImpl;
import mz.mzlib.util.wrapper.WrapMethod;
import mz.mzlib.util.wrapper.WrapperFactory;
import mz.mzlib.util.wrapper.WrapperObject;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@WrapMinecraftClass(
    {
        @VersionName(name = "net.minecraft.recipe.RecipeType", end = 1400),
        @VersionName(name = "net.minecraft.recipe.Recipe", begin = 1400)
    }
)
public interface RecipeMojang extends WrapperObject, RecipeVanilla
{
    WrapperFactory<RecipeMojang> FACTORY = WrapperFactory.of(RecipeMojang.class);

    @Override
    RecipeType getType();

    RecipeMojang autoCast();

    @Override
    List<ItemStack> getIcons();

    default List<RecipeDisplayV2102> getDisplaysV2102()
    {
        return new ListProxy<>(this.getDisplays0V2102(), FunctionInvertible.wrapper(RecipeDisplayV2102.FACTORY));
    }

    @VersionRange(end = 1200)
    default Identifier calcIdV_1200()
    {
        return Identifier.ofMinecraft(
            this.getWrapped().getClass().getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase());
    }

    @VersionRange(begin = 1300, end = 2002)
    @WrapMinecraftMethod(
        {
            @VersionName(name = "method_16202", end = 1400),
            @VersionName(name = "getId", begin = 1400)
        }
    )
    Identifier getIdV1300_2002();


    @SpecificImpl("getType")
    @VersionRange(end = 1300)
    default RecipeType getTypeV_1300()
    {
        return RecipeType.CRAFTING;
    }
    @SpecificImpl("getType")
    @VersionRange(begin = 1300, end = 1400)
    default RecipeType getTypeV1300_1400()
    {
        if(this.is(RecipeFurnaceV1300.FACTORY))
            return RecipeType.FURNACE;
        return RecipeType.CRAFTING;
    }
    @SpecificImpl("getType")
    @VersionRange(begin = 1400)
    @WrapMinecraftMethod(@VersionName(name = "getType"))
    RecipeTypeV1400 getTypeV1400();

    // TODO
    @SpecificImpl("autoCast")
    @VersionRange(end = 1300)
    default RecipeMojang autoCastV_1300()
    {
        if(this.is(RecipeCraftingShapedVanilla.FACTORY))
            return this.as(RecipeCraftingShapedVanilla.FACTORY);
        if(this.is(RecipeCraftingShapedImpl.FACTORY))
            return this.as(RecipeCraftingShapedImpl.FACTORY);
        if(this.getWrapped().getClass() == RecipeCraftingShapelessVanilla.FACTORY.getWrappedClass()) // fuck Bukkit
            return this.as(RecipeCraftingShapelessVanilla.FACTORY);
        return this;
    }
    @SpecificImpl("autoCast")
    @VersionRange(begin = 1300, end = 1400)
    default RecipeMojang autoCastV1300_1400()
    {
        if(this.is(RecipeFurnaceV1300.FACTORY))
            return this.as(RecipeFurnaceV1300.FACTORY);
        return this.autoCastV_1300();
    }
    @SpecificImpl("autoCast")
    @VersionRange(begin = 1400)
    default RecipeMojang autoCastV1400()
    {
        RecipeMojang result = this;
        if(result.is(RecipeCrafting.FACTORY))
            result = result.as(RecipeCrafting.FACTORY);
        return result.autoCastV1300_1400();
    }

    @SpecificImpl("getIcons")
    @VersionRange(end = 2102)
    default List<ItemStack> getIconsV_2102()
    {
        return Option.some(this.getIconV_2102()).filter(it -> !it.isEmpty()).map(Collections::singletonList)
            .unwrapOrGet(Collections::emptyList);
    }
    @VersionRange(end = 2102)
    ItemStack getIconV_2102();
    @SpecificImpl("getIconV_2102")
    @VersionRange(end = 1904)
    default ItemStack getIconV_2102$implV_1904()
    {
        return this.getIconV_1904();
    }
    @VersionRange(end = 1904)
    @WrapMinecraftMethod(
        {
            @VersionName(name = "getOutput", end = 1400),
            @VersionName(name = "method_8110", begin = 1400)
        }
    )
    ItemStack getIconV_1904();
    @SpecificImpl("getIconV_2102")
    @VersionRange(begin = 1904, end = 2005)
    default ItemStack getIconV1904_2005()
    {
        return this.getIconV1904_2005(MinecraftServer.instance.getRegistriesV1602());
    }
    @VersionRange(begin = 1904, end = 2005)
    @WrapMinecraftMethod(@VersionName(name = "method_8110"))
    ItemStack getIconV1904_2005(RegistryManagerV1602 registryManager);
    @SpecificImpl("getIconV_2102")
    @VersionRange(begin = 2005, end = 2102)
    default ItemStack getIconV2005_2102()
    {
        return this.getIconV2005_2102(
            MinecraftServer.instance.getRegistriesV1602().as(RegistryWrapperV1903.class_7874.FACTORY));
    }
    @VersionRange(begin = 2005, end = 2102)
    @WrapMinecraftMethod(@VersionName(name = "method_8110"))
    ItemStack getIconV2005_2102(RegistryWrapperV1903.class_7874 registriesLookup);
    @SpecificImpl("getIcons")
    @VersionRange(begin = 2102)
    default List<ItemStack> getIconsV2102()
    {
        MojangContextV2102 context = SlotDisplayContextsV2102.create();
        return this.getDisplaysV2102().stream().map(RecipeDisplayV2102::getResult)
            .map(it -> it.getItemStacks(context)).flatMap(List::stream).collect(Collectors.toList());
    }

    @VersionRange(end = 1300)
    @WrapMinecraftMethod(@VersionName(name = "matches"))
    boolean matchesV_1300(InventoryCrafting inventory, World world);
    @VersionRange(begin = 1300, end = 2100)
    @WrapMinecraftMethod(
        {
            @VersionName(name = "method_3500", end = 1400),
            @VersionName(name = "matches", begin = 1400)
        }
    )
    boolean matchesV1300_2100(Inventory inventory, World world);
    @VersionRange(begin = 2100)
    @WrapMinecraftMethod(@VersionName(name = "matches"))
    boolean matchesV2100(RecipeInputV2100 input, World world);

    @VersionRange(end = 1100)
    @WrapMinecraftMethod(@VersionName(name = "getRemainders"))
    ItemStack.Array getRemainders0V_1100(InventoryCrafting inventory);
    @VersionRange(begin = 1100, end = 1300)
    @WrapMinecraftMethod(@VersionName(name = "method_13670"))
    DefaultedListV1100<?> getRemainders0V1100_1300(InventoryCrafting inventory);
    @VersionRange(begin = 1300, end = 2100)
    @WrapMinecraftMethod({
        @VersionName(name = "method_16203", end = 1400),
        @VersionName(name = "method_8111", begin = 1400)
    })
    DefaultedListV1100<?> getRemainders0V1300_2100(Inventory inventory);
    @VersionRange(begin = 2100, end = 2102)
    @WrapMinecraftMethod(@VersionName(name = "method_8111"))
    DefaultedListV1100<?> getRemainders0V2100_2102(RecipeInputV2100 input);

    @VersionRange(end = 1300)
    @WrapMinecraftMethod(@VersionName(name = "getResult"))
    ItemStack getResultV_1300(InventoryCrafting inventory);
    @VersionRange(begin = 1300, end = 1904)
    @WrapMinecraftMethod(
        {
            @VersionName(name = "method_16201", end = 1400),
            @VersionName(name = "craft", begin = 1400)
        }
    )
    ItemStack getResultV1300_1904(Inventory inventory);
    @VersionRange(begin = 1904, end = 2005)
    @WrapMinecraftMethod(@VersionName(name = "craft"))
    ItemStack getResultV1904_2005(Inventory inventory, RegistryManagerV1602 registryManager);
    @VersionRange(begin = 2005, end = 2100)
    @WrapMinecraftMethod(@VersionName(name = "craft"))
    ItemStack getResultV2005_2100(Inventory inventory, RegistryWrapperV1903.class_7874 lookup);
    @VersionRange(begin = 2100)
    @WrapMinecraftMethod(@VersionName(name = "craft"))
    ItemStack getResultV2100(RecipeInputV2100 input, RegistryWrapperV1903.class_7874 lookup);

    @VersionRange(begin = 1300)
    @WrapMinecraftMethod({
        @VersionName(name = "method_16200", end = 1400),
        @VersionName(name = "getSerializer", begin = 1400)
    })
    RecipeSerializerV1300 getSerializerV1300();

    @VersionRange(begin = 1200, end = 2102)
    @WrapMinecraftMethod({
        @VersionName(name = "method_14252", end = 1400),
        @VersionName(name = "method_8117", begin = 1400) // getPreviewInputs; getIngredients
    })
    DefaultedListV1100<?> getIngredients0V1200_2102();

    @VersionRange(begin = 2102)
    @WrapMinecraftMethod(@VersionName(name = "getIngredientPlacement"))
    IngredientPlacementV2102 getIngredientPlacementV2102();

    /**
     * Usually client only: V1200_2102
     */
    @VersionRange(begin = 1700, end = 2102)
    @WrapMinecraftMethod(@VersionName(name = "fits"))
    boolean fitsV1700_2102(int width, int height);

    @VersionRange(begin = 1904)
    @WrapMinecraftMethod(@VersionName(name = "showNotification"))
    boolean isNotificationEnabledV1904();

    /**
     * Usually client only: V1200
     */
    @VersionRange(begin = 1700)
    @WrapMinecraftMethod(@VersionName(name = "getGroup"))
    String getGroup0V1700();

    @VersionRange(begin = 2102)
    @WrapMinecraftMethod(@VersionName(name = "getDisplays"))
    List<Object> getDisplays0V2102();

    @MinecraftPlatform.Enabled(MinecraftPlatform.Tag.BUKKIT)
    @VersionRange(begin = 1200, end = 1300)
    @WrapMethod("setKey")
    void setKeyBukkitV1200_1300(Identifier id);

    @MinecraftPlatform.Enabled(MinecraftPlatform.Tag.BUKKIT)
    @VersionRange(end = 2002)
    @WrapMethod("toBukkitRecipe")
    BukkitRecipe toBukkitV_2002();
    @MinecraftPlatform.Enabled(MinecraftPlatform.Tag.BUKKIT)
    @VersionRange(begin = 2002)
    @WrapMethod("toBukkitRecipe")
    BukkitRecipe toBukkitV2002(BukkitNamespacedKeyV1200 id);
}
