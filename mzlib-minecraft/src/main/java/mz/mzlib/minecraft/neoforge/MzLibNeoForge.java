package mz.mzlib.minecraft.neoforge;

import mz.mzlib.MzLib;
import mz.mzlib.minecraft.vanilla.MzLibMinecraftInitializer;
import mz.mzlib.module.MzModule;
import net.neoforged.fml.common.Mod;

@Mod("mzlib")
public class MzLibNeoForge extends MzModule
{
    public static MzLibNeoForge instance;
    {
        instance = this;
        this.load();
    }

    @Override
    public void onLoad()
    {
        this.register(MzLib.instance);
        this.register(new MinecraftPlatformNeoForge());
        this.register(MzLibMinecraftInitializer.instance);
    }
}
