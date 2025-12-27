package mz.mzlib.minecraft.ui.window;

import mz.mzlib.Priority;
import mz.mzlib.event.Cancellable;
import mz.mzlib.event.EventListener;
import mz.mzlib.minecraft.MinecraftServer;
import mz.mzlib.minecraft.entity.player.EntityPlayer;
import mz.mzlib.minecraft.entity.player.EntityPlayerAbstract;
import mz.mzlib.minecraft.event.player.async.EventAsyncPlayerDisplayItemInWindow;
import mz.mzlib.minecraft.event.window.async.EventAsyncWindowAction;
import mz.mzlib.minecraft.event.window.async.EventAsyncWindowClose;
import mz.mzlib.minecraft.inventory.Inventory;
import mz.mzlib.minecraft.inventory.InventorySimple;
import mz.mzlib.minecraft.item.ItemStack;
import mz.mzlib.minecraft.ui.UiStack;
import mz.mzlib.minecraft.window.*;
import mz.mzlib.module.MzModule;
import mz.mzlib.util.Option;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class UiWindow extends UiAbstractWindow
{
    public WindowType windowType;
    public Inventory inventory;

    public UiWindow(WindowType windowType, Inventory inventory)
    {
        this.windowType = windowType;
        this.inventory = inventory;
    }
    public UiWindow(WindowType windowType, int size)
    {
        this(windowType, InventorySimple.newInstance(size));
    }
    public UiWindow(WindowType windowType)
    {
        this(windowType, windowType.upperSize);
    }

    @Override
    public void update(Set<Integer> indexes)
    {
        for(EntityPlayer viewer : UiStack.getViewers(this))
        {
            for(int index : indexes)
            {
                viewer.getCurrentWindow().sendSlotUpdate(viewer, index);
            }
        }
    }

    public Map<Integer, BiFunction<Inventory, Integer, WindowSlot>> slots = new HashMap<>();
    public Map<Integer, Consumer<EventAsyncPlayerDisplayItemInWindow>> icons = new ConcurrentHashMap<>();
    public Map<Integer, ButtonHandler> buttons = new HashMap<>();

    public WindowType getWindowType()
    {
        return this.windowType;
    }
    public Inventory getInventory()
    {
        return this.inventory;
    }

    public void clear()
    {
        this.inventory.clear();
        this.slots.clear();
        this.buttons.clear();
        this.icons.clear();
    }

    public void putSlot(int index, WindowSlot slot)
    {
        this.putSlot(index, (inv, i) -> slot);
    }
    public void putSlot(int index, BiFunction<Inventory, Integer, WindowSlot> slotCreator)
    {
        this.slots.put(index, slotCreator);
    }

    public void putSlot(int index, BiFunction<Inventory, Integer, WindowSlot> slotCreator, ItemStack itemStack)
    {
        this.putSlot(index, slotCreator);
        this.inventory.setItemStack(index, itemStack);
    }

    public void putIcon0(int index, Consumer<EventAsyncPlayerDisplayItemInWindow> icon)
    {
        this.icons.put(index, icon);
    }

    public void putIcon(int index, Function<EntityPlayer, ItemStack> icon)
    {
        this.putSlot(index, WindowSlotIcon::newInstance);
        this.putIcon0(index, event -> event.setItemStack(icon.apply(event.getPlayer())));
    }
    public void putIconEmpty(int index)
    {
        this.putIcon(index, player -> ItemStack.EMPTY);
    }

    public void putButton(int index, ButtonHandler handler)
    {
        this.buttons.put(index, handler);
    }
    public void putButton(int index, Function<EntityPlayer, ItemStack> icon, ButtonHandler handler)
    {
        this.putIcon(index, icon);
        this.putButton(index, handler);
    }

    public void initWindow(WindowUiWindow window, EntityPlayer player)
    {
        for(int i = 0; i < this.windowType.upperSize; i++)
        {
            BiFunction<Inventory, Integer, WindowSlot> creator = this.slots.get(i);
            window.addSlot(
                creator == null ?
                    WindowSlotUiWindow.newInstance(this, this.inventory, i) :
                    creator.apply(this.inventory, i));
        }
        this.addPlayerInventorySlots(window, player);
    }

    public void addPlayerInventorySlots(WindowUiWindow window, EntityPlayer player)
    {
        for(int i = 0; i < 3; ++i)
        {
            for(int j = 0; j < 9; ++j)
            {
                int index = j + i * 9 + 9;
                BiFunction<Inventory, Integer, WindowSlot> creator = this.slots.get(window.getSlots().size());
                window.addSlot(creator == null ?
                    WindowSlotUiWindow.newInstance(this, player.getInventory(), index) :
                    creator.apply(player.getInventory(), index));
            }
        }
        for(int i = 0; i < 9; ++i)
        {
            BiFunction<Inventory, Integer, WindowSlot> creator = this.slots.get(window.getSlots().size());
            window.addSlot(creator == null ?
                WindowSlotUiWindow.newInstance(this, player.getInventory(), i) :
                creator.apply(player.getInventory(), i));
        }
    }

    public ItemStack quickMove(WindowUiWindow window, EntityPlayer player, int index)
    {
        return window.quickMoveSuper(player, index);
    }

    public void onAction(WindowUiWindow window, WindowAction action)
    {
        this.onAction(window, action.getIndex(), action.getData(), action.getType(), action.getPlayer());
    }
    /**
     * @see WindowAbstract#onAction(int, int, WindowActionType, EntityPlayerAbstract)
     * @see #onAction(WindowUiWindow, WindowAction)
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public void onAction(WindowUiWindow window, int index, int data, WindowActionType type, EntityPlayer player)
    {
        window.onAction$super(new WindowAction(player, index, type, data));
    }

    public void onClosed(WindowUiWindow window, EntityPlayer player)
    {
        window.onClosedSuper(player);
        MinecraftServer.instance.schedule(player::updateWindow);
    }

    @FunctionalInterface
    public interface ButtonHandler
    {
        void onClick(EntityPlayer player, WindowActionType actionType, int arg);
    }

    @Override
    public void open(EntityPlayer player)
    {
        WindowFactorySimple.newInstance(
            this.windowType, this.getTitle(player),
            (syncId, inventoryPlayer) -> WindowUiWindow.newInstance(this, player, syncId)
        ).open(player);
        player.updateWindow();
    }

    public static class Module extends MzModule
    {
        public static Module instance = new Module();

        @Override
        public void onLoad()
        {
            this.register(new EventListener<>(
                EventAsyncWindowClose.class, Priority.VERY_LOW,
                event -> event.sync(() ->
                {
                    if(event.isCancelled())
                        return;
                    Window window = event.getPlayer().getWindow(event.getSyncId());
                    if(!window.isInstanceOf(WindowUiWindow.FACTORY))
                        return;
                    window.castTo(WindowUiWindow.FACTORY).getUi().onPlayerClose(event.getPlayer());
                })
            ));
            this.register(new EventListener<>( // fuck V_1700
                EventAsyncWindowAction.class,
                event -> event.sync(() ->
                {
                    if(event.isCancelled())
                        return;
                    Window window = event.getPlayer().getWindow(event.getSyncId());
                    if(!window.isInstanceOf(WindowUiWindow.FACTORY))
                        return;
                    UiWindow ui = window.castTo(WindowUiWindow.FACTORY).getUi();
                    ButtonHandler button = ui.buttons.get(event.getSlotIndex());
                    if(button != null)
                        button.onClick(event.getPlayer(), event.getActionType(), event.getData());
                    for(ControlHit hit : ui.slot2point(event.getSlotIndex()))
                    {
                        if(hit.enabled)
                            hit.control.onAction(event.getAction(), hit.point);
                    }
                })
            ));
            this.register(new EventListener<>(
                EventAsyncPlayerDisplayItemInWindow.class, Priority.VERY_HIGH,
                event -> event.sync(() ->
                {
                    if(Option.some(event).filter(Cancellable.class).map(Cancellable::isCancelled).unwrapOr(false))
                        return;
                    for(WindowUiWindow window : event.getPlayer().getWindow(event.getSyncId())
                        .asOption(WindowUiWindow.FACTORY))
                    {
                        for(Consumer<EventAsyncPlayerDisplayItemInWindow> icon : Option.fromNullable(
                            window.getUi().icons.get(event.getSlotIndex())))
                        {
                            icon.accept(event);
                            return;
                        }
                        for(ControlHit hit : window.getUi()
                            .slot2point(event.getSlotIndex()))
                        {
                            for(ItemStack icon : hit.control.getIcon(event.getPlayer(), hit.point))
                            {
                                event.setItemStack(icon);
                            }
                        }
                    }
                })
            ));
        }
    }
}
