package mz.mzlib.minecraft;

import mz.mzlib.i18n.I18n;
import mz.mzlib.minecraft.command.Command;
import mz.mzlib.minecraft.commands.*;
import mz.mzlib.minecraft.entity.display.DisplayEntityTracker;
import mz.mzlib.minecraft.event.MinecraftEventModule;
import mz.mzlib.minecraft.i18n.MinecraftI18n;
import mz.mzlib.minecraft.item.Item;
import mz.mzlib.minecraft.item.ModuleItemDisplaySign;
import mz.mzlib.minecraft.mzitem.MzItem;
import mz.mzlib.minecraft.nbt.NbtCompound;
import mz.mzlib.minecraft.network.packet.ModulePacketListener;
import mz.mzlib.minecraft.recipe.ModuleRecipe;
import mz.mzlib.minecraft.text.Text;
import mz.mzlib.minecraft.ui.UiStack;
import mz.mzlib.minecraft.ui.book.UiWrittenBook;
import mz.mzlib.minecraft.ui.window.UiAbstractWindow;
import mz.mzlib.minecraft.ui.window.UiWindowAnvil;
import mz.mzlib.minecraft.window.ModuleWindow;
import mz.mzlib.module.MzModule;
import mz.mzlib.util.Config;
import mz.mzlib.util.IOUtil;
import mz.mzlib.util.RuntimeUtil;
import mz.mzlib.util.wrapper.TesterJarWrappers;

import java.io.File;
import java.io.InputStream;

public class MzLibMinecraft extends MzModule
{
    public static MzLibMinecraft instance = new MzLibMinecraft();

    public final String MOD_ID = "mzlib";

    public Config config;

    public Command command;

    @Override
    public void onLoad()
    {
        try
        {
            try(InputStream is = IOUtil.openFileInZip(MinecraftPlatform.instance.getMzLibJar(), "config.js"))
            {
                this.config = Config.loadJs(
                    MinecraftJsUtil.initScope(), is,
                    new File(MinecraftPlatform.instance.getMzLibDataFolder(), "config.js")
                );
            }

            this.register(I18n.load(MinecraftPlatform.instance.getMzLibJar(), "lang", 0));

            this.register(MinecraftI18n.instance);

            this.register(ModulePacketListener.instance);

            this.register(MinecraftEventModule.instance);

            this.register(Item.Module.instance);

            this.register(
                new TesterJarWrappers(MinecraftPlatform.instance.getMzLibJar(), MzLibMinecraft.class.getClassLoader()));

            this.register(NothingMinecraftServer.class);

            this.register(ModuleRecipe.instance);

            this.register(this.command = new Command("mzlib", "mz").setNamespace(this.MOD_ID));

            this.register(DisplayEntityTracker.Module.instance);

            this.register(ModuleWindow.instance);
            this.register(UiStack.Module.instance);
            this.register(UiWrittenBook.Module.instance);
            this.register(UiAbstractWindow.Module.instance);
            this.register(UiWindowAnvil.Module.instance);

            this.register(NbtCompound.Module.instance);
            this.register(Text.Module.instance);

            this.register(ModuleItemDisplaySign.instance);

            this.register(CommandMzLibTest.instance);
            this.register(CommandMzLibLang.instance);
            this.register(CommandMzLibItemInfo.instance);
            this.register(CommandMzLibJs.instance);
            this.register(CommandMzLibGive.instance);

            this.register(CommandGiveNbt.instance);

            this.register(MzItem.Module.instance);
        }
        catch(Throwable e)
        {
            throw RuntimeUtil.sneakilyThrow(e);
        }
    }
}
