package mz.mzlib.minecraft.network.packet.c2s.play;

import mz.mzlib.minecraft.VersionName;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.minecraft.fastutil.Int2ObjectMapV900;
import mz.mzlib.minecraft.item.ItemStack;
import mz.mzlib.minecraft.network.packet.Packet;
import mz.mzlib.minecraft.window.WindowActionType;
import mz.mzlib.minecraft.window.sync.ItemStackHashV2105;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.minecraft.wrapper.WrapMinecraftFieldAccessor;
import mz.mzlib.minecraft.wrapper.WrapMinecraftMethod;
import mz.mzlib.util.FunctionInvertible;
import mz.mzlib.util.proxy.MapProxy;
import mz.mzlib.util.wrapper.*;

import java.util.Map;

@WrapMinecraftClass(
    {
        @VersionName(name = "net.minecraft.network.packet.c2s.play.ClickWindowC2SPacket", end = 1604),
        @VersionName(name = "net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket", begin = 1604)
    }
)
public interface PacketC2sWindowAction extends Packet
{
    WrapperFactory<PacketC2sWindowAction> FACTORY = WrapperFactory.of(PacketC2sWindowAction.class);
    @Deprecated
    @WrapperCreator
    static PacketC2sWindowAction create(Object wrapped)
    {
        return WrapperObject.create(PacketC2sWindowAction.class, wrapped);
    }

    @WrapMinecraftFieldAccessor(@VersionName(name = "syncId"))
    int getSyncId();

    @WrapMinecraftFieldAccessor({
        @VersionName(name = "actionType", end = 900),
        @VersionName(name = "field_13798", begin = 900, end = 1400),
        @VersionName(name = "actionType", begin = 1400, end = 2601),
        @VersionName(name = "containerInput", remap = false, begin = 2601)
    })
    WindowActionType getActionType();

    @WrapMinecraftFieldAccessor(@VersionName(name = "slot"))
    int getSlotIndex();

    @WrapMinecraftFieldAccessor(
        {
            @VersionName(name = "button", end = 1600),
            @VersionName(name = "clickData", begin = 1600, end = 1605),
            @VersionName(name = "button", begin = 1605)
        }
    )
    int getData();

    @VersionRange(end = 1700)
    @WrapMinecraftFieldAccessor(
        {
            @VersionName(name = "transactionId", end = 1400),
            @VersionName(name = /*actionId*/"field_12820", begin = 1400)
        }
    )
    short getActionIdV_1700();

    @VersionRange(end = 1700)
    @WrapMinecraftFieldAccessor({
        @VersionName(name = "selectedStack", end = 1400),
        @VersionName(name = "stack", begin = 1400)
    })
    ItemStack getItemStackV_1700();

    @VersionRange(begin = 1701)
    @WrapMinecraftMethod(
        {
            @VersionName(name = "getRevision", end = 2105),
            @VersionName(name = "revision", begin = 2105)
        }
    )
    int getRevisionV1701();

    @VersionRange(begin = 1700)
    Map<Integer, ? extends WrapperObject> getModifiedV1700();

    WrapperObject getCursorV1700();

    @SpecificImpl("getCursorV1700")
    @VersionRange(begin = 1700, end = 2105)
    @WrapMinecraftMethod(@VersionName(name = "method_12190"/*getStack*/))
    ItemStack getCursorV1700_2105();

    @SpecificImpl("getModifiedV1700")
    @VersionRange(begin = 1700, end = 2105)
    default Map<Integer, ItemStack> getModifiedV1700_2105()
    {
        return new MapProxy<>(
            this.getModified0V1700().getWrapped(), FunctionInvertible.identity(), FunctionInvertible.wrapper(ItemStack.FACTORY));
    }
    @SpecificImpl("getModifiedV1700")
    @VersionRange(begin = 2105)
    default Map<Integer, ItemStackHashV2105> getModifiedV2105()
    {
        return new MapProxy<>(
            this.getModified0V1700().getWrapped(), FunctionInvertible.identity(),
            FunctionInvertible.wrapper(ItemStackHashV2105.FACTORY)
        );
    }
    @VersionRange(begin = 1700)
    @WrapMinecraftMethod(
        { @VersionName(name = "method_34678", end = 2105), @VersionName(name = "comp_3847", begin = 2105) }
    )
    Int2ObjectMapV900<Object> getModified0V1700();

    @SpecificImpl("getCursorV1700")
    @VersionRange(begin = 2105)
    @WrapMinecraftMethod(@VersionName(name = "cursor"))
    ItemStackHashV2105 getCursorV2105();

    static Builder builder()
    {
        return new Builder();
    }
    @SuppressWarnings("UnusedReturnValue")
    class Builder
    {
        public Builder()
        {
        }
        public Builder from(PacketC2sWindowAction packet)
        {
            packet.accept(this);
            return this;
        }
        int syncId;
        int slotIndex;
        WindowActionType actionType;
        int data;
        short actionIdV_1700;
        ItemStack itemStackV_1700;
        Int2ObjectMapV900<Object> modified0V1700;
        int revisionV1701;
        WrapperObject cursorV1700;
        public Builder syncId(int value)
        {
            this.syncId = value;
            return this;
        }
        public Builder slotIndex(int value)
        {
            this.slotIndex = value;
            return this;
        }
        public Builder actionType(WindowActionType value)
        {
            this.actionType = value;
            return this;
        }
        public Builder data(int value)
        {
            this.data = value;
            return this;
        }
        public Builder actionIdV_1700(short value)
        {
            this.actionIdV_1700 = value;
            return this;
        }
        public Builder itemStackV_1700(ItemStack value)
        {
            this.itemStackV_1700 = value;
            return this;
        }
        public Builder modified0V1700(Int2ObjectMapV900<Object> value)
        {
            this.modified0V1700 = value;
            return this;
        }
        public Builder revisionV1701(int value)
        {
            this.revisionV1701 = value;
            return this;
        }
        public Builder cursorV1700(WrapperObject value)
        {
            this.cursorV1700 = value;
            return this;
        }
        public Builder cursorV1700_2105(ItemStack value)
        {
            return this.cursorV1700(value);
        }
        public Builder cursorV2105(ItemStackHashV2105 value)
        {
            return this.cursorV1700(value);
        }
        public ItemStack getCursorV1700_2105()
        {
            return (ItemStack) this.cursorV1700;
        }
        public ItemStackHashV2105 getCursorV2105()
        {
            return (ItemStackHashV2105) this.cursorV1700;
        }
        public PacketC2sWindowAction build()
        {
            return PacketC2sWindowAction.of(this);
        }
    }

    static PacketC2sWindowAction of(Builder builder)
    {
        return FACTORY.getStatic().static$of(builder);
    }

    void accept(Builder builder); // TODO


    PacketC2sWindowAction static$of(Builder builder); // TODO

    @SpecificImpl("static$of")
    @VersionRange(end = 1700)
    default PacketC2sWindowAction static$ofV_1700(Builder builder)
    {
        PacketC2sWindowAction result = this.static$ofV_1700();
        result.setSyncIdV_1700(builder.syncId);
        result.setSlotIndexV_1700(builder.slotIndex);
        result.setActionTypeV_1700(builder.actionType);
        result.setDataV_1700(builder.data);
        result.setActionIdV_1700(builder.actionIdV_1700);
        result.setItemStackV_1700(builder.itemStackV_1700);
        return result;
    }
    @VersionRange(end = 1700)
    @WrapConstructor
    PacketC2sWindowAction static$ofV_1700();
    @VersionRange(end = 1700)
    @WrapMinecraftFieldAccessor(@VersionName(name = "syncId"))
    void setSyncIdV_1700(int value);
    @VersionRange(end = 1700)
    @WrapMinecraftFieldAccessor(@VersionName(name = "slot"))
    void setSlotIndexV_1700(int value);
    @VersionRange(end = 1700)
    @WrapMinecraftFieldAccessor(
        {
            @VersionName(name = "actionType", end = 900),
            @VersionName(name = "field_13798", begin = 900, end = 1400),
            @VersionName(name = "actionType", begin = 1400)
        }
    )
    void setActionTypeV_1700(WindowActionType value);
    @VersionRange(end = 1700)
    @WrapMinecraftFieldAccessor(
        {
            @VersionName(name = "button", end = 1600),
            @VersionName(name = "clickData", begin = 1600, end = 1605),
            @VersionName(name = "button", begin = 1605)
        }
    )
    void setDataV_1700(int value);
    @VersionRange(end = 1700)
    @WrapMinecraftFieldAccessor(
        {
            @VersionName(name = "transactionId", end = 1400),
            @VersionName(name = /*actionId*/"field_12820", begin = 1400)
        }
    )
    void setActionIdV_1700(short value);
    @VersionRange(end = 1700)
    @WrapMinecraftFieldAccessor({
        @VersionName(name = "selectedStack", end = 1400),
        @VersionName(name = "stack", begin = 1400)
    })
    void setItemStackV_1700(ItemStack value);
    @SpecificImpl("accept")
    @VersionRange(end = 1700)
    default void acceptV_1700(Builder builder)
    {
        builder.syncId = this.getSyncId();
        builder.slotIndex = this.getSlotIndex();
        builder.actionType = this.getActionType();
        builder.data = this.getData();
        builder.actionIdV_1700 = this.getActionIdV_1700();
        builder.itemStackV_1700 = this.getItemStackV_1700();
    }

    @SpecificImpl("static$of")
    @VersionRange(begin = 1700, end = 1701)
    default PacketC2sWindowAction static$ofV1700_1701(Builder builder)
    {
        return this.static$of0V1700_1701(
            builder.syncId, builder.slotIndex, builder.data, builder.actionType,
            builder.getCursorV1700_2105(), builder.modified0V1700
        );
    }
    @VersionRange(begin = 1700, end = 1701)
    @WrapConstructor
    PacketC2sWindowAction static$of0V1700_1701(
        int syncId,
        int slotIndex,
        int data,
        WindowActionType actionType,
        ItemStack cursor,
        Int2ObjectMapV900<Object> modified0);
    @SpecificImpl("accept")
    @VersionRange(begin = 1700, end = 1701)
    default void acceptV1700_1701(Builder builder)
    {
        builder.syncId = this.getSyncId();
        builder.slotIndex = this.getSlotIndex();
        builder.data = this.getData();
        builder.actionType = this.getActionType();
        builder.modified0V1700 = this.getModified0V1700();
        builder.cursorV1700_2105(this.getCursorV1700_2105());
    }

    @SpecificImpl("static$of")
    @VersionRange(begin = 1701, end = 2105)
    default PacketC2sWindowAction static$ofV1701_2105(Builder builder)
    {
        return this.static$of0V1701_2105(
            builder.syncId, builder.revisionV1701, builder.slotIndex, builder.data, builder.actionType,
            builder.getCursorV1700_2105(), builder.modified0V1700
        );
    }
    @VersionRange(begin = 1701, end = 2105)
    @WrapConstructor
    PacketC2sWindowAction static$of0V1701_2105(
        int syncId,
        int revision,
        int slotIndex,
        int data,
        WindowActionType actionType,
        ItemStack cursor,
        Int2ObjectMapV900<Object> modified0);
    @SpecificImpl("accept")
    @VersionRange(begin = 1701, end = 2105)
    default void acceptV1701_2105(Builder builder)
    {
        builder.syncId = this.getSyncId();
        builder.revisionV1701 = this.getRevisionV1701();
        builder.slotIndex = this.getSlotIndex();
        builder.data = this.getData();
        builder.actionType = this.getActionType();
        builder.modified0V1700 = this.getModified0V1700();
        builder.cursorV1700_2105(this.getCursorV1700_2105());
    }

    @SpecificImpl("static$of")
    @VersionRange(begin = 2105)
    default PacketC2sWindowAction static$ofV2105(Builder builder)
    {
        return this.static$of0V2105(
            builder.syncId, builder.revisionV1701, (short) builder.slotIndex,
            (byte) builder.data, builder.actionType, builder.modified0V1700, builder.getCursorV2105()
        );
    }
    @VersionRange(begin = 2105)
    @WrapConstructor
    PacketC2sWindowAction static$of0V2105(
        int syncId,
        int revision,
        short slotIndex,
        byte data,
        WindowActionType actionType,
        Int2ObjectMapV900<Object> modified0,
        ItemStackHashV2105 cursor);
    @SpecificImpl("accept")
    @VersionRange(begin = 2105)
    default void acceptV2105(Builder builder)
    {
        builder.syncId = this.getSyncId();
        builder.revisionV1701 = this.getRevisionV1701();
        builder.slotIndex = (short) this.getSlotIndex();
        builder.data = (byte) this.getData();
        builder.actionType = this.getActionType();
        builder.modified0V1700 = this.getModified0V1700();
        builder.cursorV2105(this.getCursorV2105());
    }
}
