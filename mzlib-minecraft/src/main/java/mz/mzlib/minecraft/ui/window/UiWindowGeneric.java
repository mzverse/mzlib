package mz.mzlib.minecraft.ui.window;

import mz.mzlib.minecraft.inventory.Inventory;
import mz.mzlib.minecraft.inventory.InventorySimple;
import mz.mzlib.minecraft.ui.window.control.UiWindowRegion;
import mz.mzlib.minecraft.window.WindowType;

import java.awt.*;

public class UiWindowGeneric extends UiWindow
{
    public UiWindowRegion regionInventory;
    public UiWindowRegion regionPlayer;

    public UiWindowGeneric(int rowsInventory, Inventory inventory)
    {
        super(WindowType.generic9x(rowsInventory), inventory);
        this.addRegion(this.regionInventory = UiWindowRegion.rect(new Dimension(9, rowsInventory), 0));
        this.addRegion(this.regionPlayer = UiWindowRegion.rect(new Dimension(9, 4), 9 * rowsInventory));
    }
    public UiWindowGeneric(int rowsInventory)
    {
        this(rowsInventory, InventorySimple.newInstance(rowsInventory * 9));
    }
}
