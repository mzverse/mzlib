package mz.mzlib.minecraft;

import mz.mzlib.minecraft.command.CommandManager;
import mz.mzlib.minecraft.command.CommandOutput;
import mz.mzlib.minecraft.datafixer.DataFixerV1300;
import mz.mzlib.minecraft.datafixer.DataUpdaterV900_1300;
import mz.mzlib.minecraft.entity.player.EntityPlayer;
import mz.mzlib.minecraft.incomprehensible.SavePropertiesV1600;
import mz.mzlib.minecraft.incomprehensible.registry.RegistryManagerV1602;
import mz.mzlib.minecraft.recipe.RecipeManager;
import mz.mzlib.minecraft.world.WorldServer;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.minecraft.wrapper.WrapMinecraftFieldAccessor;
import mz.mzlib.minecraft.wrapper.WrapMinecraftMethod;
import mz.mzlib.util.*;
import mz.mzlib.util.async.AsyncFunctionRunner;
import mz.mzlib.util.async.BasicAwait;
import mz.mzlib.util.proxy.IteratorProxy;
import mz.mzlib.util.wrapper.SpecificImpl;
import mz.mzlib.util.wrapper.WrapperFactory;
import mz.mzlib.util.wrapper.WrapperObject;

import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.BooleanSupplier;

@WrapMinecraftClass(@VersionName(name = "net.minecraft.server.MinecraftServer"))
public interface MinecraftServer extends WrapperObject, CommandOutput, Instance, AsyncFunctionRunner
{
    WrapperFactory<MinecraftServer> FACTORY = WrapperFactory.of(MinecraftServer.class);

    MinecraftServer instance = RuntimeUtil.nul();

    @WrapMinecraftFieldAccessor({
        @VersionName(name = "serverThread", end = 2601),
        @VersionName(name = "serverThread", remap = false, begin = 2601)
    })
    Thread getThread();

    Iterable<WorldServer> getWorlds();

    default List<EntityPlayer> getPlayers()
    {
        return this.getPlayerManager().getPlayers();
    }

    @WrapMinecraftMethod({
        @VersionName(name = "getPlayerManager", end = 2601),
        @VersionName(name = "getPlayerList", remap = false, begin = 2601)
    })
    PlayerManager getPlayerManager();

    @WrapMinecraftMethod(
        {
            @VersionName(name = "getCommandManager", end = 1300),
            @VersionName(name = "method_2971", begin = 1300, end = 1400),
            @VersionName(name = "getCommandManager", begin = 1400, end = 2601),
            @VersionName(name = "getCommands", remap = false, begin = 2601)
        }
    )
    CommandManager getCommandManager();

    @MinecraftPlatform.Disabled(MinecraftPlatform.Tag.FOLIA)
    @VersionRange(end = 1300)
    @WrapMinecraftMethod(@VersionName(name = "setupWorld"))
    void tickV_1300();

    @MinecraftPlatform.Disabled(MinecraftPlatform.Tag.FOLIA)
    @VersionRange(begin = 1300)
    @WrapMinecraftMethod({
        @VersionName(name = "method_20324", end = 1400),
        @VersionName(name = "tick", begin = 1400, end = 2601),
        @VersionName(name = "tickServer", remap = false, begin = 2601)
    })
    void tickV1300(BooleanSupplier booleanSupplier);

    Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();

    @Override
    default void schedule(Runnable task)
    {
        tasks.add(task);
    }

    RefStrong<Long> tickNumber = new RefStrong<>(0L);
    Queue<Pair<Long, Runnable>> waitingTasks = new PriorityBlockingQueue<>(
        11, Pair.comparingByFirst(Comparator.comparingLong(x -> x - tickNumber.get()))
    );

    @Override
    default void schedule(Runnable task, BasicAwait await)
    {
        if(await instanceof SleepTicks)
        {
            if(((SleepTicks) await).ticks == 0)
                this.schedule(task);
            else
                waitingTasks.add(new Pair<>(tickNumber.get() + ((SleepTicks) await).ticks, task));
        }
        else
            throw new UnsupportedOperationException();
    }

    @VersionRange(begin = 900, end = 1300)
    @WrapMinecraftFieldAccessor(@VersionName(name = "dataFixer"))
    DataUpdaterV900_1300 getDataUpdaterV900_1300();

    @VersionRange(begin = 1300)
    @WrapMinecraftFieldAccessor({
        @VersionName(name = "field_21612", end = 1400),
        @VersionName(name = "dataFixer", begin = 1400, end = 2601),
        @VersionName(name = "fixerUpper", remap = false, begin = 2601)
    })
    DataFixerV1300 getDataUpdaterV1300();

    /**
     * 1.12.2: 1343
     * 1.13.2: 1631
     * 1.14: 1952
     * 1.19: 3105
     * 1.20.1: 3465
     * 1.20.5: 3837
     */
    int getDataVersion();

    @SpecificImpl("getDataVersion")
    @VersionRange(end = 900)
    default int getDataVersionV_900()
    {
        return 0;
    }

    @SpecificImpl("getDataVersion")
    @VersionRange(begin = 900, end = 1300)
    default int getDataVersionV900_1300()
    {
        return this.getDataUpdaterV900_1300().getDataVersion();
    }

    @SpecificImpl("getDataVersion")
    @VersionRange(begin = 1300, end = 1302)
    default int getDataVersionUnsupported()
    {
        throw new UnsupportedOperationException();
    }
    @SpecificImpl("getDataVersion")
    @VersionRange(begin = 1302, end = 1400)
    default int getDataVersionV1302_1400()
    {
        return 1631;
    }

    @SpecificImpl("getDataVersion")
    @VersionRange(begin = 1400, end = 1800)
    default int getDataVersionV1400_1800()
    {
        return GlobalConstants.getMinecraftVersionV1400_1800().getDataVersion();
    }

    @SpecificImpl("getDataVersion")
    @VersionRange(begin = 1800)
    default int getDataVersionV1800()
    {
        return GlobalConstants.getMinecraftVersionV1800().getDataVersion().getNumber();
    }

    @VersionRange(begin = 1602)
    RegistryManagerV1602 getRegistriesV1602();

    @SpecificImpl("getRegistriesV1602")
    @WrapMinecraftMethod(@VersionName(name = "getRegistryManager", begin = 1602, end = 1802))
    RegistryManagerV1602 getRegistriesV1602_1802();

    @SpecificImpl("getRegistriesV1602")
    @WrapMinecraftMethod({
        @VersionName(name = "getRegistryManager", begin = 1802, end = 2601),
        @VersionName(name = "registryAccess", remap = false, begin = 2601)
    })
    RegistryManagerV1602.Immutable getRegistriesV1802();

    @VersionRange(begin = 1300)
    @WrapMinecraftMethod({
        @VersionName(name = "method_20331", end = 1400),
        @VersionName(name = "getRecipeManager", begin = 1400, end = 2601),
        @VersionName(name = "getRecipeManager", remap = false, begin = 2601)
    })
    RecipeManager getRecipeManagerV1300();

    @VersionRange(begin = 1600)
    @WrapMinecraftMethod({
        @VersionName(name = "getSaveProperties", end = 2601),
        @VersionName(name = "getWorldData", remap = false, begin = 2601)
    })
    SavePropertiesV1600 getSavePropertiesV1600();

    @WrapMinecraftMethod({
        @VersionName(name = "run", end = 1601),
        @VersionName(name = "method_29741", begin = 1601, end = 2601),
        @VersionName(name = "runServer", remap = false, begin = 2601)
    })
    void run();

    @VersionRange(end = 1904)
    @WrapMinecraftMethod(
        {
            @VersionName(name = "setServerMeta", end = 1400),
            @VersionName(name = "setFavicon", begin = 1400)
        }
    )
    void setFaviconV_1904(ServerMetadata metadata);

    @VersionRange(begin = 1904)
    @WrapMinecraftMethod({
        @VersionName(name = "createMetadata", end = 2601),
        @VersionName(name = "buildServerStatus", remap = false, begin = 2601)
    })
    ServerMetadata createMetadataV1904();

    @WrapMinecraftMethod({
        @VersionName(name = "stopServer", end = 1400),
        @VersionName(name = "shutdown", begin = 1400, end = 2601),
        @VersionName(name = "stopServer", remap = false, begin = 2601)
    })
    void onStop();


    @SpecificImpl("getWorlds")
    @VersionRange(end = 1300)
    default Iterable<WorldServer> getWorlds$implV_1300()
    {
        return this.getWorldsV_1300().asList();
    }
    @VersionRange(end = 1300)
    @WrapMinecraftFieldAccessor(@VersionName(name = "worlds"))
    WorldServer.Array getWorldsV_1300();
    @SpecificImpl("getWorlds")
    @VersionRange(begin = 1300)
    default Iterable<WorldServer> getWorldsV1300()
    {
        return IteratorProxy.iterable(this.getWorlds0V1300(), FunctionInvertible.wrapper(WorldServer.FACTORY));
    }
    @VersionRange(begin = 1300)
    @WrapMinecraftMethod({
        @VersionName(name = "method_20351", end = 1400),
        @VersionName(name = "getWorlds", begin = 1400, end = 2601),
        @VersionName(name = "getAllLevels", remap = false, begin = 2601)
    })
    Iterable<Object> getWorlds0V1300();
}
