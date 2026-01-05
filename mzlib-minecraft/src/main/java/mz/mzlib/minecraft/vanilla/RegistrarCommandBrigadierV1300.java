package mz.mzlib.minecraft.vanilla;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import mz.mzlib.minecraft.MinecraftServer;
import mz.mzlib.minecraft.VersionName;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.minecraft.command.Command;
import mz.mzlib.minecraft.command.CommandManager;
import mz.mzlib.minecraft.command.CommandSource;
import mz.mzlib.minecraft.command.brigadier.CommandDispatcherV1300;
import mz.mzlib.minecraft.command.brigadier.CommandNodeV1300;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.minecraft.wrapper.WrapMinecraftInnerClass;
import mz.mzlib.module.IRegistrar;
import mz.mzlib.module.MzModule;
import mz.mzlib.util.CollectionUtil;
import mz.mzlib.util.RuntimeUtil;
import mz.mzlib.util.nothing.Nothing;
import mz.mzlib.util.nothing.NothingInject;
import mz.mzlib.util.nothing.NothingInjectType;
import mz.mzlib.util.wrapper.WrapSameClass;
import mz.mzlib.util.wrapper.WrapperFactory;
import mz.mzlib.util.wrapper.WrapperObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RegistrarCommandBrigadierV1300 extends MzModule implements IRegistrar<Command>
{
    public static RegistrarCommandBrigadierV1300 instance = new RegistrarCommandBrigadierV1300();

    @Override
    public Class<Command> getType()
    {
        return Command.class;
    }

    Set<Command> registeredCommands = new HashSet<>();
    @Override
    public void register(MzModule module, Command command)
    {
        this.registeredCommands.add(command);
        this.register(MinecraftServer.instance.getCommandManager(), command);
    }
    void register(CommandManager manager, Command command)
    {
        List<String> commandNames = CollectionUtil.addAll(CollectionUtil.newArrayList(command.aliases), command.name);
        if(command.namespace != null)
            for(String n : new ArrayList<>(commandNames))
            {
                commandNames.add(command.namespace + ":" + n);
            }
        for(String name : commandNames)
        {
            manager.getDispatcherV1300().getWrapped()
                .register(RuntimeUtil.cast(LiteralArgumentBuilder.literal(name).executes(context ->
                {
                    command.execute(
                        CommandSource.FACTORY.create(context.getSource()), context.getRange().get(context.getInput()), null);
                    return 1;
                }).then(RequiredArgumentBuilder.argument("args", StringArgumentType.greedyString()).executes(context ->
                {
                    command.execute(
                        CommandSource.FACTORY.create(context.getSource()),
                        context.getNodes().get(0).getRange().get(context.getInput()),
                        context.getNodes().get(1).getRange().get(context.getInput())
                    );
                    return 1;
                }).suggests((context, b) ->
                {
                    while(context.getChild() != null)
                    {
                        context = context.getChild();
                    }
                    int start = context.getRange().getStart();
                    String input = context.getInput().substring(start);
                    b = b.createOffset(start + input.lastIndexOf(' ') + 1);
                    String[] argv2 = input.split(" ", 2);
                    for(String s : command.suggest(CommandSource.FACTORY.create(context.getSource()), argv2[0], argv2[1]))
                    {
                        b.suggest(s, null);
                    }
                    return b.buildFuture();
                }).build())));
        }
        manager.updateAllV1300();
    }

    @Override
    public void unregister(MzModule module, Command command)
    {
        this.registeredCommands.remove(command);
        CommandNodeV1300 root = CommandDispatcherV1300.FACTORY.create(
            MinecraftServer.instance.getCommandManager().getDispatcherV1300().getWrapped()).getRoot();
        if(command.namespace != null)
            root.removeChild(command.namespace + ":" + command.name);
        root.removeChild(command.name);
        for(String alias : command.aliases)
        {
            root.removeChild(alias);
            if(command.namespace != null)
                root.removeChild(command.namespace + ":" + alias);
        }
        MinecraftServer.instance.getCommandManager().updateAllV1300();
    }

    @Override
    public void onLoad()
    {
        this.register(NothingCommandManager.class);
    }

    @WrapSameClass(CommandManager.class)
    public interface NothingCommandManager extends Nothing, CommandManager
    {
        default void handle()
        {
            for(Command command : RegistrarCommandBrigadierV1300.instance.registeredCommands)
            {
                RegistrarCommandBrigadierV1300.instance.register(this, command);
            }
        }
        @VersionRange(end = 900)
        @NothingInject(
            wrapperMethodName = "<init>", wrapperMethodParams = {},
            locateMethod = "locateAllReturn", type = NothingInjectType.INSERT_BEFORE
        )
        default void of$endV_900()
        {
            this.handle();
        }
        @VersionRange(begin = 900, end = 1300)
        @NothingInject(
            wrapperMethodName = "<init>", wrapperMethodParams = { MinecraftServer.class },
            locateMethod = "locateAllReturn", type = NothingInjectType.INSERT_BEFORE
        )
        default void of$endV900_1300()
        {
            this.handle();
        }
        @VersionRange(begin = 1300, end = 1600)
        @NothingInject(
            wrapperMethodName = "<init>", wrapperMethodParams = { boolean.class }, // isDedicatedServer
            locateMethod = "locateAllReturn", type = NothingInjectType.INSERT_BEFORE
        )
        default void of$endV1300_1600()
        {
            this.handle();
        }
        @VersionRange(begin = 1600, end = 1900)
        @NothingInject(
            wrapperMethodName = "<init>", wrapperMethodParams = { RegistrationEnvironmentV1600.class },
            locateMethod = "locateAllReturn", type = NothingInjectType.INSERT_BEFORE
        )
        default void of$endV1600_1900()
        {
            this.handle();
        }
        @VersionRange(begin = 1900)
        @NothingInject(
            wrapperMethodName = "<init>", wrapperMethodParams = { RegistrationEnvironmentV1600.class, CommandRegistryAccessV1900.class },
            locateMethod = "locateAllReturn", type = NothingInjectType.INSERT_BEFORE
        )
        default void of$endV1900()
        {
            this.handle();
        }
    }

    @VersionRange(begin = 1600)
    @WrapMinecraftInnerClass(outer = CommandManager.class, name = @VersionName(name = "class_5364"))
    public interface RegistrationEnvironmentV1600 extends WrapperObject
    {
        WrapperFactory<RegistrationEnvironmentV1600> FACTORY = WrapperFactory.of(RegistrationEnvironmentV1600.class);
    }
    @VersionRange(begin = 1900)
    @WrapMinecraftClass(@VersionName(name = "net.minecraft.command.CommandRegistryAccess"))
    public interface CommandRegistryAccessV1900 extends WrapperObject
    {
        WrapperFactory<CommandRegistryAccessV1900> FACTORY = WrapperFactory.of(CommandRegistryAccessV1900.class);
    }
}
