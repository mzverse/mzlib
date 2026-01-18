package mz.mzlib.minecraft.network.packet;

import io.netty.channel.Channel;
import mz.mzlib.minecraft.entity.player.EntityPlayer;
import mz.mzlib.util.Option;
import mz.mzlib.util.TaskQueue;
import mz.mzlib.util.wrapper.WrapperFactory;

import java.util.function.Function;

public class PacketEvent
{
    public Channel channel;
    public Option<EntityPlayer> player;
    final Packet packet; // ref
    public boolean isCancelled = false;
    public PacketEvent(Channel channel, Option<EntityPlayer> player, final Packet packet)
    {
        this.channel = channel;
        this.player = player;
        this.packet = packet;
    }

    public Option<EntityPlayer> getPlayer()
    {
        return this.player;
    }

    public <T extends Packet> T getPacket(WrapperFactory<T> factory)
    {
        return this.packet.as(factory);
    }
    public Packet getPacket()
    {
        return this.getPacket(Packet.FACTORY);
    }
    @Deprecated
    public <T extends Packet> T getPacket(Function<Object, T> creator)
    {
        return this.getPacket(new WrapperFactory<>(creator));
    }
    public void setPacket(Packet value)
    {
        this.packet.setWrappedFrom(value);
    }

    /**
     * @see #setPacket
     */
    @Deprecated
    public void ensureCopied()
    {
        if(this.isCopied)
            return;
        this.setPacket(this.packet.copy(this.channel.alloc()));
        this.isCopied = true;
    }
    @Deprecated
    public boolean isCopied = false;

    public TaskQueue syncTasks = null;
    public void sync(Runnable task)
    {
        if(this.syncTasks != null)
            this.syncTasks.schedule(task);
        else
            task.run();
    }

    public void setCancelled(boolean cancelled)
    {
        this.isCancelled = cancelled;
    }
    public boolean isCancelled()
    {
        return this.isCancelled;
    }

    public <T extends Packet> Specialized<T> specialize(WrapperFactory<T> factory)
    {
        return new Specialized<>(this, factory);
    }

    public static class Specialized<T extends Packet>
    {
        PacketEvent base;
        WrapperFactory<T> type;
        public Specialized(PacketEvent common, WrapperFactory<T> creator)
        {
            this.base = common;
            this.type = creator;
        }

        public PacketEvent getBase()
        {
            return this.base;
        }
        public WrapperFactory<T> getType()
        {
            return this.type;
        }

        public Option<EntityPlayer> getPlayer()
        {
            return this.getBase().getPlayer();
        }

        public T getPacket()
        {
            return this.getBase().getPacket(this.getType());
        }

        public void setPacket(Packet value)
        {
            this.getBase().setPacket(value);
        }

        /**
         * @see #setPacket
         */
        @Deprecated
        public void ensureCopied()
        {
            this.getBase().ensureCopied();
        }

        public void sync(Runnable task)
        {
            this.getBase().sync(task);
        }

        public void setCancelled(boolean cancelled)
        {
            this.getBase().setCancelled(cancelled);
        }
        public boolean isCancelled()
        {
            return this.getBase().isCancelled();
        }
    }
}
