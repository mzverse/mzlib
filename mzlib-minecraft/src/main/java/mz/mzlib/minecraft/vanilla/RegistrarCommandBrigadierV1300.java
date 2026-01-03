package mz.mzlib.minecraft.vanilla;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import mz.mzlib.minecraft.command.Command;
import mz.mzlib.minecraft.command.CommandManager;
import mz.mzlib.minecraft.command.CommandSource;
import mz.mzlib.minecraft.command.brigadier.CommandDispatcherV1300;
import mz.mzlib.module.IRegistrar;
import mz.mzlib.module.MzModule;
import mz.mzlib.util.CollectionUtil;
import mz.mzlib.util.RuntimeUtil;

import java.util.ArrayList;
import java.util.List;

public class RegistrarCommandBrigadierV1300 implements IRegistrar<Command>
{
    public static RegistrarCommandBrigadierV1300 instance = new RegistrarCommandBrigadierV1300();

    @Override
    public Class<Command> getType()
    {
        return Command.class;
    }

    @Override
    public void register(MzModule module, Command command)
    {
        List<String> commandNames = CollectionUtil.addAll(CollectionUtil.newArrayList(command.aliases), command.name);
        if(command.namespace != null)
            for(String n : new ArrayList<>(commandNames))
            {
                commandNames.add(command.namespace + ":" + n);
            }
        for(String name : commandNames)
        {
            CommandManager.instance.getDispatcherV1300().getWrapped()
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
        CommandManager.instance.updateAllV1300();
    }

    @Override
    public void unregister(MzModule module, Command command)
    {
        if(command.namespace != null)
            CommandDispatcherV1300.FACTORY.create(CommandManager.instance.getDispatcherV1300().getWrapped()).getRoot()
                .removeChild(command.namespace + ":" + command.name);
        CommandDispatcherV1300.FACTORY.create(CommandManager.instance.getDispatcherV1300().getWrapped()).getRoot()
            .removeChild(command.name);
        for(String alias : command.aliases)
        {
            CommandDispatcherV1300.FACTORY.create(CommandManager.instance.getDispatcherV1300().getWrapped()).getRoot()
                .removeChild(alias);
            if(command.namespace != null)
                CommandDispatcherV1300.FACTORY.create(CommandManager.instance.getDispatcherV1300().getWrapped()).getRoot()
                    .removeChild(command.namespace + ":" + alias);
        }
        CommandManager.instance.updateAllV1300();
    }
}
