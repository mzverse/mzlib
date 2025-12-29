package mz.mzlib.minecraft.ui.window;

import mz.mzlib.minecraft.entity.player.EntityPlayer;
import mz.mzlib.minecraft.inventory.Inventory;
import mz.mzlib.minecraft.inventory.InventorySimple;
import mz.mzlib.minecraft.ui.window.control.UiWindowRegion;
import mz.mzlib.minecraft.window.WindowSlot;
import mz.mzlib.minecraft.window.WindowType;

import java.awt.*;
import java.util.function.BiFunction;

public class UiWindowFull extends UiWindow
{
    public UiWindowRegion region;

    public UiWindowFull(int rowsInventory, Inventory inventory)
    {
        super(WindowType.generic9x(rowsInventory), inventory);
        this.addRegion(this.region = UiWindowRegion.rect(new Dimension(9, rowsInventory + 4), 0));
    }
    public UiWindowFull(int rowsInventory)
    {
        this(rowsInventory, InventorySimple.newInstance(9 * (rowsInventory + 4)));
    }

    public void initWindow(WindowUiWindow window, EntityPlayer player)
    {
        for(int i = 0; i < this.windowType.upperSize + 9 * 4; i++)
        {
            BiFunction<Inventory, Integer, WindowSlot> creator = this.slots.get(i);
            window.addSlot(
                creator == null ?
                    WindowSlotUiWindow.newInstance(this, this.inventory, i) :
                    creator.apply(this.inventory, i));
        }
    }
}
