package mz.mzlib.minecraft.mzitem;

import mz.mzlib.data.DataHandler;
import mz.mzlib.data.DataKey;
import mz.mzlib.event.EventListener;
import mz.mzlib.i18n.I18n;
import mz.mzlib.minecraft.Identifier;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.minecraft.event.player.async.EventAsyncPlayerDisplayItem;
import mz.mzlib.minecraft.i18n.MinecraftI18n;
import mz.mzlib.minecraft.inventory.InventoryCrafting;
import mz.mzlib.minecraft.item.Item;
import mz.mzlib.minecraft.item.ItemStack;
import mz.mzlib.minecraft.nbt.NbtCompound;
import mz.mzlib.minecraft.recipe.IngredientVanilla;
import mz.mzlib.minecraft.recipe.crafting.RecipeCraftingShapedVanilla;
import mz.mzlib.minecraft.recipe.crafting.RecipeCraftingShapelessVanilla;
import mz.mzlib.minecraft.text.Text;
import mz.mzlib.minecraft.world.World;
import mz.mzlib.module.MzModule;
import mz.mzlib.util.Editor;
import mz.mzlib.util.Option;
import mz.mzlib.util.ThrowablePredicate;
import mz.mzlib.util.nothing.LocalVar;
import mz.mzlib.util.nothing.Nothing;
import mz.mzlib.util.nothing.NothingInject;
import mz.mzlib.util.nothing.NothingInjectType;
import mz.mzlib.util.wrapper.CallOnce;
import mz.mzlib.util.wrapper.WrapSameClass;
import mz.mzlib.util.wrapper.basic.Wrapper_boolean;

import java.util.Collections;
import java.util.List;

@WrapSameClass(ItemStack.class)
public interface MzItem extends ItemStack
{
    Identifier static$getMzId();

    ItemStack static$vanilla();

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    default boolean isVanilla()
    {
        return false;
    }

    @CallOnce
    default void init()
    {
        for(NbtCompound customData : Item.CUSTOM_DATA.revise(this))
        {
            for(NbtCompound mz : customData.reviseNbtCompoundOrNew("mz"))
            {
                mz.put("id", this.getMzId().toString());
            }
        }
    }

    @CallOnce
    default void onDisplay(EventAsyncPlayerDisplayItem event)
    {
        Identifier id = this.getMzId();
        String key = id.getNamespace() + ".item." + id.getName();
        String lang = event.getPlayer().getLanguage();
        Object args = getDisplayArgs(lang);
        if(Item.CUSTOM_NAME.get(event.getItemStack()).isNone())
        {
            for(ItemStack itemStack : event.reviseItemStack())
            {
                Item.CUSTOM_NAME.set(itemStack, Option.some(MinecraftI18n.resolveText(lang, key, args)));
            }
        }
        key += ".lore";
        if(I18n.getSource(lang, key, null) != null)
        {
            for(ItemStack itemStack : event.reviseItemStack())
            {
                for(List<Text> lore : Item.LORE.revise(itemStack))
                {
                    lore.addAll(0, MinecraftI18n.resolveTexts(lang, key, args));
                }
            }
        }
    }
    /**
     * called async
     */
    default Object getDisplayArgs(String lang)
    {
        return Collections.emptyMap();
    }

    default Identifier getMzId()
    {
        return this.static$getMzId();
    }

    DataKey<MzItem, Option<NbtCompound>, NbtCompound> MZ_DATA = new DataKey<>("mz_data");

    @Deprecated
    default Option<NbtCompound> getMzData()
    {
        return MZ_DATA.get(this);
    }

    @Deprecated
    default Editor<NbtCompound> reviseMzData()
    {
        return MZ_DATA.revise(this);
    }

    class Module extends MzModule
    {
        public static Module instance = new Module();

        @Override
        public void onLoad()
        {
            DataHandler.builder(MZ_DATA)
                .getter(is ->
                {
                    for(NbtCompound customData : Item.CUSTOM_DATA.get(is))
                        for(NbtCompound mz : customData.getNbtCompound("mz"))
                            return mz.getNbtCompound("data");
                    return Option.none();
                })
                .setter((is, data) ->
                {
                    for(NbtCompound customData : Item.CUSTOM_DATA.revise(is))
                    {
                        for(NbtCompound mz : customData.reviseNbtCompoundOrNew("mz"))
                        {
                            for(NbtCompound d : data)
                            {
                                mz.put("data", d);
                            }
                            if(data.isNone())
                                mz.remove("data");
                        }
                    }
                })
                .reviserGetter(o -> o.map(NbtCompound::clone).unwrapOrGet(NbtCompound::newInstance))
                .reviserApplier(data -> Option.some(data).filter(ThrowablePredicate.of(NbtCompound::isEmpty).negate()))
                .register(this);
            this.register(NothingIngredientVanilla.class);
            this.registerIfEnabled(NothingRecipeCraftingShapedVanillaV_1200.class);

            this.register(RegistrarMzItem.instance);

            this.register(new EventListener<>(EventAsyncPlayerDisplayItem.class, this::onAsyncPlayerDisplayItem));

            this.register(MzItemUsable.Module.instance);

            this.register(MzItemDebugStick.class);
            this.register(MzItemIconPlaceholder.class);
        }

        public void onAsyncPlayerDisplayItem(EventAsyncPlayerDisplayItem event)
        {
            for(MzItem mzItem : RegistrarMzItem.instance.toMzItem(event.getItemStack()))
            {
                mzItem.onDisplay(event);
            }
        }

        static Wrapper_boolean handleVanilla(ItemStack is)
        {
            for(MzItem mzItem : RegistrarMzItem.instance.toMzItem(is))
            {
                if(!mzItem.isVanilla())
                    return Wrapper_boolean.FACTORY.create(false);
            }
            return Nothing.notReturn();
        }
        @WrapSameClass(IngredientVanilla.class)
        public interface NothingIngredientVanilla extends Nothing, IngredientVanilla
        {
            @VersionRange(begin = 1200)
            @NothingInject(
                wrapperMethodName = "testV1200",
                wrapperMethodParams = { ItemStack.class },
                locateMethod = "",
                type = NothingInjectType.INSERT_BEFORE
            )
            default Wrapper_boolean testV1200$begin(@LocalVar(1) ItemStack itemStack)
            {
                return handleVanilla(itemStack);
            }
        }

        static Wrapper_boolean handleRecipeV_1200(InventoryCrafting inventory)
        {
            for(int size = inventory.size(), i = 0; i < size; i++)
            {
                Wrapper_boolean result = handleVanilla(inventory.getItemStack(i));
                if(Nothing.isReturn(result))
                    return result;
            }
            return Nothing.notReturn();
        }
        @VersionRange(end = 1200)
        @WrapSameClass(RecipeCraftingShapedVanilla.class)
        public interface NothingRecipeCraftingShapedVanillaV_1200 extends Nothing, RecipeCraftingShapedVanilla
        {
            @NothingInject(
                wrapperMethodName = "matchesV_1300",
                wrapperMethodParams = { InventoryCrafting.class, World.class },
                locateMethod = "",
                type = NothingInjectType.INSERT_BEFORE
            )
            default Wrapper_boolean matchesV_1300$begin(@LocalVar(1) InventoryCrafting inventory)
            {
                return handleRecipeV_1200(inventory);
            }
        }
        @VersionRange(end = 1200)
        @WrapSameClass(RecipeCraftingShapelessVanilla.class)
        public interface NothingRecipeCraftingShapelessVanillaV_1200 extends Nothing, RecipeCraftingShapelessVanilla
        {
            @NothingInject(
                wrapperMethodName = "matchesV_1300",
                wrapperMethodParams = { InventoryCrafting.class, World.class },
                locateMethod = "",
                type = NothingInjectType.INSERT_BEFORE
            )
            default Wrapper_boolean matchesV_1300$begin(@LocalVar(1) InventoryCrafting inventory)
            {
                return handleRecipeV_1200(inventory);
            }
        }
        // TODO: other recipe type V_1200
    }
}
