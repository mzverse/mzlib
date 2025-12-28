package mz.mzlib.minecraft.item;

import com.google.gson.JsonElement;
import mz.mzlib.data.DataHandler;
import mz.mzlib.data.DataKey;
import mz.mzlib.minecraft.*;
import mz.mzlib.minecraft.component.ComponentKeyV2005;
import mz.mzlib.minecraft.component.type.LoreComponentV2005;
import mz.mzlib.minecraft.component.type.NbtCompoundComponentV2005;
import mz.mzlib.minecraft.i18n.VanillaI18nV_1300;
import mz.mzlib.minecraft.nbt.NbtCompound;
import mz.mzlib.minecraft.nbt.NbtList;
import mz.mzlib.minecraft.nbt.NbtString;
import mz.mzlib.minecraft.registry.RegistriesV1300;
import mz.mzlib.minecraft.registry.Registry;
import mz.mzlib.minecraft.registry.RegistrySimple;
import mz.mzlib.minecraft.text.Text;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.minecraft.wrapper.WrapMinecraftFieldAccessor;
import mz.mzlib.minecraft.wrapper.WrapMinecraftMethod;
import mz.mzlib.module.MzModule;
import mz.mzlib.util.*;
import mz.mzlib.util.proxy.ListProxy;
import mz.mzlib.util.wrapper.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@WrapMinecraftClass(@VersionName(name = "net.minecraft.item.Item"))
public interface Item extends WrapperObject
{
    WrapperFactory<Item> FACTORY = WrapperFactory.of(Item.class);

    static Item fromId(Identifier id)
    {
        return Option.fromWrapper(getRegistry().get(id)).unwrap(() -> new IllegalArgumentException("Unknown item id: " + id)).as(Item.FACTORY);
    }

    static Item fromId(String id)
    {
        return fromId(Identifier.of(id));
    }

    DataKey<ItemStack, Option<NbtCompound>, NbtCompound> CUSTOM_DATA = new DataKey<>("custom_data");
    DataKey<ItemStack, Option<Text>, Void> CUSTOM_NAME = new DataKey<>("custom_name");
    DataKey<ItemStack, Option<List<Text>>, List<Text>> LORE = new DataKey<>("lore");

    String TAG_KEY_ENCHANTMENTS_V_2005 = MinecraftPlatform.instance.getVersion() < 1300 ? "ench" : "Enchantments";
    String TAG_KEY_HIDE_FLAGS_V_2005 = "HideFlags";

    ComponentKeyV2005.Wrapper<NbtCompoundComponentV2005> COMPONENT_KEY_CUSTOM_DATA_V2005 = componentKeyOrNull("custom_data", () -> NbtCompoundComponentV2005.FACTORY);
    ComponentKeyV2005.Wrapper<Text> COMPONENT_KEY_CUSTOM_NAME_V2005 = componentKeyOrNull("custom_name", Text.FACTORY);
    ComponentKeyV2005.Wrapper<LoreComponentV2005> COMPONENT_KEY_LORE_V2005 = componentKeyOrNull("lore", () -> LoreComponentV2005.FACTORY);
    ComponentKeyV2005<Boolean> COMPONENT_KEY_ENCHANTMENT_GLINT_OVERRIDE_V2005 = componentKeyOrNull("enchantment_glint_override");

    @VersionRange(begin = 1300)
    default V1300 v1300()
    {
        return this.as(V1300.FACTORY);
    }

    Identifier getId();

    static Registry<Item> getRegistry()
    {
        return FACTORY.getStatic().static$getRegistry();
    }
    static RegistrySimple<Item> getRegistryV_1300()
    {
        return FACTORY.getStatic().static$getRegistryV_1300();
    }

    @VersionRange(end = 1400)
    @WrapMinecraftMethod(@VersionName(name = "getDisplayName"))
    String getNameLocalizedV_1400(ItemStack itemStack);

    String getTranslationKeyV_2102(ItemStack itemStack);

    @VersionRange(begin = 1300)
    @WrapMinecraftMethod({
        @VersionName(name = "getDisplayName", end = 1400),
        @VersionName(name = "getName", begin = 1400)
    })
    Text getNameV1300(ItemStack itemStack);

    static void makeEnchantmentGlint(ItemStack is)
    {
        FACTORY.getStatic().static$makeEnchantmentGlint(is);
    }

    static <T> ComponentKeyV2005<T> componentKeyOrNull(String id)
    {
        if(MinecraftPlatform.instance.getVersion() < 2005)
            return RuntimeUtil.nul();
        return ComponentKeyV2005.fromId(id);
    }
    static <T extends WrapperObject> ComponentKeyV2005.Wrapper<T> componentKeyOrNull(String id, WrapperFactory<T> type)
    {
        return componentKeyOrNull(id, ThrowableSupplier.constant(type));
    }
    static <T extends WrapperObject> ComponentKeyV2005.Wrapper<T> componentKeyOrNull(String id, Supplier<WrapperFactory<T>> typeSupplier)
    {
        if(MinecraftPlatform.instance.getVersion() < 2005)
            return RuntimeUtil.nul();
        return ComponentKeyV2005.fromId(id, typeSupplier.get());
    }


    @SpecificImpl("getId")
    @VersionRange(end = 1300)
    default Identifier getIdV_1300()
    {
        return getRegistryV_1300().getId(this);
    }
    @SpecificImpl("getId")
    @VersionRange(begin = 1300)
    default Identifier getIdV1300()
    {
        return getRegistryV1300().getIdV1300(this);
    }

    Registry<Item> static$getRegistry();
    @SpecificImpl("static$getRegistry")
    @VersionRange(end = 1300)
    @WrapMinecraftFieldAccessor(@VersionName(name = "REGISTRY"))
    RegistrySimple<Item> static$getRegistryV_1300();
    static Registry<Item> getRegistryV1300()
    {
        return FACTORY.getStatic().static$getRegistryV1300();
    }
    @SpecificImpl("static$getRegistry")
    @VersionRange(begin = 1300)
    default Registry<Item> static$getRegistryV1300()
    {
        return RegistriesV1300.item();
    }

    @SpecificImpl("getTranslationKeyV_2102")
    @VersionRange(end = 1300)
    default String getTranslationKeyV_1300(ItemStack itemStack)
    {
        VanillaI18nV_1300.lastKey.set(new RefStrong<>(null));
        try
        {
            String ignored = this.getNameLocalizedV_1400(itemStack);
            return VanillaI18nV_1300.lastKey.get().get();
        }
        finally
        {
            VanillaI18nV_1300.lastKey.remove();
        }
    }
    @SpecificImpl("getTranslationKeyV_2102")
    @VersionRange(begin = 1300, end = 2102)
    @WrapMinecraftMethod(@VersionName(name = "getTranslationKey"))
    String getTranslationKeyV1300_2102(ItemStack itemStack);

    void static$makeEnchantmentGlint(ItemStack is);
    @SpecificImpl("static$makeEnchantmentGlint")
    @VersionRange(end = 2005)
    default void static$makeEnchantmentGlintV_2005(ItemStack is)
    {
        for(NbtCompound tag : CUSTOM_DATA.get(is))
        {
            for(NbtList enchantments : tag.getNbtList(TAG_KEY_ENCHANTMENTS_V_2005))
            {
                if(enchantments.size()>0)
                    return;
            }
            for(NbtList enchantments : tag.getNbtList("StoredEnchantments"))
            {
                if(enchantments.size()>0)
                    return;
            }
        }
        for(NbtCompound tag : CUSTOM_DATA.revise(is))
        {
            // 不同时设置id和lvl，否则升级到1.20.5+会报错
            tag.put(TAG_KEY_ENCHANTMENTS_V_2005, NbtList.newInstance(NbtCompound.newInstance()));
            tag.put(TAG_KEY_HIDE_FLAGS_V_2005, tag.getInt(TAG_KEY_HIDE_FLAGS_V_2005).unwrapOr(0) | 1);
        }
    }
    @SpecificImpl("static$makeEnchantmentGlint")
    @VersionRange(begin = 2005)
    default void static$makeEnchantmentGlintV2005(ItemStack is)
    {
        is.getComponentsV2005().put(COMPONENT_KEY_ENCHANTMENT_GLINT_OVERRIDE_V2005, true);
    }

    @VersionRange(begin = 1300)
    @WrapSameClass(Item.class)
    interface V1300 extends WrapperObject, Item, ItemConvertibleV1300
    {
        WrapperFactory<V1300> FACTORY = WrapperFactory.of(V1300.class);
    }

    class Module extends MzModule
    {
        public static Module instance = new Module();

        @Override
        public void onLoad()
        {
            this.register(ItemPlayerHead.Module.instance);
            this.register(ItemWrittenBook.Module.instance);

            (MinecraftPlatform.instance.getVersion() < 2005 ?
                DataHandler.builder(CUSTOM_DATA)
                    .getter(ItemStack::getTagV_2005)
                    .setter(ItemStack::setTagV_2005)
                :
                DataHandler.builder(CUSTOM_DATA)
                    .getter(is -> is.getComponentsV2005().get(COMPONENT_KEY_CUSTOM_DATA_V2005)
                        .map(NbtCompoundComponentV2005::getNbtCompound))
                    .setter((is, value) -> is.getComponentsV2005()
                        .set(COMPONENT_KEY_CUSTOM_DATA_V2005, value.map(NbtCompoundComponentV2005::newInstance)))
            )
                .reviserGetter(data -> data.map(NbtCompound::clone0).unwrapOrGet(NbtCompound::newInstance))
                .reviserApplier(reviser -> Option.some(reviser)
                    .filter(ThrowablePredicate.ofPredicate(NbtCompound::isEmpty).negate()))
                .register(this);

            if(MinecraftPlatform.instance.getVersion() < 2005)
            {
                final Function<String, Text> customNameDecoder;
                final Function<Text, String> customNameEncoder;
                if(MinecraftPlatform.instance.getVersion() < 1300)
                {
                    customNameDecoder = Text::fromLegacy;
                    customNameEncoder = Text::toLegacy;
                }
                else
                {
                    customNameDecoder = Text::decode;
                    customNameEncoder = ThrowableFunction.ofFunction(Text::encode).thenApply(JsonElement::toString);
                }
                DataHandler.builder(CUSTOM_NAME)
                    .getter(is ->
                    {
                        for(NbtCompound nbt : is.getTagV_2005())
                        {
                            for(NbtCompound display : nbt.getNbtCompound("display"))
                            {
                                for(String name : display.getString("Name"))
                                {
                                    return Option.some(customNameDecoder.apply(name));
                                }
                            }
                        }
                        return Option.none();
                    })
                    .setter((is, value) ->
                    {
                        for(NbtCompound tag : CUSTOM_DATA.revise(is))
                        {
                            for(NbtCompound display : tag.reviseNbtCompoundOrNew("display"))
                            {
                                if(value.isSome())
                                    display.put("Name", customNameEncoder.apply(value.unwrap()));
                                else
                                    display.remove("Name");
                            }
                        }
                    })
                    .register(this);
            }
            else
                DataHandler.builder(CUSTOM_NAME)
                    .getter(is -> is.getComponentsV2005().get(COMPONENT_KEY_CUSTOM_NAME_V2005))
                    .setter((is, value) -> is.getComponentsV2005().set(COMPONENT_KEY_CUSTOM_NAME_V2005, value))
                    .register(this);

            if(MinecraftPlatform.instance.getVersion() < 2005)
            {
                final Function<String, Text> loreLineDecoder;
                final Function<Text, String> loreLineEncoder;
                if(MinecraftPlatform.instance.getVersion() < 1400)
                {
                    loreLineDecoder = Text::fromLegacy;
                    loreLineEncoder = Text::toLegacy;
                }
                else
                {
                    loreLineDecoder = Text::decode;
                    loreLineEncoder = ThrowableFunction.ofFunction(Text::encode).thenApply(JsonElement::toString);
                }
                FunctionInvertible<NbtString, Text> proxyFunction = FunctionInvertible.of(
                    ThrowableFunction.of(NbtString::getValue).thenApply(loreLineDecoder),
                    loreLineEncoder.andThen(NbtString::newInstance)
                );
                DataHandler.builder(LORE)
                    .getter(is ->
                    {
                        for(NbtCompound tag : is.getTagV_2005())
                        {
                            for(NbtCompound display : tag.getNbtCompound("display"))
                            {
                                for(NbtList lore : display.getNbtList("Lore"))
                                {
                                    return Option.some(new ListProxy<>(
                                        new ArrayList<>(lore.asList(NbtString.FACTORY)), // copy
                                        proxyFunction
                                    ));
                                }
                            }
                        }
                        return Option.none();
                    })
                    .setter((is, value) ->
                    {
                        for(NbtCompound tag : CUSTOM_DATA.revise(is))
                        {
                            l1:
                            for(NbtCompound display : tag.reviseNbtCompoundOrNew("display"))
                            {
                                for(List<Text> unwrap : value)
                                {
                                    if(unwrap instanceof ListProxy &&
                                        ((ListProxy<?, ?>) unwrap).getFunction() == proxyFunction)
                                        display.put(
                                            "Lore", NbtList.newInstance(
                                                ((ListProxy<Text, NbtString>) unwrap).getDelegate())
                                        );
                                    else
                                        display.put(
                                            "Lore", NbtList.newInstance(
                                                unwrap.stream()
                                                    .map(loreLineEncoder)
                                                    .map(NbtString::newInstance)
                                                    .toArray(NbtString[]::new))
                                        );
                                    continue l1;
                                }
                                display.remove("Lore");
                            }
                        }
                    })
                    .reviserGetter(lore -> lore.unwrapOrGet(ArrayList::new))
                    .reviserApplier(reviser -> Option.some(reviser)
                        .filter(ThrowablePredicate.ofPredicate(List<Text>::isEmpty).negate()))
                    .register(this);
            }
            else
                DataHandler.builder(LORE)
                    .getter(is -> is.getComponentsV2005()
                        .get(COMPONENT_KEY_LORE_V2005).map(LoreComponentV2005::getLines))
                    .setter((is, value) -> is.getComponentsV2005()
                        .set(COMPONENT_KEY_LORE_V2005, value.map(LoreComponentV2005::newInstance)))
                    .reviserGetter(lore -> lore.map(ArrayList::new).unwrapOrGet(ArrayList::new))
                    .reviserApplier(reviser -> Option.some(reviser)
                        .filter(ThrowablePredicate.ofPredicate(List<Text>::isEmpty).negate()))
                    .register(this);
        }
    }


    /**
     * @see #CUSTOM_DATA
     */
    @Deprecated
    static Option<NbtCompound> getCustomData(ItemStack itemStack)
    {
        return CUSTOM_DATA.get(itemStack);
    }
    /**
     * @see #CUSTOM_DATA
     */
    @Deprecated
    static void setCustomData(ItemStack itemStack, Option<NbtCompound> value)
    {
        CUSTOM_DATA.set(itemStack, value);
    }
    /**
     * @see #CUSTOM_DATA
     */
    @Deprecated
    static Editor<NbtCompound> reviseCustomData(ItemStack itemStack)
    {
        return CUSTOM_DATA.revise(itemStack);
    }

    /**
     * @see #CUSTOM_DATA
     * @deprecated slow; cannot break
     */
    @Deprecated
    static Editor<Ref<Option<NbtCompound>>> editCustomData(ItemStack itemStack)
    {
        return FACTORY.getStatic().static$editCustomData(itemStack);
    }
    Editor<Ref<Option<NbtCompound>>> static$editCustomData(ItemStack itemStack);
    @SpecificImpl("static$editCustomData")
    @VersionRange(end = 2005)
    default Editor<Ref<Option<NbtCompound>>> static$editCustomDataV_2005(ItemStack itemStack)
    {
        return Editor.ofRef(itemStack, CUSTOM_DATA::get, CUSTOM_DATA::set);
    }
    @SpecificImpl("static$editCustomData")
    @VersionRange(begin = 2005)
    default Editor<Ref<Option<NbtCompound>>> static$editCustomDataV2005(ItemStack itemStack)
    {
        return Editor.ofRef(
            itemStack,
            ThrowableFunction.of(CUSTOM_DATA::get).thenApply(ThrowableFunction.optionMap(NbtCompound::copy)),
            CUSTOM_DATA::set
        );
    }

    /**
     * @see #CUSTOM_NAME
     */
    @Deprecated
    static Option<Text> getCustomName(ItemStack itemStack)
    {
        return CUSTOM_NAME.get(itemStack);
    }
    /**
     * @see #CUSTOM_NAME
     */
    @Deprecated
    static void setCustomName(ItemStack itemStack, Option<Text> value)
    {
        CUSTOM_NAME.set(itemStack, value);
    }
    /**
     * @see #CUSTOM_NAME
     */
    @Deprecated
    static Editor<Ref<Option<Text>>> editCustomName(ItemStack itemStack)
    {
        return Editor.ofRef(itemStack, Item::getCustomName, Item::setCustomName);
    }

    /**
     * @see #LORE
     */
    @Deprecated
    static Option<List<Text>> getLore(ItemStack itemStack)
    {
        return LORE.get(itemStack);
    }
    /**
     * @see #LORE
     */
    @Deprecated
    static void setLore(ItemStack itemStack, Option<List<Text>> value)
    {
        LORE.set(itemStack, value);
    }
    /**
     * @see #LORE
     */
    @Deprecated
    static Editor<List<Text>> reviseLore(ItemStack itemStack)
    {
        return LORE.revise(itemStack);
    }

    @Deprecated
    static Option<List<Text>> copyLore(ItemStack itemStack)
    {
        return FACTORY.getStatic().static$copyLore(itemStack);
    }
    Option<List<Text>> static$copyLore(ItemStack itemStack);
    @SpecificImpl("static$copyLore")
    @VersionRange(end = 2005)
    default Option<List<Text>> static$copyLoreV_2005(ItemStack itemStack)
    {
        return LORE.get(itemStack);
    }
    @SpecificImpl("static$copyLore")
    @VersionRange(begin = 2005)
    default Option<List<Text>> static$copyLoreV2005(ItemStack itemStack)
    {
        return LORE.get(itemStack).map(ArrayList::new);
    }

    /**
     * @see #reviseLore
     * @deprecated slow
     */
    @Deprecated
    static Editor<Ref<Option<List<Text>>>> editLore(ItemStack itemStack)
    {
        return Editor.ofRef(itemStack, Item::copyLore, LORE::set);
    }
}
