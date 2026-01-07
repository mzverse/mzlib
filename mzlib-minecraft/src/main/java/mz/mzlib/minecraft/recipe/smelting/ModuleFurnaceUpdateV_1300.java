package mz.mzlib.minecraft.recipe.smelting;

import mz.mzlib.event.EventListener;
import mz.mzlib.minecraft.MinecraftServer;
import mz.mzlib.minecraft.event.window.async.EventAsyncWindowAction;
import mz.mzlib.minecraft.window.WindowActionType;
import mz.mzlib.minecraft.window.WindowFurnace;
import mz.mzlib.module.MzModule;

public class ModuleFurnaceUpdateV_1300 extends MzModule
{
    public static ModuleFurnaceUpdateV_1300 instance = new ModuleFurnaceUpdateV_1300();

    @Override
    public void onLoad()
    {
        this.register(new EventListener<>(EventAsyncWindowAction.class, this::handle)); // TODO listen sync event
    }

    public void handle(EventAsyncWindowAction event)
    {
        event.sync(() ->
        {
            if(event.getActionType().equals(WindowActionType.SHIFT_CLICK) &&
                event.getPlayer().getCurrentWindow().is(WindowFurnace.FACTORY))
                MinecraftServer.instance.schedule(event.getPlayer()::updateWindow);
        });
    }
}
