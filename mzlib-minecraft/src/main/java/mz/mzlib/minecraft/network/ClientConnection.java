package mz.mzlib.minecraft.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.GenericFutureListener;
import mz.mzlib.minecraft.VersionName;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.minecraft.entity.player.EntityPlayer;
import mz.mzlib.minecraft.network.listener.MinecraftPacketListener;
import mz.mzlib.minecraft.network.packet.Packet;
import mz.mzlib.minecraft.network.packet.PacketCallbacksV1901;
import mz.mzlib.minecraft.network.packet.PacketDirection;
import mz.mzlib.minecraft.network.packet.PacketHandler;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.minecraft.wrapper.WrapMinecraftFieldAccessor;
import mz.mzlib.minecraft.wrapper.WrapMinecraftMethod;
import mz.mzlib.util.Option;
import mz.mzlib.util.wrapper.WrapMethod;
import mz.mzlib.util.wrapper.WrapperFactory;
import mz.mzlib.util.wrapper.WrapperObject;

@WrapMinecraftClass(@VersionName(name = "net.minecraft.network.ClientConnection"))
public interface ClientConnection extends WrapperObject
{
    WrapperFactory<ClientConnection> FACTORY = WrapperFactory.of(ClientConnection.class);

    @WrapMinecraftFieldAccessor(@VersionName(name = "side"))
    PacketDirection getReceivingDirection();

    @WrapMinecraftMethod(@VersionName(name = "getPacketListener"))
    MinecraftPacketListener getPacketListener();

    @WrapMinecraftFieldAccessor(@VersionName(name = "channel"))
    Channel getChannel();

    default Option<EntityPlayer> getPlayer()
    {
        return this.getPacketListener().asOption(ServerPlayNetworkHandler.FACTORY)
            .map(ServerPlayNetworkHandler::getPlayer);
    }

    @WrapMinecraftMethod(@VersionName(name = "send"))
    void sendPacket(Packet packet);

    @VersionRange(end = 1300)
    @WrapMinecraftMethod(@VersionName(name = "sendImmediately"))
    void sendPacketImmediatelyV_1300(Packet packet, GenericFutureListener<?>[] callbacks);

    @VersionRange(begin = 1300, end = 1901)
    @WrapMinecraftMethod({
        @VersionName(name = "method_20161", end = 1400),
        @VersionName(name = "sendImmediately", begin = 1400)
    })
    void sendPacketImmediatelyV1300_1901(Packet packet, GenericFutureListener<?> callbacks);

    @VersionRange(begin = 1901, end = 2002)
    @WrapMinecraftMethod(@VersionName(name = "sendImmediately"))
    void sendPacketImmediatelyV1901_2002(Packet packet, PacketCallbacksV1901 callbacks);

    @VersionRange(begin = 2002, end = 2106)
    @WrapMinecraftMethod(@VersionName(name = "sendImmediately"))
    void sendPacketImmediatelyV2002_2106(Packet packet, PacketCallbacksV1901 callbacks, boolean flush);

    @VersionRange(begin = 2106)
    @WrapMinecraftMethod(@VersionName(name = "sendImmediately"))
    void sendPacketImmediatelyV2106(Packet packet, ChannelFutureListener callbacks, boolean flush);

    @WrapMethod("@0")
    void channelRead0(ChannelHandlerContext ctx, Packet packet);

    static void handlePacketV1300(Packet packet, PacketHandler handler)
    {
        FACTORY.getStatic().static$handlePacketV1300(packet, handler);
    }
    @VersionRange(begin = 1300)
    @WrapMinecraftMethod({
        @VersionName(name = "method_20159", end = 1400),
        @VersionName(name = "handlePacket", begin = 1400)
    })
    void static$handlePacketV1300(Packet packet, PacketHandler handler);

    @WrapMinecraftMethod(@VersionName(name = "handleDisconnection"))
    void handleDisconnection();
}
