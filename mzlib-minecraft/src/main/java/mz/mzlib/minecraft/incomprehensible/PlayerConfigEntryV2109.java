package mz.mzlib.minecraft.incomprehensible;

import mz.mzlib.minecraft.VersionName;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.util.wrapper.WrapperFactory;
import mz.mzlib.util.wrapper.WrapperObject;

@WrapMinecraftClass({
    @VersionName(name = "net.minecraft.server.PlayerConfigEntry", end = 2601),
    @VersionName(name = "net.minecraft.server.players.NameAndId", begin = 2601)
})
@VersionRange(begin = 2109)
public interface PlayerConfigEntryV2109 extends WrapperObject
{
    WrapperFactory<PlayerConfigEntryV2109> FACTORY = WrapperFactory.of(PlayerConfigEntryV2109.class);
}
