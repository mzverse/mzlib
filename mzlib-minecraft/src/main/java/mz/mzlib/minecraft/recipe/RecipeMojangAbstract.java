package mz.mzlib.minecraft.recipe;

import mz.mzlib.minecraft.Identifier;
import mz.mzlib.minecraft.MinecraftPlatform;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.minecraft.bukkit.BukkitNamespacedKeyV1200;
import mz.mzlib.minecraft.bukkit.inventory.BukkitRecipe;
import mz.mzlib.minecraft.incomprehensible.recipe.IngredientPlacementV2102;
import mz.mzlib.minecraft.incomprehensible.recipe.RecipeSerializerV1300;
import mz.mzlib.minecraft.incomprehensible.registry.RegistryManagerV1602;
import mz.mzlib.minecraft.inventory.Inventory;
import mz.mzlib.minecraft.inventory.InventoryCrafting;
import mz.mzlib.minecraft.item.ItemStack;
import mz.mzlib.minecraft.network.packet.s2c.play.PacketS2cRecipesV1300_2102;
import mz.mzlib.minecraft.recipe.display.RecipeDisplayV2102;
import mz.mzlib.minecraft.recipe.input.RecipeInputV2100;
import mz.mzlib.minecraft.registry.entry.RegistryWrapperV1903;
import mz.mzlib.minecraft.util.collection.DefaultedListV1100;
import mz.mzlib.minecraft.world.World;
import mz.mzlib.module.MzModule;
import mz.mzlib.util.FunctionInvertible;
import mz.mzlib.util.Option;
import mz.mzlib.util.RuntimeUtil;
import mz.mzlib.util.compound.Compound;
import mz.mzlib.util.compound.CompoundOverride;
import mz.mzlib.util.nothing.Nothing;
import mz.mzlib.util.nothing.NothingInject;
import mz.mzlib.util.nothing.NothingInjectType;
import mz.mzlib.util.proxy.ListProxy;
import mz.mzlib.util.wrapper.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Compound
public interface RecipeMojangAbstract<I extends RecipeMojangAbstract.Input> extends RecipeMojang
{
    WrapperFactory<RecipeMojangAbstract<?>> FACTORY = WrapperFactory.of(
        RuntimeUtil.castClass(RecipeMojangAbstract.class));

    RecipeMojang getDisplay();

    boolean matches(I input);

    /**
     * Make a new result
     * Such as `return result.copy();`
     */
    ItemStack getResult(I input);

    @Override
    Identifier getIdV1300_2002();

    Option<String> getGroupV1700();

    interface Input
    {
        ItemStack get(int index);
    }

    I inputV_2100(Inventory inventory);
    I inputV2100(RecipeInputV2100 input);
    class InputV_2100<T extends Inventory> implements Input
    {
        T delegate;
        public InputV_2100(T delegate)
        {
            this.delegate = delegate;
        }
        public T getDelegate()
        {
            return this.delegate;
        }
        @Override
        public ItemStack get(int index)
        {
            return this.getDelegate().getItemStack(index);
        }
    }
    class InputV2100<T extends RecipeInputV2100> implements Input
    {
        T delegate;
        public InputV2100(T delegate)
        {
            this.delegate = delegate;
        }
        public T getDelegate()
        {
            return this.delegate;
        }
        @Override
        public ItemStack get(int index)
        {
            return this.getDelegate().get(index);
        }
    }

    @Override
    @VersionRange(begin = 1300)
    default RecipeSerializerV1300 getSerializerV1300()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    @VersionRange(begin = 1200, end = 2102)
    default DefaultedListV1100<?> getIngredients0V1200_2102()
    {
        return this.getDisplay().getIngredients0V1200_2102();
    }
    @Override
    @VersionRange(begin = 2102)
    default IngredientPlacementV2102 getIngredientPlacementV2102()
    {
        return this.getDisplay().getIngredientPlacementV2102();
    }

    @Override
    @VersionRange(begin = 1700, end = 2102)
    default boolean fitsV1700_2102(int width, int height)
    {
        return true;
    }

    @Override
    @VersionRange(begin = 2102)
    default List<RecipeDisplayV2102> getDisplaysV2102()
    {
        return this.getDisplay().getDisplaysV2102();
    }

    @Override
    @VersionRange(end = 1300)
    @CompoundOverride(parent = RecipeMojang.class, method = "matchesV_1300")
    default boolean matchesV_1300(InventoryCrafting inventory, World world)
    {
        return this.matches(this.inputV_2100(inventory));
    }
    @Override
    @VersionRange(begin = 1300, end = 2100)
    @CompoundOverride(parent = RecipeMojang.class, method = "matchesV1300_2100")
    default boolean matchesV1300_2100(Inventory inventory, World world)
    {
        return this.matches(this.inputV_2100(inventory));
    }
    @Override
    @VersionRange(begin = 2100)
    @CompoundOverride(parent = RecipeMojang.class, method = "matchesV2100")
    default boolean matchesV2100(RecipeInputV2100 input, World world)
    {
        return this.matches(this.inputV2100(input));
    }

    @Override
    @VersionRange(end = 1100)
    @CompoundOverride(parent = RecipeMojang.class, method = "getRemainders0V_1100")
    default ItemStack.Array getRemainders0V_1100(InventoryCrafting inventory)
    {
        return this.getDisplay().getRemainders0V_1100(inventory);
    }
    @Override
    @VersionRange(begin = 1100, end = 1300)
    @CompoundOverride(parent = RecipeMojang.class, method = "getRemainders0V1100_1300")
    default DefaultedListV1100<?> getRemainders0V1100_1300(InventoryCrafting inventory)
    {
        return this.getDisplay().getRemainders0V1100_1300(inventory);
    }
    @Override
    @VersionRange(begin = 1300, end = 2100)
    @CompoundOverride(parent = RecipeMojang.class, method = "getRemainders0V1300_2100")
    default DefaultedListV1100<?> getRemainders0V1300_2100(Inventory inventory)
    {
        return this.getDisplay().getRemainders0V1300_2100(inventory);
    }
    @Override
    @VersionRange(begin = 2100, end = 2102)
    @CompoundOverride(parent = RecipeMojang.class, method = "getRemainders0V2100_2102")
    default DefaultedListV1100<?> getRemainders0V2100_2102(RecipeInputV2100 input)
    {
        return this.getDisplay().getRemainders0V2100_2102(input);
    }

    @Override
    @VersionRange(end = 1904)
    @CompoundOverride(parent = RecipeMojang.class, method = "getIconV_1904")
    default ItemStack getIconV_1904()
    {
        return this.getDisplay().getIconV_1904();
    }
    @Override
    @VersionRange(begin = 1904, end = 2005)
    @CompoundOverride(parent = RecipeMojang.class, method = "getIconV1904_2005")
    default ItemStack getIconV1904_2005(RegistryManagerV1602 registryManager)
    {
        return this.getDisplay().getIconV1904_2005(registryManager);
    }
    @Override
    @VersionRange(begin = 2005, end = 2102)
    @CompoundOverride(parent = RecipeMojang.class, method = "getIconV2005_2102")
    default ItemStack getIconV2005_2102(RegistryWrapperV1903.class_7874 registriesLookup)
    {
        return this.getDisplay().getIconV2005_2102(registriesLookup);
    }

    @Override
    @VersionRange(end = 1300)
    @CompoundOverride(parent = RecipeMojang.class, method = "getResultV_1300")
    default ItemStack getResultV_1300(InventoryCrafting inventory)
    {
        return this.getResult(this.inputV_2100(inventory));
    }
    @Override
    @VersionRange(begin = 1300, end = 1904)
    @CompoundOverride(parent = RecipeMojang.class, method = "getResultV1300_1904")
    default ItemStack getResultV1300_1904(Inventory inventory)
    {
        return this.getResult(this.inputV_2100(inventory));
    }
    @Override
    @VersionRange(begin = 1904, end = 2005)
    @CompoundOverride(parent = RecipeMojang.class, method = "getResultV1904_2005")
    default ItemStack getResultV1904_2005(Inventory inventory, RegistryManagerV1602 registryManager)
    {
        return this.getResult(this.inputV_2100(inventory));
    }
    @Override
    @VersionRange(begin = 2005, end = 2100)
    @CompoundOverride(parent = RecipeMojang.class, method = "getResultV2005_2100")
    default ItemStack getResultV2005_2100(Inventory inventory, RegistryWrapperV1903.class_7874 lookup)
    {
        return this.getResult(this.inputV_2100(inventory));
    }
    @Override
    @VersionRange(begin = 2100)
    @CompoundOverride(parent = RecipeMojang.class, method = "getResultV2100")
    default ItemStack getResultV2100(RecipeInputV2100 input, RegistryWrapperV1903.class_7874 lookup)
    {
        return this.getResult(this.inputV2100(input));
    }

    @Override
    @VersionRange(begin = 1700)
    @CompoundOverride(parent = RecipeMojang.class, method = "getGroup0V1700")
    default String getGroup0V1700()
    {
        return this.getGroupV1700().unwrapOr("");
    }

    @VersionRange(begin = 1300, end = 2002)
    @CompoundOverride(parent = RecipeMojang.class, method = "getIdV1300_2002")
    default Identifier getIdV1300_2002$impl()
    {
        return this.getIdV1300_2002();
    }

    @VersionRange(begin = 1300)
    @CompoundOverride(parent = RecipeMojang.class, method = "getSerializerV1300")
    default RecipeSerializerV1300 getSerializerV1300$impl()
    {
        return this.getSerializerV1300();
    }

    @VersionRange(begin = 1200, end = 2102)
    @CompoundOverride(parent = RecipeMojang.class, method = "getIngredients0V1200_2102")
    default DefaultedListV1100<?> getIngredients0V1200_2102$impl()
    {
        return this.getIngredients0V1200_2102();
    }
    @VersionRange(begin = 2102)
    @CompoundOverride(parent = RecipeMojang.class, method = "getIngredientPlacementV2102")
    default IngredientPlacementV2102 getIngredientPlacementV2102$impl()
    {
        return this.getIngredientPlacementV2102();
    }

    @VersionRange(begin = 1700, end = 2102)
    @CompoundOverride(parent = RecipeMojang.class, method = "fitsV1700_2102")
    default boolean fitsV1700_2102$impl(int width, int height)
    {
        return this.fitsV1700_2102(width, height);
    }

    @Override
    @VersionRange(begin = 2102)
    @CompoundOverride(parent = RecipeMojang.class, method = "getDisplays0V2102")
    default List<Object> getDisplays0V2102()
    {
        return new ListProxy<>(
            this.getDisplaysV2102(), FunctionInvertible.wrapper(RecipeDisplayV2102.FACTORY).inverse());
    }

    @Override
    @MinecraftPlatform.Enabled(MinecraftPlatform.Tag.BUKKIT)
    @VersionRange(end = 1300)
    @CompoundOverride(parent = RecipeMojang.class, method = "setKeyBukkitV1200_1300")
    default void setKeyBukkitV1200_1300(Identifier id)
    {
    }

    @Override
    @MinecraftPlatform.Enabled(MinecraftPlatform.Tag.BUKKIT)
    @VersionRange(end = 2002)
    @CompoundOverride(parent = RecipeMojang.class, method = "toBukkitV_2002")
    default BukkitRecipe toBukkitV_2002()
    {
        return BukkitRecipe.of(this);
    }
    @Override
    @MinecraftPlatform.Enabled(MinecraftPlatform.Tag.BUKKIT)
    @VersionRange(begin = 2002)
    @CompoundOverride(parent = RecipeMojang.class, method = "toBukkitV2002")
    default BukkitRecipe toBukkitV2002(BukkitNamespacedKeyV1200 id)
    {
        return BukkitRecipe.of(this);
    }


    class Module extends MzModule
    {
        public static final Module instance = new Module();

        @Override
        public void onLoad()
        {
            this.registerIfEnabled(NothingPacketS2cRecipesV1300_2102.class);
        }

        @VersionRange(begin = 1300, end = 2102)
        @WrapSameClass(PacketS2cRecipesV1300_2102.class)
        public interface NothingPacketS2cRecipesV1300_2102 extends Nothing, PacketS2cRecipesV1300_2102
        {
            @WrapConstructor
            PacketS2cRecipesV1300_2102 static$of0(Collection<Object> recipes);
            @NothingInject(
                wrapperMethodName = "static$of0", wrapperMethodParams = { Collection.class },
                locateMethod = "locateAllReturn", type = NothingInjectType.INSERT_BEFORE
            )
            void of$end();

            @SpecificImpl("of$end")
            @VersionRange(end = 2002)
            default void of$endV_2002()
            {
                this.setRecipes0(this.getRecipesV_2002().stream().map(this::handle).map(WrapperObject::getWrapped).collect(Collectors.toList()));
            }
            @SpecificImpl("of$end")
            @VersionRange(begin = 2002)
            default void of$endV2002()
            {
                this.setRecipes0(this.getRecipesV2002().stream().map(recipe ->
                    RecipeEntryV2002.of(recipe.getId(), handle(recipe.getValue())))
                    .map(WrapperObject::getWrapped).collect(Collectors.toList()));
            }

            default RecipeMojang handle(RecipeMojang recipe)
            {
                for(RecipeMojangAbstract<?> recipeMojangAbstract : recipe.asCompound().filter(RecipeMojangAbstract.class))
                {
                    return recipeMojangAbstract.getDisplay();
                }
                return recipe;
            }
        }
    }
}
