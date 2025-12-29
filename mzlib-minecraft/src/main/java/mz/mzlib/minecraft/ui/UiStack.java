package mz.mzlib.minecraft.ui;

import mz.mzlib.Priority;
import mz.mzlib.event.EventListener;
import mz.mzlib.minecraft.MinecraftServer;
import mz.mzlib.minecraft.entity.player.EntityPlayer;
import mz.mzlib.minecraft.event.player.EventPlayerJoin;
import mz.mzlib.minecraft.event.player.EventPlayerQuit;
import mz.mzlib.module.MzModule;
import mz.mzlib.util.Option;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UiStack
{
    static Map<EntityPlayer, UiStack> uiStacks = new ConcurrentHashMap<>();
    static Map<Ui, Set<EntityPlayer>> viewersMap = new WeakHashMap<>();

    public static class Module extends MzModule
    {
        public static Module instance = new Module();

        @Override
        public void onLoad()
        {
            this.register(new EventListener<>(
                EventPlayerJoin.class, Priority.VERY_VERY_HIGH, event ->
            {
                uiStacks.put(event.getPlayer(), new UiStack(event.getPlayer()));
                if(event.isCancelled())
                    uiStacks.remove(event.getPlayer());
            }
            ));
            for(EntityPlayer player : MinecraftServer.instance.getPlayers())
            {
                uiStacks.put(player, new UiStack(player));
            }
            this.register(new EventListener<>(
                EventPlayerQuit.class, Priority.VERY_VERY_LOW,
                event -> uiStacks.remove(event.getPlayer())
            ));
        }
    }

    public static UiStack get(EntityPlayer player)
    {
        return uiStacks.get(player);
    }
    public static synchronized Set<EntityPlayer> getViewers(Ui ui)
    {
        return Option.fromNullable(viewersMap.get(ui)).map(HashSet::new).unwrapOrGet(HashSet::new);
    }

    public EntityPlayer player;
    public List<Ui> data = new ArrayList<>();
    public UiStack(EntityPlayer player)
    {
        this.player = player;
    }

    public void start(Ui ui)
    {
        synchronized(UiStack.class)
        {
            this.clear();
            this.go(ui);
        }
    }

    public void go(Ui ui)
    {
        synchronized(UiStack.class)
        {
            viewersMap.computeIfAbsent(ui, it -> new HashSet<>()).add(player);
            this.data.add(ui);
            ui.open(this.player);
        }
    }

    public void back()
    {
        synchronized(UiStack.class)
        {
            viewersMap.get(this.top()).remove(this.player);
            if(this.data.size() == 1)
                this.data.get(0).close(this.player);
            this.data.remove(this.data.size() - 1);
            if(!this.data.isEmpty())
            {
                Ui ui = this.top();
                viewersMap.computeIfAbsent(ui, it -> new HashSet<>()).add(player);
                ui.open(this.player);
            }
        }
    }

    public void replace(Ui ui)
    {
        synchronized(UiStack.class)
        {
            viewersMap.get(this.top()).remove(this.player);
            this.data.remove(this.data.size() - 1);
            this.go(ui);
        }
    }

    public Ui top()
    {
        synchronized(UiStack.class)
        {
            if(this.data.isEmpty())
                return null;
            return this.data.get(this.data.size() - 1);
        }
    }

    void clear()
    {
        synchronized(UiStack.class)
        {
            Ui ui = this.top();
            if(ui != null)
                viewersMap.get(ui).remove(player);
            this.data.clear();
        }
    }

    public void close()
    {
        synchronized(UiStack.class)
        {
            if(this.data.isEmpty())
                return;
            this.data.get(this.data.size() - 1).close(this.player);
            this.clear();
        }
    }
}
