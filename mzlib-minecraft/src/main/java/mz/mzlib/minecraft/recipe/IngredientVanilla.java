package mz.mzlib.minecraft.recipe;

import mz.mzlib.minecraft.*;
import mz.mzlib.minecraft.item.Item;
import mz.mzlib.minecraft.item.ItemConvertibleV1300;
import mz.mzlib.minecraft.item.ItemStack;
import mz.mzlib.minecraft.item.ItemTagsV1300;
import mz.mzlib.minecraft.mzitem.MzItem;
import mz.mzlib.minecraft.mzitem.RegistrarMzItem;
import mz.mzlib.minecraft.registry.RegistryKeysV1600;
import mz.mzlib.minecraft.registry.entry.RegistryEntryListV1903;
import mz.mzlib.minecraft.registry.entry.RegistryEntryV1802;
import mz.mzlib.minecraft.registry.entry.RegistryWrapperV1903;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.minecraft.wrapper.WrapMinecraftFieldAccessor;
import mz.mzlib.minecraft.wrapper.WrapMinecraftMethod;
import mz.mzlib.util.FunctionInvertible;
import mz.mzlib.util.Option;
import mz.mzlib.util.RuntimeUtil;
import mz.mzlib.util.proxy.ListProxy;
import mz.mzlib.util.wrapper.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@WrapMinecraftClass(
    {
        @VersionName(name = "net.minecraft.item.ItemStack", end = 1200),
        @VersionName(name = "net.minecraft.recipe.Ingredient", begin = 1200)
    }
)
public interface IngredientVanilla extends WrapperObject, Ingredient
{
    WrapperFactory<IngredientVanilla> FACTORY = WrapperFactory.of(IngredientVanilla.class);

    IngredientVanilla WOOL = ofCategory("wool");
    IngredientVanilla PLANKS = ofCategory("planks");

    IngredientVanilla EMPTY_V_2102 = MinecraftPlatform.instance.getVersion() < 2102 ? FACTORY.getStatic().static$emptyV_2102() :
        RuntimeUtil.nul();

    static IngredientVanilla fromOptionV_2102(Option<IngredientVanilla> option)
    {
        return option.unwrapOrGet(() -> EMPTY_V_2102);
    }
    default Option<IngredientVanilla> toOptionV_2102()
    {
        if(this.equals(EMPTY_V_2102))
            return Option.none();
        return Option.some(this);
    }

    @Override
    boolean test(ItemStack itemStack);

    static IngredientVanilla of(ItemStack itemStack)
    {
        return FACTORY.getStatic().static$of(itemStack);
    }

    @VersionRange(begin = 1200)
    static IngredientVanilla ofV1200(List<ItemStack> itemStacks)
    {
        return FACTORY.getStatic().static$ofV1200(itemStacks);
    }
    @VersionRange(begin = 1200)
    static IngredientVanilla ofV1200(ItemStack... itemStacks)
    {
        return ofV1200(Arrays.asList(itemStacks));
    }
    @VersionRange(begin = 1200)
    static IngredientVanilla ofV1200(Item... items)
    {
        return FACTORY.getStatic().static$ofV1200(items);
    }
    @VersionRange(begin = 1300)
    static IngredientVanilla ofV1300(ItemConvertibleV1300... items)
    {
        return FACTORY.getStatic().static$ofV1300(
            Arrays.stream(items).collect(WrapperArray.collector(ItemConvertibleV1300.Array.FACTORY)));
    }

    @Override
    Collection<ItemStack> getExamples();

    @Override
    default ItemStack getRemainder(ItemStack itemStack)
    {
        throw new UnsupportedOperationException(); // TODO
    }

    @VersionRange(end = 1200)
    default ItemStack asItemStackV_1200()
    {
        return this.as(ItemStack.FACTORY);
    }

    @VersionRange(begin = 1200, end = 1300)
    default List<ItemStack> getDataV1200_1300()
    {
        return this.getData0V1200_1300().asList();
    }
    @VersionRange(begin = 1300)
    List<ItemStack> getMatchingStacksV1300();

    @WrapArrayClass(IngredientVanilla.class)
    interface Array extends WrapperArray<IngredientVanilla>
    {
        WrapperFactory<Array> FACTORY = WrapperFactory.of(Array.class);

        static Array newInstance(int size)
        {
            return (Array) FACTORY.getStatic().static$newInstance(size);
        }
    }

    static IngredientVanilla ofCategory(String id)
    {
        return ofCategory(Identifier.of(id));
    }
    static IngredientVanilla ofCategory(Identifier id)
    {
        return ofCategory(id, id);
    }
    static IngredientVanilla ofCategory(Identifier itemV_1300, Identifier tagV1300)
    {
        return FACTORY.getStatic().static$ofCategory(itemV_1300, tagV1300);
    }
    static IngredientVanilla ofV1300(TagV1300<Item> tag)
    {
        return FACTORY.getStatic().static$ofV1300(tag);
    }


    IngredientVanilla static$emptyV_2102();
    @SpecificImpl("static$emptyV_2102")
    @VersionRange(end = 1200)
    default IngredientVanilla static$emptyV_1200()
    {
        return ItemStack.EMPTY.as(FACTORY);
    }
    @SpecificImpl("static$emptyV_2102")
    @VersionRange(begin = 1200, end = 2102)
    @WrapMinecraftFieldAccessor(
        {
            @VersionName(name = "field_15680", end = 1400),
            @VersionName(name = "EMPTY", begin = 1400)
        }
    )
    IngredientVanilla static$emptyV1200_2102();

    @SpecificImpl("test")
    @VersionRange(end = 1200)
    default boolean testV_1200(ItemStack itemStack)
    {
        for(MzItem mzItem : RegistrarMzItem.instance.toMzItem(itemStack))
        {
            if(!mzItem.isVanilla())
                return false;
        }
        ItemStack thisItemStack = this.asItemStackV_1200();
        if(!itemStack.getItem().equals(thisItemStack.getItem()))
            return false;
        return thisItemStack.getDamageV_1300() == Short.MAX_VALUE ||
            thisItemStack.getDamageV_1300() == itemStack.getDamageV_1300();
    }

    @VersionRange(begin = 1200)
    @WrapMinecraftMethod(
        {
            @VersionName(name = "apply", remap = false, end = 1300),
            @VersionName(name = "test", remap = false, begin = 1300)
        }
    )
    boolean testV1200$bridge(Object itemStack);
    @SpecificImpl("test")
    @VersionRange(begin = 1200)
    @WrapMethodFromBridge(name = "testV1200$bridge", params = { Object.class })
    boolean testV1200(ItemStack itemStack);

    IngredientVanilla static$of(ItemStack itemStack);
    @SpecificImpl("static$of")
    @VersionRange(end = 1200)
    default IngredientVanilla static$ofV_1200(ItemStack itemStack)
    {
        return itemStack.as(FACTORY);
    }
    @SpecificImpl("static$of")
    @VersionRange(begin = 1200)
    default IngredientVanilla static$ofV1200(ItemStack itemStack)
    {
        return ofV1200(itemStack);
    }

    @VersionRange(begin = 1200)
    IngredientVanilla static$ofV1200(List<ItemStack> itemStacks);
    @SpecificImpl("static$ofV1200")
    @VersionRange(begin = 1200, end = 1300)
    default IngredientVanilla static$ofV1200_1300(List<ItemStack> itemStacks)
    {
        return FACTORY.getStatic().static$ofV1200_1300(
            itemStacks.stream().collect(WrapperArray.collector(ItemStack.Array.FACTORY)));
    }
    @SpecificImpl("static$ofV1200")
    @VersionRange(begin = 1300)
    default IngredientVanilla static$ofV1300(List<ItemStack> itemStacks)
    {
        return ofV1200(itemStacks.stream().map(ItemStack::getItem).toArray(Item[]::new));
    }

    IngredientVanilla static$ofV1200(Item... items);
    @SpecificImpl("static$ofV1200")
    @VersionRange(begin = 1200, end = 1300)
    default IngredientVanilla static$ofV1200_1300(Item... items)
    {
        return ofV1200(Arrays.stream(items).map(ItemStack::newInstance).toArray(ItemStack[]::new));
    }
    @SpecificImpl("static$ofV1200")
    @VersionRange(begin = 1300)
    default IngredientVanilla static$ofV1300(Item... items)
    {
        return ofV1300(Arrays.stream(items)
            .map(it -> it.as(Item.V1300.FACTORY)).toArray(ItemConvertibleV1300[]::new));
    }

    @VersionRange(begin = 1200, end = 1300)
    @WrapMinecraftMethod(@VersionName(name = "method_14248"))
    IngredientVanilla static$ofV1200_1300(ItemStack.Array itemStacks);

    @VersionRange(begin = 1300)
    @WrapMinecraftMethod(@VersionName(name = "ofItems"))
    IngredientVanilla static$ofV1300(ItemConvertibleV1300.Array items);

    IngredientVanilla static$ofCategory(Identifier itemV_1300, Identifier tagV1300);
    @SpecificImpl("static$ofCategory")
    @VersionRange(end = 1300)
    default IngredientVanilla static$ofCategoryV_1300(Identifier itemV_1300, Identifier tagV1300)
    {
        return of(ItemStack.builder().fromId(itemV_1300).damageV_1300(32767).build());
    }
    @SpecificImpl("static$ofCategory")
    @VersionRange(begin = 1300)
    default IngredientVanilla static$ofCategoryV1300(Identifier itemV_1300, Identifier tagV1300)
    {
        return ofV1300(ItemTagsV1300.of(tagV1300));
    }

    IngredientVanilla static$ofV1300(TagV1300<Item> tag);
    @SpecificImpl("static$ofV1300")
    @VersionRange(begin = 1300, end = 2102)
    @WrapMinecraftMethod(@VersionName(name = "fromTag"))
    IngredientVanilla static$ofV1300_2102(TagV1300<Item> tag);
    @SpecificImpl("static$ofV1300")
    @VersionRange(begin = 2102)
    default IngredientVanilla static$ofV2102(TagV1300<Item> tag)
    {
        return MinecraftServer.instance.getRegistriesV1802().as(RegistryWrapperV1903.class_7874.FACTORY)
            .get(RegistryKeysV1600.ITEM)
            .unwrap(IllegalStateException::new)
            .get(tag)
            .map(this::static$ofV2102)
            .unwrap(() -> new IllegalArgumentException(Objects.toString(tag)));
    }
    @VersionRange(begin = 2102)
    @WrapConstructor
    IngredientVanilla static$ofV2102(RegistryEntryListV1903 list);

    @SpecificImpl("getMatchingStacksV1300")
    @VersionRange(begin = 1300, end = 1701)
    default List<ItemStack> getMatchingStacksV1300_1701()
    {
        this.cacheMatchingStacksV1300_1903();
        return this.getCachedMatchingStacks0V1300_2102().asList();
    }

    @SpecificImpl("getMatchingStacksV1300")
    @VersionRange(begin = 1701, end = 2102)
    default List<ItemStack> getMatchingStacksV1701_2102()
    {
        return this.getMatchingStacks0V1701_2102().asList();
    }
    @VersionRange(begin = 1701, end = 2102)
    @WrapMinecraftMethod(@VersionName(name = "getMatchingStacks"))
    ItemStack.Array getMatchingStacks0V1701_2102();

    @SpecificImpl("getMatchingStacksV1300")
    @VersionRange(begin = 2102)
    default List<ItemStack> getMatchingStacksV2102()
    {
        return this.getMatchingItemsV2102().map(RegistryEntryV1802.Wrapper::getValue)
            .map(ItemStack::newInstance).collect(Collectors.toList());
    }
    default Stream<RegistryEntryV1802.Wrapper<Item>> getMatchingItemsV2102()
    {
        return this.getMatchingItems0V2102().map(FunctionInvertible.wrapper(RegistryEntryV1802.FACTORY)
            .thenApply(it -> new RegistryEntryV1802.Wrapper<>(it, Item.FACTORY), RegistryEntryV1802.Wrapper::getBase));
    }
    Stream<Object> getMatchingItems0V2102();
    @SpecificImpl("getMatchingItems0V2102")
    @VersionRange(begin = 2102, end = 2104)
    default Stream<Object> getMatchingItems0V2102_2104()
    {
        return this.getMatchingItems00V2102_2104().stream();
    }
    @VersionRange(begin = 2102, end = 2104)
    @WrapMinecraftMethod(@VersionName(name = "getMatchingItems"))
    List<Object> getMatchingItems00V2102_2104();
    @SpecificImpl("getMatchingItems0V2102")
    @VersionRange(begin = 2104)
    @WrapMinecraftMethod(@VersionName(name = "getMatchingItems"))
    Stream<Object> getMatchingItems0V2104();

    @SpecificImpl("getExamples")
    @VersionRange(end = 1200)
    default Collection<ItemStack> getExamplesV_1200()
    {
        ItemStack result = this.asItemStackV_1200().clone();
        result.setCount(1);
        if(result.getDamageV_1300() == Short.MAX_VALUE)
            result.setDamageV_1300(0);
        return Collections.singletonList(result);
    }
    @SpecificImpl("getExamples")
    @VersionRange(begin = 1200, end = 1300)
    default Collection<ItemStack> getExamplesV1200_1300()
    {
        return new ListProxy<>(this.getDataV1200_1300(), FunctionInvertible.of(is ->
        {
            is = is.clone();
            is.setCount(1);
            if(is.getDamageV_1300() == Short.MAX_VALUE)
                is.setDamageV_1300(0);
            return is;
        }, is -> RuntimeUtil.valueThrow(new UnsupportedOperationException())));
    }
    @SpecificImpl("getExamples")
    @VersionRange(begin = 1300)
    default Collection<ItemStack> getExamplesV1300()
    {
        return this.getMatchingStacksV1300();
    }

    @VersionRange(begin = 1200, end = 1300)
    @WrapMinecraftFieldAccessor(@VersionName(name = "field_15681"))
    ItemStack.Array getData0V1200_1300();
    @VersionRange(begin = 1300, end = 2102)
    @WrapMinecraftFieldAccessor(
        {
            @VersionName(name = "field_15681", end = 1400),
            @VersionName(name = "matchingStacks", begin = 1400)
        }
    )
    ItemStack.Array getCachedMatchingStacks0V1300_2102();
    @VersionRange(begin = 1300, end = 1903)
    @WrapMinecraftMethod(
        {
            @VersionName(name = "method_16197", end = 1400),
            @VersionName(name = "cacheMatchingStacks", begin = 1400)
        }
    )
    void cacheMatchingStacksV1300_1903();

    @Override
    int hashCode();
    @Override
    boolean equals(Object object);

    @SpecificImpl("hashCode")
    @VersionRange(end = 1200)
    default int hashCodeV_1200()
    {
        return this.asItemStackV_1200().hashCode();
    }
    @SpecificImpl("equals")
    @VersionRange(end = 1200)
    default boolean equalsV_1200(Object object)
    {
        if(this == object)
            return true;
        if(!(object instanceof IngredientVanilla))
            return false;
        return Objects.equals(this.asItemStackV_1200(), ((IngredientVanilla) object).asItemStackV_1200());
    }
    @SpecificImpl("hashCode")
    @VersionRange(begin = 1200, end = 1300)
    default int hashCodeV1200_1300()
    {
        return this.getDataV1200_1300().hashCode();
    }
    @SpecificImpl("equals")
    @VersionRange(begin = 1200, end = 1300)
    default boolean equalsV1200_1300(Object object)
    {
        if(this == object)
            return true;
        if(!(object instanceof IngredientVanilla))
            return false;
        IngredientVanilla o = (IngredientVanilla) object;
        if(this.getWrapped() == o.getWrapped())
            return true;
        return Objects.equals(this.getDataV1200_1300(), o.getDataV1200_1300());
    }
    @SpecificImpl("hashCode")
    @VersionRange(begin = 1300)
    default int hashCodeV1300()
    {
        return this.getMatchingStacksV1300().hashCode();
    }
    @SpecificImpl("equals")
    @VersionRange(begin = 1300)
    default boolean equalsV1300(Object object)
    {
        if(this == object)
            return true;
        if(!(object instanceof IngredientVanilla))
            return false;
        IngredientVanilla o = (IngredientVanilla) object;
        if(this.getWrapped() == o.getWrapped())
            return true;
        return Objects.equals(this.getMatchingStacksV1300(), o.getMatchingStacksV1300());
    }
}
