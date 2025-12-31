package mz.mzlib.minecraft.vanilla;

import mz.mzlib.event.EventListener;
import mz.mzlib.minecraft.event.server.EventServerStart;
import mz.mzlib.minecraft.event.server.EventServerStop;
import mz.mzlib.module.MzModule;
import mz.mzlib.module.Registrable;
import mz.mzlib.util.CollectionUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerModule implements Registrable
{
    public MzModule module;

    public ServerModule(MzModule module)
    {
        this.module = module;
    }

    public static LinkedHashMap<ServerModule, MzModule> moduleParents = new LinkedHashMap<>();

    @Override
    public void onRegister(MzModule module)
    {
        moduleParents.put(this, module);
    }
    @Override
    public void onUnregister(MzModule module)
    {
        moduleParents.remove(this);
    }

    public static class Module extends MzModule
    {
        public static Module instance = new Module();

        @Override
        public void onLoad()
        {
            this.register(new EventListener<>(
                EventServerStart.class, event -> event.runLater(() ->
            {
                this.register(event.server);
                for(Map.Entry<ServerModule, MzModule> entry : moduleParents.entrySet())
                {
                    try
                    {
                        entry.getValue().register(entry.getKey().module);
                    }
                    catch(Throwable e)
                    {
                        e.printStackTrace(System.err);
                    }
                }
            })
            ));
            this.register(new EventListener<>(
                EventServerStop.class, event ->
            {
                for(Map.Entry<ServerModule, MzModule> entry : CollectionUtil.reverse(moduleParents.entrySet().stream())
                    .collect(Collectors.toList()))
                {
                    try
                    {
                        entry.getValue().unregister(entry.getKey().module);
                    }
                    catch(Throwable e)
                    {
                        e.printStackTrace(System.err);
                    }
                }
                this.unregister(event.server);
            }
            ));
        }
    }
}
