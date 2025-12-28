package mz.mzlib.minecraft.item;

import mz.mzlib.data.DataKey;
import mz.mzlib.i18n.I18n;
import mz.mzlib.minecraft.Identifier;
import mz.mzlib.minecraft.MinecraftServer;
import mz.mzlib.minecraft.VersionName;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.minecraft.authlib.GameProfile;
import mz.mzlib.minecraft.component.ComponentKeyV2005;
import mz.mzlib.minecraft.component.ComponentMapDefaultedV2005;
import mz.mzlib.minecraft.datafixer.DataUpdateTypesV1300;
import mz.mzlib.minecraft.datafixer.DataUpdateTypesV900_1300;
import mz.mzlib.minecraft.entity.player.ActionResult;
import mz.mzlib.minecraft.entity.player.EntityPlayer;
import mz.mzlib.minecraft.entity.player.EntityPlayerAbstract;
import mz.mzlib.minecraft.entity.player.Hand;
import mz.mzlib.minecraft.i18n.MinecraftI18n;
import mz.mzlib.minecraft.incomprehensible.TypedActionResultV900_2102;
import mz.mzlib.minecraft.nbt.*;
import mz.mzlib.minecraft.registry.entry.RegistryEntryListV1903;
import mz.mzlib.minecraft.registry.tag.TagKeyV1903;
import mz.mzlib.minecraft.serialization.CodecV1600;
import mz.mzlib.minecraft.serialization.DynamicV1300;
import mz.mzlib.minecraft.text.Text;
import mz.mzlib.minecraft.text.TextColor;
import mz.mzlib.minecraft.text.TextTranslatable;
import mz.mzlib.minecraft.world.World;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.minecraft.wrapper.WrapMinecraftFieldAccessor;
import mz.mzlib.minecraft.wrapper.WrapMinecraftMethod;
import mz.mzlib.util.Option;
import mz.mzlib.util.Result;
import mz.mzlib.util.wrapper.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@WrapMinecraftClass(@VersionName(name = "net.minecraft.item.ItemStack"))
public interface ItemStack extends WrapperObject
{
    WrapperFactory<ItemStack> FACTORY = WrapperFactory.of(ItemStack.class);

    ItemStack EMPTY = FACTORY.getStatic().static$empty();

    static ItemStack newInstance(Item item)
    {
        return FACTORY.getStatic().static$newInstance(item);
    }
    static ItemStack newInstance(Item item, int count)
    {
        ItemStack result = newInstance(item);
        result.setCount(count);
        return result;
    }
    static ItemStack newInstance(ItemType type, int count)
    {
        return type.toItemStack(count);
    }

    static Builder builder()
    {
        return FACTORY.getStatic().static$builder();
    }
    static Builder builder(ItemStack from)
    {
        return FACTORY.getStatic().static$builder(from);
    }
    interface Builder
    {
        ItemStack build();
        Builder item(Item value);
        Builder fromId(Identifier id);
        Builder fromId(String id);

        Builder count(int value);

        ItemStack.Builder damageV_1300(int value);
        Builder fromId(String idV_1300, int damageV_1300, String idV1300);

        <T> Builder data(DataKey<ItemStack, T, ?> key, T value);

        StepPlayerHead playerHead();
        Builder.StepColor colored(String idV_1300, String baseIdV1300);
        Builder.StepColor colored(String baseId);
        Builder.StepColor dye();
        Builder.StepColor wool();
        Builder.StepColor stainedGlass();
        Builder.StepColor stainedGlassPane();
        Builder.StepColor terracotta();
        Builder.StepColor carpet();
        Builder.StepColor banner();
        Builder.StepColor shulkerBoxV1100();
        Builder.StepColor bedV1200();
        Builder.StepColor concreteV1200();
        Builder.StepColor concretePowderV1200();
        Builder.StepColor glazedTerracottaV1200();

        default Builder customName(Text value)
        {
            return this.data(Item.CUSTOM_NAME, Option.some(value));
        }
        default Builder emptyName()
        {
            return this.customName(Text.literal("").setColor(TextColor.BLACK));
        }
        StepLore lore();
        default Builder i18n(String lang, String key)
        {
            this.customName(MinecraftI18n.resolveText(lang, key));
            key += ".lore";
            if(I18n.getSource(lang, key, null) != null)
                this.lore()
                    .lines(MinecraftI18n.resolveTexts(lang, key))
                    .finish();
            return this;
        }
        default Builder i18n(EntityPlayer player, String key)
        {
            return this.i18n(player.getLanguage(), key);
        }

        interface StepLore
        {
            StepLore line(Text value);
            StepLore lines(List<Text> values);
            default StepLore lines(Text... values)
            {
                return this.lines(Arrays.asList(values));
            }
            Builder finish();
            default ItemStack build()
            {
                return this.finish().build();
            }
        }

        interface StepPlayerHead
        {
            Builder gameProfile(GameProfile.Description description);
            default Builder textures(Option<String> name, Option<UUID> uuid, String textures)
            {
                return this.gameProfile(GameProfile.Description.textures(name, uuid, textures));
            }
            default Builder texturesUrl(UUID uuid, String value)
            {
                return this.gameProfile(GameProfile.Description.texturesUrl(Option.none(), Option.some(uuid), value));
            }
            default Builder texturesUrl(String value)
            {
                return this.gameProfile(GameProfile.Description.texturesUrl(value));
            }
            default Builder textures(UUID uuid, String value)
            {
                return this.gameProfile(GameProfile.Description.textures(Option.none(), Option.some(uuid), value));
            }
            default Builder textures(String value)
            {
                return this.gameProfile(GameProfile.Description.textures(value));
            }
        }

        interface StepColor
        {
            StepColor reversed();
            Builder color(String value);
            default Builder white()
            {
                return this.color("white");
            }
            default Builder orange()
            {
                return this.color("orange");
            }
            default Builder magenta()
            {
                return this.color("magenta");
            }
            default Builder lightBlue()
            {
                return this.color("light_blue");
            }
            default Builder yellow()
            {
                return this.color("yellow");
            }
            default Builder lime()
            {
                return this.color("lime");
            }
            default Builder pink()
            {
                return this.color("pink");
            }
            default Builder gray()
            {
                return this.color("gray");
            }
            default Builder lightGray()
            {
                return this.color("light_gray");
            }
            default Builder cyan()
            {
                return this.color("cyan");
            }
            default Builder purple()
            {
                return this.color("purple");
            }
            default Builder blue()
            {
                return this.color("blue");
            }
            default Builder brown()
            {
                return this.color("brown");
            }
            default Builder green()
            {
                return this.color("green");
            }
            default Builder red()
            {
                return this.color("red");
            }
            default Builder black()
            {
                return this.color("black");
            }
        }
    }

    @VersionRange(begin = 1600)
    static CodecV1600.Wrapper<ItemStack> codecV1600()
    {
        return new CodecV1600.Wrapper<>(codec0V1600(), FACTORY);
    }

    @VersionRange(end = 2005)
    static ItemStack newInstanceV_2005(NbtCompound nbt)
    {
        return FACTORY.getStatic().static$newInstanceV_2005(nbt);
    }

    static Result<Option<ItemStack>, String> decode0(NbtCompound nbt)
    {
        return FACTORY.getStatic().static$decode0(nbt);
    }

    /**
     * Decode and convert version
     */
    static Result<Option<ItemStack>, String> decode(NbtCompound nbt)
    {
        for(String id : nbt.getString("id"))
        {
            if(!Identifier.of(id).equals(Identifier.ofMinecraft("air"))) // legacy
                return decode0(upgrade(nbt));
        }
        return Result.success(Option.some(EMPTY));
    }

    default Result<Option<NbtCompound>, String> encode()
    {
        if(this.isEmpty())
            return Result.success(Option.some(NbtCompound.newInstance()));
        Result<Option<NbtCompound>, String> result = encode0();
        for(NbtCompound nbt : result.getValue())
        {
            nbt.put("DataVersion", NbtInt.newInstance(MinecraftServer.instance.getDataVersion()));
        }
        return result;
    }
    Result<Option<NbtCompound>, String> encode0();

    @WrapMinecraftFieldAccessor(@VersionName(name = "item")) // cannot replace with wrapping method
    Item getItem();

    @Deprecated
    @WrapMinecraftFieldAccessor(@VersionName(name = "item"))
    void setItem(Item item);

    default Identifier getId()
    {
        return this.getItem().getId();
    }

    int getCount();

    void setCount(int count);

    @VersionRange(end = 1300)
    @WrapMinecraftFieldAccessor(@VersionName(name = "damage"))
    int getDamageV_1300();

    @VersionRange(end = 1300)
    @WrapMinecraftFieldAccessor(@VersionName(name = "damage"))
    void setDamageV_1300(int damage);

    default Option<NbtCompound> getTagV_2005()
    {
        if(!this.isPresent())
            return Option.none();
        return Option.fromWrapper(this.getTag0V_2005());
    }

    default void setTagV_2005(Option<NbtCompound> value)
    {
        this.setTag0V_2005(value.unwrapOrGet(NbtCompound.FACTORY::getStatic));
    }

    default NbtCompound tagV_2005()
    {
        for(NbtCompound result : this.getTagV_2005())
        {
            return result;
        }
        NbtCompound result = NbtCompound.newInstance();
        this.setTagV_2005(Option.some(result));
        return result;
    }

    @VersionRange(begin = 2005)
    @WrapMinecraftFieldAccessor(@VersionName(name = "components"))
        // cannot replace with wrapping method
    ComponentMapDefaultedV2005 getComponentsV2005();

    /**
     * @see #getComponentsV2005()
     */
    @Deprecated
    @VersionRange(begin = 2005)
    @WrapMinecraftMethod(@VersionName(name = "set"))
    <T> T setComponentV2005(ComponentKeyV2005<T> key, T value);

    boolean isEmpty();

    @WrapMinecraftMethod(@VersionName(name = "isStackable"))
    boolean isStackable();

    default void grow(int count)
    {
        int n = this.getCount() + count;
        this.setCount(n);
    }

    default void shrink(int count)
    {
        this.grow(-count);
    }

    default ItemStack split(int count)
    {
        ItemStack result = this.copy(Math.min(this.getCount(), count));
        this.shrink(result.getCount());
        return result;
    }

    ItemStack copy();

    default ItemStack copy(int count)
    {
        ItemStack result = this.copy();
        result.setCount(count);
        return result;
    }

    @WrapMinecraftMethod(@VersionName(name = "getMaxCount"))
    int getMaxStackCount();

    Text getName();

    /**
     * @see #getName
     */
    @Deprecated
    String getTranslationKey();
    @SpecificImpl("getTranslationKey")
    @VersionRange(end = 2102)
    default String getTranslationKeyV_2102()
    {
        return this.getItem().getTranslationKeyV_2102(this);
    }

    default ItemType getType()
    {
        return new ItemType(this);
    }

    static boolean isStackable(ItemStack a, ItemStack b)
    {
        return FACTORY.getStatic().static$isStackable(a, b);
    }

    @VersionRange(begin = 1700)
    @WrapMinecraftMethod(@VersionName(name = "method_31574"))
    boolean isOfV1700(Item item);

    @VersionRange(begin = 1903)
    @WrapMinecraftMethod(@VersionName(name = "isIn"))
    boolean hasTagV1903(TagKeyV1903<?> tag);

    @VersionRange(begin = 2002)
    @WrapMinecraftMethod(@VersionName(name = "method_53187"))
    boolean isInV2002(RegistryEntryListV1903 registryEntries);

    @VersionRange(end = 900)
    @WrapMinecraftMethod(@VersionName(name = "onStartUse"))
    ItemStack useV_900(World world, EntityPlayerAbstract player);
    @VersionRange(begin = 900, end = 2102)
    @WrapMinecraftMethod({ @VersionName(name = "method_11390", end = 1400), @VersionName(name = "use", begin = 1400) })
    TypedActionResultV900_2102<?> use0V900_2102(World world, EntityPlayerAbstract player, Hand hand);
    @VersionRange(begin = 2102)
    @WrapMinecraftMethod(@VersionName(name = "use"))
    ActionResult useV2102(World world, EntityPlayerAbstract player, Hand hand);

    /**
     * Shadow clone
     */
    ItemStack clone();
    @SpecificImpl("clone")
    default ItemStack clone$impl()
    {
        if(this.isEmpty())
            return EMPTY;
        return this.clone(this.getItem());
    }
    @Deprecated
    @Override
    default ItemStack clone0()
    {
        return this.clone();
    }
    ItemStack clone(Item newItem);

    @Override
    int hashCode0();

    @Override
    default boolean equals0(Object object)
    {
        if(this == object)
            return true;
        if(!(object instanceof ItemStack))
            return false;
        ItemStack that = (ItemStack) object;
        if(this.getWrapped() == that.getWrapped())
            return true;
        if(this.isEmpty() && that.isEmpty())
            return true;
        if(this.isEmpty() || that.isEmpty())
            return false;
        return this.getCount() == that.getCount() && isStackable(this, that);
    }

    static NbtCompound upgrade(NbtCompound nbt)
    {
        int dataVersion;
        l1:
        do
        {
            for(NbtInt nbtVersion : nbt.get("DataVersion", NbtInt.FACTORY))
            {
                dataVersion = nbtVersion.getValue();
                break l1;
            }
            if(nbt.containsKey("Damage"))
                dataVersion = 1343; // 1.12.2
            else if(nbt.containsKey("count")) // if(nbt.containsKey("components"))
                dataVersion = 3837; // 1.20.5
            else
            {
                dataVersion = 1952; // 1.14
                for(NbtCompound tag : nbt.get("tag", NbtCompound.FACTORY))
                {
                    for(NbtCompound display : tag.get("display", NbtCompound.FACTORY))
                    {
                        for(NbtList lore : display.get("Lore", NbtList.FACTORY))
                        {
                            for(NbtString l : lore.asList(NbtString.FACTORY))
                            {
                                try
                                {
                                    Text.decode(l.getValue());
                                }
                                catch(Throwable ignored)
                                {
                                    dataVersion = 1631; // 1.13.2
                                    break l1;
                                }
                            }
                        }
                    }
                }
            }
        } while(false);
        return upgrade(nbt, dataVersion);
    }

    static NbtCompound upgrade(NbtCompound nbt, int from)
    {
        return FACTORY.getStatic().static$upgrade(nbt, from);
    }

    @WrapArrayClass(ItemStack.class)
    interface Array extends WrapperArray<ItemStack>
    {
        WrapperFactory<Array> FACTORY = WrapperFactory.of(Array.class);

        static Array newInstance(int size)
        {
            return (Array) FACTORY.getStatic().static$newInstance(size);
        }
    }


    ItemStack static$empty();
    @SpecificImpl("static$empty")
    @VersionRange(end = 1100)
    default ItemStack static$emptyV_1100()
    {
        return FACTORY.create(null);
    }
    @SpecificImpl("static$empty")
    @VersionRange(begin = 1100)
    @WrapMinecraftFieldAccessor(@VersionName(name = "EMPTY"))
    ItemStack static$emptyV1100();

    ItemStack static$newInstance(Item item);
    @WrapConstructor
    @VersionRange(end = 1300)
    @SpecificImpl("static$newInstance")
    ItemStack static$newInstanceV_1300(Item item);
    @WrapConstructor
    @VersionRange(begin = 1300)
    ItemStack static$newInstanceV1300(ItemConvertibleV1300 item);
    @SpecificImpl("static$newInstance")
    @VersionRange(begin = 1300)
    default ItemStack static$newInstanceV1300(Item item)
    {
        return static$newInstanceV1300((ItemConvertibleV1300) item.castTo(Item.V1300.FACTORY));
    }

    ItemStack.Builder static$builder();
    @SpecificImpl("static$builder")
    @VersionRange(end = 1300)
    default ItemStack.Builder static$builderV_1300()
    {
        return new ItemStackBuilderImpl.V_1300();
    }
    @SpecificImpl("static$builder")
    @VersionRange(begin = 1300)
    default ItemStack.Builder static$builderV1300()
    {
        return new ItemStackBuilderImpl();
    }

    ItemStack.Builder static$builder(ItemStack from);
    @SpecificImpl("static$builder")
    @VersionRange(end = 1300)
    default ItemStack.Builder static$builderV_1300(ItemStack from)
    {
        return new ItemStackBuilderImpl.V_1300(from);
    }
    @SpecificImpl("static$builder")
    @VersionRange(begin = 1300)
    default ItemStack.Builder static$builderV1300(ItemStack from)
    {
        return new ItemStackBuilderImpl(from);
    }

    static CodecV1600<?> codec0V1600()
    {
        return FACTORY.getStatic().static$codec0V1600();
    }
    @VersionRange(begin = 1600)
    @WrapMinecraftFieldAccessor(@VersionName(name = "CODEC"))
    CodecV1600<?> static$codec0V1600();

    ItemStack static$newInstanceV_2005(NbtCompound nbt);
    @SpecificImpl("static$newInstanceV_2005")
    @VersionRange(end = 1100)
    @WrapMinecraftMethod(@VersionName(name = "fromNbt"))
    ItemStack static$newInstanceV_1100(NbtCompound nbt);
    @SpecificImpl("static$newInstanceV_2005")
    @VersionRange(begin = 1100, end = 2005)
    @WrapConstructor
    ItemStack static$newInstanceV1100_2005(NbtCompound nbt);

    Result<Option<ItemStack>, String> static$decode0(NbtCompound nbt);
    @SpecificImpl("static$decode0")
    @VersionRange(end = 2005)
    default Result<Option<ItemStack>, String> static$decode0V_2005(NbtCompound nbt)
    {
        try
        {
            return Result.success(Option.some(newInstanceV_2005(nbt)));
        }
        catch(Throwable e)
        {
            return Result.failure(Option.none(), e.toString());
        }
    }
    @SpecificImpl("static$decode0")
    @VersionRange(begin = 2005)
    default Result<Option<ItemStack>, String> static$decode0V2005(NbtCompound nbt)
    {
        return codecV1600().parse(NbtOpsV1300.withRegistriesV1903(), nbt).toResult();
    }

    @SpecificImpl("encode0")
    @VersionRange(end = 2005)
    default Result<Option<NbtCompound>, String> encode0V_2005()
    {
        try
        {
            return Result.success(Option.some(this.encode0V_2005(NbtCompound.newInstance())));
        }
        catch(Throwable e)
        {
            return Result.failure(Option.none(), e.toString());
        }
    }
    @VersionRange(end = 2005)
    @WrapMinecraftMethod({
        @VersionName(name = "toNbt", end = 1400),
        @VersionName(name = "method_7953", begin = 1400)
    })
    NbtCompound encode0V_2005(NbtCompound nbt);
    @SpecificImpl("encode0")
    @VersionRange(begin = 2005)
    default Result<Option<NbtCompound>, String> encode0V2005()
    {
        return codecV1600().encodeStart(NbtOpsV1300.withRegistriesV1903(), this).toResult();
    }

    @SpecificImpl("getCount")
    @VersionRange(end = 1100)
    default int getCount_V1100()
    {
        if(!this.isPresent())
            return 0;
        return this.getCount0_V1100();
    }
    @VersionRange(end = 1100)
    @WrapMinecraftFieldAccessor(@VersionName(name = "count"))
    int getCount0_V1100();
    @SpecificImpl("getCount")
    @VersionRange(begin = 1100)
    @WrapMinecraftMethod(@VersionName(name = "getCount"))
    int getCountV1100();

    @SpecificImpl("setCount")
    @VersionRange(end = 1100)
    @WrapMinecraftFieldAccessor(@VersionName(name = "count"))
    void setCountV_1100(int count);
    @SpecificImpl("setCount")
    @VersionRange(begin = 1100)
    @WrapMinecraftMethod(@VersionName(name = "setCount"))
    void setCountV1100(int count);

    @VersionRange(end = 2005)
    @WrapMinecraftMethod({
        @VersionName(end = 1400, name = "getNbt"),
        @VersionName(begin = 1400, name = "method_7969")
    })
    NbtCompound getTag0V_2005();

    @VersionRange(end = 2005)
    @WrapMinecraftMethod({
        @VersionName(end = 1400, name = "setNbt"),
        @VersionName(begin = 1400, name = "method_7980")
    })
    void setTag0V_2005(NbtCompound value);

    @SpecificImpl("isEmpty")
    @VersionRange(end = 1100)
    default boolean isEmptyV_1100()
    {
        return !this.isPresent() || this.getCount() <= 0;
    }
    @SpecificImpl("isEmpty")
    @VersionRange(begin = 1100)
    @WrapMinecraftMethod(@VersionName(name = "isEmpty"))
    boolean isEmptyV1100();

    @SpecificImpl("copy")
    @VersionRange(end = 1100)
    default ItemStack copyV_1100()
    {
        if(this.isEmpty())
            return EMPTY;
        return this.copy0();
    }
    @SpecificImpl("copy")
    @VersionRange(begin = 1100)
    default ItemStack copyV1100()
    {
        return this.copy0();
    }
    @WrapMinecraftMethod(@VersionName(name = "copy"))
    ItemStack copy0();

    @SpecificImpl("getName")
    @VersionRange(end = 1300)
    default Text getNameV_1300()
    {
        return TextTranslatable.newInstance(this.getTranslationKeyV_2102());
    }
    @SpecificImpl("getName")
    @VersionRange(begin = 1300)
    default Text getNameV1300()
    {
        return this.getItem().getNameV1300(this);
    }

    @Deprecated
    @SpecificImpl("getTranslationKey")
    @VersionRange(begin = 2102)
    default String getTranslationKeyV2102()
    {
        return this.getItem().getNameV1300(this).castTo(TextTranslatable.FACTORY).getKey();
    }

    boolean static$isStackable(ItemStack a, ItemStack b);
    @SpecificImpl("static$isStackable")
    @VersionRange(end = 1300)
    default boolean static$isStackableV_1300(ItemStack a, ItemStack b)
    {
        if(isEmpty(a) && isEmpty(b))
            return true;
        if(isEmpty(a) || isEmpty(b))
            return false;
        return a.getItem().equals(b.getItem()) && a.getDamageV_1300() == b.getDamageV_1300() &&
            a.getTagV_2005().equals(b.getTagV_2005());
    }
    @SpecificImpl("static$isStackable")
    @VersionRange(begin = 1300, end = 1700)
    default boolean static$isStackableV1300_1700(ItemStack a, ItemStack b)
    {
        if(isEmpty(a) && isEmpty(b))
            return true;
        if(a.isEmpty() || b.isEmpty())
            return false;
        return a.getItem().equals(b.getItem()) && a.getTagV_2005().equals(b.getTagV_2005());
    }

    @SpecificImpl("static$isStackable")
    @VersionRange(begin = 1700)
    @WrapMinecraftMethod({
        @VersionName(name = "canCombine", end = 2005),
        @VersionName(name = "areItemsAndComponentsEqual", begin = 2005)
    })
    boolean static$isStackableV1700(ItemStack a, ItemStack b);

    @SpecificImpl("clone")
    @VersionRange(end = 1100)
    default ItemStack cloneV_1100(Item newItem)
    {
        if(this.isEmpty())
            return EMPTY;
        return this.cloneV1100_1300(newItem);
    }
    @SpecificImpl("clone")
    @VersionRange(begin = 1100, end = 1300)
    default ItemStack cloneV1100_1300(Item newItem)
    {
        ItemStack result = this.cloneV1300_2005(newItem);
        result.setDamageV_1300(this.getDamageV_1300());
        return result;
    }
    @SpecificImpl("clone")
    @VersionRange(begin = 1300, end = 2005)
    default ItemStack cloneV1300_2005(Item newItem)
    {
        ItemStack result = newInstance(newItem, this.getCount());
        result.setTagV_2005(this.getTagV_2005());
        return result;
    }
    @SpecificImpl("clone")
    @VersionRange(begin = 2005)
    default ItemStack cloneV2005(Item newItem)
    {
        return this.cloneV2005(newItem.v1300(), this.getCount());
    }
    @VersionRange(begin = 2005)
    @WrapMinecraftMethod(@VersionName(name = "method_56701"))
    ItemStack cloneV2005(ItemConvertibleV1300 newItem, int count);

    @SpecificImpl("hashCode0")
    @VersionRange(end = 1300)
    default int hashCode0V_1300()
    {
        return Objects.hash(this.hashCode0V1300_2005(), this.getDamageV_1300());
    }
    @SpecificImpl("hashCode0")
    @VersionRange(begin = 1300, end = 2005)
    default int hashCode0V1300_2005()
    {
        return Objects.hash(this.getItem(), this.getCount(), this.getTagV_2005());
    }
    @SpecificImpl("hashCode0")
    @VersionRange(begin = 2005)
    default int hashCode0V2005()
    {
        return Objects.hash(this.getItem(), this.getCount(), this.getComponentsV2005());
    }

    NbtCompound static$upgrade(NbtCompound nbt, int from);
    @SpecificImpl("static$upgrade")
    @VersionRange(end = 900)
    default NbtCompound static$upgradeV_900(NbtCompound nbt, int from)
    {
        return nbt;
    }
    @SpecificImpl("static$upgrade")
    @VersionRange(begin = 900, end = 1300)
    default NbtCompound static$upgradeV900_1300(NbtCompound nbt, int from)
    {
        return MinecraftServer.instance.getDataUpdaterV900_1300()
            .update(DataUpdateTypesV900_1300.itemStack(), nbt, from);
    }
    @SpecificImpl("static$upgrade")
    @VersionRange(begin = 1300)
    default NbtCompound static$upgradeV1300(NbtCompound nbt, int from)
    {
        return MinecraftServer.instance.getDataUpdaterV1300().update(
            DataUpdateTypesV1300.itemStack(), DynamicV1300.newInstance(NbtOpsV1300.instance(), nbt), from,
            MinecraftServer.instance.getDataVersion()
        ).getValue();
    }


    @Deprecated
    @WrapperCreator
    static ItemStack create(Object wrapped)
    {
        return WrapperObject.create(ItemStack.class, wrapped);
    }

    /**
     * @see #EMPTY
     */
    @Deprecated
    static ItemStack empty()
    {
        return EMPTY;
    }

    /**
     * @see #encode()
     */
    @Deprecated
    static Result<Option<NbtCompound>, String> encode(ItemStack is)
    {
        return is.encode();
    }

    /**
     * @see #isEmpty()
     */
    @Deprecated
    static boolean isEmpty(ItemStack is)
    {
        return is.isEmpty();
    }

    /**
     * @see #copy(ItemStack)
     */
    @Deprecated
    static ItemStack copy(ItemStack is)
    {
        return is.copy();
    }

    /**
     * @see #copy(int)
     */
    @Deprecated
    static ItemStack copy(ItemStack is, int count)
    {
        return is.copy(count);
    }

    /**
     * @see #getName
     */
    @Deprecated
    static String getTranslationKey(ItemStack is)
    {
        return is.getTranslationKey();
    }
}
