package mz.mzlib.demo;

import mz.mzlib.demo.game.tictactoe.Tictactoe;
import mz.mzlib.i18n.I18n;
import mz.mzlib.minecraft.MinecraftJsUtil;
import mz.mzlib.minecraft.command.ChildCommandRegistration;
import mz.mzlib.minecraft.command.Command;
import mz.mzlib.minecraft.item.ItemStack;
import mz.mzlib.minecraft.permission.Permission;
import mz.mzlib.minecraft.text.Text;
import mz.mzlib.minecraft.ui.UiStack;
import mz.mzlib.minecraft.ui.window.UiWindowRect;
import mz.mzlib.minecraft.ui.window.control.UiWindowList;
import mz.mzlib.module.MzModule;
import mz.mzlib.util.CollectionUtil;
import mz.mzlib.util.Config;
import mz.mzlib.util.IOUtil;
import mz.mzlib.util.RuntimeUtil;

import java.awt.*;
import java.io.File;
import java.io.InputStream;

public class Demo extends MzModule
{
    public static Demo instance = new Demo();

    public File jar;
    public File dataFolder;

    public Config config;

    public Permission permission = new Permission("mzlibdemo.command.mzlibdemo");
    public Command command;

    @Override
    public void onLoad()
    {
        try
        {
            try(InputStream is = IOUtil.openFileInZip(this.jar, "config.js"))
            {
                this.config = Config.loadJs(MinecraftJsUtil.initScope(), is, new File(this.dataFolder, "config.js"));
            }

            this.register(this.permission);
            this.register(I18n.load(this.jar, "lang", 0));
            this.register(this.command = new Command("mzlibdemo", "mzd").setNamespace("mzlibdemo")
                .setPermissionChecker(sender -> Command.checkPermission(sender, this.permission)));
            this.register(new ChildCommandRegistration(
                this.command, new Command("list")
                .setPermissionChecker(Command::checkPermissionSenderPlayer)
                .setHandler(context ->
                {
                    UiWindowRect ui = new UiWindowRect(6);
                    ui.region.addChild(UiWindowList.overlappedBuilder(CollectionUtil.newArrayList("aaa", "bbb"))
                        .size(new Dimension(9, 10))
                        .iconGetter(entry -> ItemStack.builder().fromId("stick").customName(
                            Text.literal(entry.getElement())).build())
                        .adder(() -> "new")
                        .remover()
                        .viewer((entry -> entry.getPlayer().sendMessage(Text.literal(entry.getElement()))))
                        .build());
                    UiStack.get(context.getSource().getPlayer().unwrap()).go(ui);
//                    UiStack.get(context.getSource().getPlayer().unwrap())
//                        .go(UiWindowList.builder(CollectionUtil.newArrayList("aaa", "bbb"))
//                            .iconGetter((value, player) -> ItemStack.builder().fromId("stick").customName(
//                                Text.literal(value)).build())
//                            .adder(() -> "new")
//                            .remover()
//                            .viewer(((ui, player, index) -> player.sendMessage(Text.literal(ui.getList().get(index)))))
//                            .build());
                })
            ));

            this.register(DemoReload.instance);
            this.register(Tictactoe.instance);
            this.register(DemoBookUi.instance);
            this.register(Inventory10Slots.instance);
            this.register(DemoUIInput.instance);
            this.register(ExampleAsyncFunction.instance);
            this.register(DemoTest.instance);

            this.register(DemoRecipe.instance);
        }
        catch(Throwable e)
        {
            throw RuntimeUtil.sneakilyThrow(e);
        }
    }
}
