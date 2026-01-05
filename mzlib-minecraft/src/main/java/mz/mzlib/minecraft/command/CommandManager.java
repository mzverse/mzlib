package mz.mzlib.minecraft.command;

import mz.mzlib.minecraft.MinecraftServer;
import mz.mzlib.minecraft.VersionName;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.minecraft.command.brigadier.CommandDispatcherV1300;
import mz.mzlib.minecraft.entity.player.EntityPlayer;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.minecraft.wrapper.WrapMinecraftMethod;
import mz.mzlib.util.Instance;
import mz.mzlib.util.wrapper.WrapperObject;

@WrapMinecraftClass(@VersionName(name = "net.minecraft.server.command.CommandManager"))
public interface CommandManager extends WrapperObject, Instance
{
    @VersionRange(begin = 1300)
    @WrapMinecraftMethod({
        @VersionName(name = "method_17518", end = 1400),
        @VersionName(name = "getDispatcher", begin = 1400)
    })
    CommandDispatcherV1300 getDispatcherV1300();

    @VersionRange(begin = 1300)
    @WrapMinecraftMethod({
        @VersionName(name = "method_17532", end = 1400),
        @VersionName(name = "sendCommandTree", begin = 1400)
    })
    void sendCommandTreeV1300(EntityPlayer player);

    default void updateAllV1300()
    {
        for(EntityPlayer player : MinecraftServer.instance.getPlayers())
        {
            this.sendCommandTreeV1300(player);
        }
    }
}
