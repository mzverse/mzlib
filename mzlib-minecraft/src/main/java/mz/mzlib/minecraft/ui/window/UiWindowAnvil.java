package mz.mzlib.minecraft.ui.window;

import mz.mzlib.Priority;
import mz.mzlib.event.EventListener;
import mz.mzlib.minecraft.entity.player.EntityPlayer;
import mz.mzlib.minecraft.event.window.async.EventAsyncWindowAnvilSetName;
import mz.mzlib.minecraft.inventory.Inventory;
import mz.mzlib.minecraft.ui.Ui;
import mz.mzlib.minecraft.window.Window;
import mz.mzlib.minecraft.window.WindowType;
import mz.mzlib.module.MzModule;

public abstract class UiWindowAnvil extends UiWindow // FIXME: V_1100
{
    public UiWindowAnvil(Inventory inventory)
    {
        super(WindowType.ANVIL, inventory);
    }

    public UiWindowAnvil()
    {
        super(WindowType.ANVIL, 3);
    }

    public void onNameChanged(EntityPlayer player, String name)
    {
    }

    public static class Module extends MzModule
    {
        public static Module instance = new Module();

        @Override
        public void onLoad()
        {
            this.register(new EventListener<>(
                EventAsyncWindowAnvilSetName.class, Priority.VERY_LOW, event -> event.sync(() ->
            {
                if(event.isCancelled())
                    return;
                Window window = event.getPlayer().getCurrentWindow();
                if(!window.isInstanceOf(WindowUiWindow.FACTORY))
                    return;
                Ui ui = window.castTo(WindowUiWindow.FACTORY).getUi();
                if(ui instanceof UiWindowAnvil)
                {
                    event.sync(() ->
                    {
                        if(window.isInstanceOf(WindowUiWindow.FACTORY))
                        {
                            Ui ui1 = window.castTo(WindowUiWindow.FACTORY).getUi();
                            if(ui1 instanceof UiWindowAnvil)
                                ((UiWindowAnvil) ui1).onNameChanged(event.getPlayer(), event.getName());
                        }
                    });
                }
            })
            ));
        }
    }
}
