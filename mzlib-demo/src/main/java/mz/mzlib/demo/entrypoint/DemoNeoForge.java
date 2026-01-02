package mz.mzlib.demo.entrypoint;

import mz.mzlib.demo.Demo;
import mz.mzlib.minecraft.vanilla.MzLibMinecraftInitializer;
import mz.mzlib.minecraft.vanilla.ServerModule;
import mz.mzlib.module.MzModule;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;

import java.io.File;

@Mod("mzlib_demo")
public class DemoNeoForge extends MzModule
{
    public static DemoNeoForge instance;
    {
        instance = this;

        Demo.instance.jar = ModList.get().getModContainerById("mzlib_demo").orElseThrow(AssertionError::new)
            .getModInfo().getOwningFile().getFile().getFilePath().toFile();
        Demo.instance.dataFolder = new File(Demo.instance.jar.getParentFile(), "MzLibDemo");
        MzLibMinecraftInitializer.instance.future.thenRun(this::load);
    }

    @Override
    public void onLoad()
    {
        this.register(new ServerModule(Demo.instance));
    }
}
