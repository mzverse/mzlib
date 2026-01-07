package mz.mzlib.minecraft;

import mz.mzlib.minecraft.authlib.GameProfile;
import mz.mzlib.minecraft.entity.player.EntityPlayer;
import mz.mzlib.minecraft.incomprehensible.PlayerConfigEntryV2109;
import mz.mzlib.minecraft.network.ClientConnection;
import mz.mzlib.minecraft.network.ClientConnectionDataV2002;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.minecraft.wrapper.WrapMinecraftMethod;
import mz.mzlib.util.FunctionInvertible;
import mz.mzlib.util.Option;
import mz.mzlib.util.proxy.ListProxy;
import mz.mzlib.util.wrapper.SpecificImpl;
import mz.mzlib.util.wrapper.WrapperFactory;
import mz.mzlib.util.wrapper.WrapperObject;

import java.util.List;
import java.util.UUID;

@WrapMinecraftClass({
    @VersionName(name = "net.minecraft.server.PlayerManager", end = 2601),
    @VersionName(name = "net.minecraft.server.players.PlayerList", begin = 2601)
})
public interface PlayerManager extends WrapperObject
{
    WrapperFactory<PlayerManager> FACTORY = WrapperFactory.of(PlayerManager.class);

    default List<EntityPlayer> getPlayers()
    {
        return new ListProxy<>(this.getPlayers0(), FunctionInvertible.wrapper(EntityPlayer.FACTORY));
    }

    default Option<EntityPlayer> getPlayer(String name)
    {
        return Option.fromWrapper(this.getPlayer0(name));
    }

    boolean isOp(EntityPlayer player);

    default Option<EntityPlayer> getPlayer(UUID uuid)
    {
        return Option.fromWrapper(this.getPlayer0(uuid));
    }


    @WrapMinecraftMethod({
        @VersionName(name = "getPlayers", end = 1400),
        @VersionName(name = "method_14571", begin = 1400, end = 2601),
        @VersionName(name = "getPlayers", remap = false, begin = 2601)
    })
    List<Object> getPlayers0();

    @WrapMinecraftMethod({
        @VersionName(name = "getPlayer", end = 2601),
        @VersionName(name = "getPlayerByName", remap = false, begin = 2601)
    })
    EntityPlayer getPlayer0(String name);

    @SpecificImpl("isOp")
    @VersionRange(end = 2109)
    default boolean isOpV_2109(EntityPlayer player)
    {
        return this.isOpV_2109(player.getGameProfile());
    }
    @SpecificImpl("isOp")
    @VersionRange(begin = 2109)
    default boolean isOpV2109(EntityPlayer player)
    {
        return this.isOpV2109(player.getPlayerConfigEntryV2109());
    }

    @WrapMinecraftMethod(@VersionName(name = "isOperator"))
    @VersionRange(end = 2109)
    boolean isOpV_2109(GameProfile playerProfile);
    @WrapMinecraftMethod({
        @VersionName(name = "isOperator", end = 2601),
        @VersionName(name = "isOp", remap = false, begin = 2601)
    })
    @VersionRange(begin = 2109)
    boolean isOpV2109(PlayerConfigEntryV2109 key);

    @WrapMinecraftMethod({
        @VersionName(name = "getPlayer", end = 2601),
        @VersionName(name = "getPlayer", remap = false, begin = 2601)
    })
    EntityPlayer getPlayer0(UUID uuid);

    @VersionRange(end = 2002)
    @WrapMinecraftMethod({
        @VersionName(name = "onPlayerConnect", end = 900),
        @VersionName(name = "method_12827", begin = 900, end = 1400),
        @VersionName(name = "onPlayerConnect", begin = 1400)
    })
    void addPlayerV_2002(ClientConnection connection, EntityPlayer player);

    @VersionRange(begin = 2002)
    @WrapMinecraftMethod({
        @VersionName(name = "onPlayerConnect", end = 2601),
        @VersionName(name = "placeNewPlayer", remap = false, begin = 2601)
    })
    void addPlayerV2002(ClientConnection connection, EntityPlayer player, ClientConnectionDataV2002 connectionData);
}
