package mz.mzlib.minecraft.network.packet;

import mz.mzlib.minecraft.VersionName;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.minecraft.wrapper.WrapMinecraftFieldAccessor;
import mz.mzlib.util.wrapper.WrapperFactory;
import mz.mzlib.util.wrapper.WrapperObject;

@WrapMinecraftClass(@VersionName(name = "net.minecraft.network.NetworkSide"))
public interface PacketDirection extends WrapperObject
{
    WrapperFactory<PacketDirection> FACTORY = WrapperFactory.of(PacketDirection.class);

    PacketDirection C2S = FACTORY.getStatic().static$c2s();
    PacketDirection S2C = FACTORY.getStatic().static$s2c();

    @Deprecated
    static PacketDirection c2s()
    {
        return C2S;
    }
    @Deprecated
    static PacketDirection s2c()
    {
        return S2C;
    }


    @WrapMinecraftFieldAccessor({
        @VersionName(name = "SERVERBOUND", end = 1400),
        @VersionName(name = "field_11941", begin = 1400)
    })
    PacketDirection static$c2s();
    @WrapMinecraftFieldAccessor({
        @VersionName(name = "CLIENTBOUND", end = 1400),
        @VersionName(name = "field_11942", begin = 1400)
    })
    PacketDirection static$s2c();
}
