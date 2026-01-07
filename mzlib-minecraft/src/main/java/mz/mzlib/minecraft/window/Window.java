package mz.mzlib.minecraft.window;

import mz.mzlib.minecraft.MinecraftPlatform;
import mz.mzlib.minecraft.VersionName;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.minecraft.bukkit.inventory.BukkitInventoryView;
import mz.mzlib.minecraft.entity.player.EntityPlayerAbstract;
import mz.mzlib.minecraft.entity.player.EntityPlayer;
import mz.mzlib.minecraft.incomprehensible.network.WindowSyncHandlerV1700;
import mz.mzlib.minecraft.item.ItemStack;
import mz.mzlib.minecraft.network.packet.s2c.play.PacketS2cWindowSlotUpdate;
import mz.mzlib.minecraft.util.collection.DefaultedListV1100;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.minecraft.wrapper.WrapMinecraftFieldAccessor;
import mz.mzlib.minecraft.wrapper.WrapMinecraftMethod;
import mz.mzlib.util.FunctionInvertible;
import mz.mzlib.util.Option;
import mz.mzlib.util.proxy.ListProxy;
import mz.mzlib.util.wrapper.*;

import java.util.List;
import java.util.function.Supplier;

@WrapMinecraftClass({
    @VersionName(name = "net.minecraft.screen.ScreenHandler", end = 1400),
    @VersionName(name = "net.minecraft.container.Container", begin = 1400, end = 1600),
    @VersionName(name = "net.minecraft.screen.ScreenHandler", begin = 1600)
})
public interface Window extends WrapperObject
{
    WrapperFactory<Window> FACTORY = WrapperFactory.of(Window.class);
    @Deprecated
    @WrapperCreator
    static Window create(Object wrapped)
    {
        return WrapperObject.create(Window.class, wrapped);
    }

    @WrapConstructor
    @VersionRange(end = 1400)
    Window static$newInstanceV_1400();
    @WrapConstructor
    @VersionRange(begin = 1400)
    Window static$newInstanceV1400(WindowTypeV1400 type, int syncId);

    @WrapMinecraftFieldAccessor(@VersionName(name = "syncId"))
    int getSyncId();

    @VersionRange(begin = 1700)
    @WrapMinecraftMethod(@VersionName(name = "syncState"))
    void updateV1700();

    List<Object> getSlots0();
    @SpecificImpl("getSlots0")
    @WrapMinecraftFieldAccessor(@VersionName(name = "slots", end = 1700))
    List<Object> getSlots0V_1700();
    @WrapMinecraftFieldAccessor(@VersionName(name = "slots", begin = 1700))
    DefaultedListV1100<?> getSlots00V1700();
    @SpecificImpl("getSlots0")
    @VersionRange(begin = 1700)
    default List<?> getSlots0V1700()
    {
        return getSlots00V1700().getWrapped();
    }
    default List<WindowSlot> getSlots()
    {
        return new ListProxy<>(getSlots0(), FunctionInvertible.wrapper(WindowSlot.FACTORY));
    }
    default WindowSlot getSlot(int index)
    {
        return this.getSlots().get(index);
    }
    @WrapMinecraftMethod(@VersionName(name = "addSlot"))
    WindowSlot addSlot(WindowSlot slot);
    default void setSlot(int index, WindowSlot slot)
    {
        slot.setSlotIndex(index);
        this.getSlots().set(index, slot);
    }

    @VersionRange(begin = 1700)
    @WrapMinecraftMethod(@VersionName(name = "getCursorStack"))
    ItemStack getCursorV1700();

    @WrapMinecraftMethod(@VersionName(name = "insertItem"))
    boolean placeIn(ItemStack itemStack, int begin, int end, boolean inverted);

    @WrapMinecraftMethod({ @VersionName(name = "close", end = 1904), @VersionName(name = "onClosed", begin = 1904) })
    void onClosed(EntityPlayerAbstract player);

    @MinecraftPlatform.Enabled(MinecraftPlatform.Tag.BUKKIT)
    @WrapMinecraftMethod(@VersionName(name = "getBukkitView"))
    BukkitInventoryView getBukkitView();

    /**
     * @return {@link ItemStack#EMPTY} when no item stack can be transferred, otherwise the original item stack
     */
    @WrapMinecraftMethod({
        @VersionName(name = "transferSlot", end = 1903),
        @VersionName(name = "quickMove", begin = 1903)
    })
    ItemStack quickMove(EntityPlayerAbstract player, int index);

    @WrapMinecraftMethod(@VersionName(name = "canUse"))
    boolean checkReachable(EntityPlayerAbstract player);

    @VersionRange(end = 1700)
    @WrapMinecraftMethod({
        @VersionName(name = "onSlotClick", end = 900),
        @VersionName(name = "method_3252", begin = 900, end = 1400),
        @VersionName(name = "onSlotClick", begin = 1400)
    })
    ItemStack onActionV_1700(int index, int data, WindowActionType actionType, EntityPlayerAbstract player);
    @VersionRange(begin = 1700)
    @WrapMinecraftMethod({
        @VersionName(name = "onSlotClick", end = 2601),
        @VersionName(name = "clicked", remap = false, begin = 2601)
    })
    void onActionV1700(int index, int data, WindowActionType actionType, EntityPlayerAbstract player);

    @WrapMinecraftMethod(@VersionName(name = "nextRevision", begin = 1701))
    int nextRevisionV1701();

    default void sendSlotUpdate(EntityPlayer player, int index)
    {
        this.sendSlotUpdate(player, index, this.getSlot(index).getItemStack());
    }
    void sendSlotUpdate(EntityPlayer player, int slot, ItemStack itemStack);
    @SpecificImpl("sendSlotUpdate")
    @VersionRange(end = 1701)
    default void sendSlotUpdateV_1701(EntityPlayer player, int slot, ItemStack itemStack)
    {
        player.sendPacket(PacketS2cWindowSlotUpdate.newInstanceV_1701(this.getSyncId(), slot, itemStack));
    }
    @SpecificImpl("sendSlotUpdate")
    @VersionRange(begin = 1701)
    default void sendSlotUpdateV1701(EntityPlayer player, int slot, ItemStack itemStack)
    {
        player.sendPacket(
            PacketS2cWindowSlotUpdate.newInstanceV1701(this.getSyncId(), this.nextRevisionV1701(), slot, itemStack));
    }

    @VersionRange(begin = 1700)
    @WrapMinecraftFieldAccessor(@VersionName(name = "syncHandler"))
    WindowSyncHandlerV1700 getSyncHandler0V1700();

    @VersionRange(begin = 1700)
    default Option<EntityPlayer> getPlayerV1700()
    {
        for(WindowSyncHandlerV1700 syncHandler : Option.fromWrapper(this.getSyncHandler0V1700()))
        {
            return syncHandler.asOption(WindowSyncHandlerV1700.Impl.FACTORY).map(WindowSyncHandlerV1700.Impl::getPlayer);
        }
        return Option.none();
    }

    @VersionRange(begin = 1700)
    @WrapMinecraftMethod(@VersionName(name = "checkSlotUpdates"))
    void syncSlot0V1700(int slot, ItemStack stack, Supplier<?> copySupplier);

    @VersionRange(begin = 1700)
    @WrapMinecraftMethod(@VersionName(name = "checkCursorStackUpdates"))
    void syncCursorV1700();
}
