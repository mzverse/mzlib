package mz.mzlib.minecraft.world;

import mz.mzlib.minecraft.VersionName;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.minecraft.wrapper.WrapMinecraftFieldAccessor;
import mz.mzlib.util.wrapper.WrapperFactory;
import mz.mzlib.util.wrapper.WrapperObject;

@WrapMinecraftClass(@VersionName(name = "net.minecraft.world.World"))
public interface World extends WrapperObject
{
    WrapperFactory<World> FACTORY = WrapperFactory.of(World.class);

    @WrapMinecraftFieldAccessor(@VersionName(name = "isClient"))
    boolean isClient();
}
