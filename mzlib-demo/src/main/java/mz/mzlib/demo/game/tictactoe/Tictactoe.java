package mz.mzlib.demo.game.tictactoe;

import mz.mzlib.demo.Demo;
import mz.mzlib.minecraft.MinecraftPlatform;
import mz.mzlib.minecraft.command.Command;
import mz.mzlib.minecraft.entity.player.EntityPlayer;
import mz.mzlib.minecraft.i18n.MinecraftI18n;
import mz.mzlib.minecraft.item.Item;
import mz.mzlib.minecraft.item.ItemStack;
import mz.mzlib.minecraft.text.Text;
import mz.mzlib.minecraft.ui.UiStack;
import mz.mzlib.minecraft.ui.window.UiWindow;
import mz.mzlib.minecraft.ui.window.WindowSlotIcon;
import mz.mzlib.minecraft.ui.window.WindowUiWindow;
import mz.mzlib.minecraft.window.WindowAction;
import mz.mzlib.minecraft.window.WindowActionType;
import mz.mzlib.minecraft.window.WindowSlotOutput;
import mz.mzlib.minecraft.window.WindowType;
import mz.mzlib.module.MzModule;
import mz.mzlib.util.Option;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tictactoe extends MzModule
{
    public static Tictactoe instance = new Tictactoe();

    public Command command;

    @Override
    public void onLoad()
    {
        Demo.instance.command.addChild(
            this.command = new Command("tictactoe").setPermissionChecker(Command::checkPermissionSenderPlayer)
                .setHandler(context ->
                {
                    if(context.argsReader.hasNext())
                        context.successful = false;
                    if(!context.successful || !context.doExecute)
                        return;
                    UiStack.get(context.getSource().getPlayer().unwrap())
                        .start(new UiTictactoe(context.getSource().getPlayer().unwrap()));
                }));
    }

    @Override
    public void onUnload()
    {
        Demo.instance.command.removeChild(this.command);
    }

    public static class UiTictactoe extends UiWindow
    {
        public ItemStack PLAYER;
        public ItemStack AI;

        public UiTictactoe(EntityPlayer player)
        {
            super(WindowType.CRAFTING, 10);

            PLAYER = ItemStack.builder().fromId(
                MinecraftPlatform.instance.getVersion() < 1000 ? "ender_eye" : "structure_void").data(
                Item.CUSTOM_NAME,
                Option.some(MinecraftI18n.resolveText(player.getLanguage(), "mzlibdemo.game.tictactoe.piece.player"))
            ).build();
            AI = ItemStack.builder().fromId("barrier").data(
                Item.CUSTOM_NAME,
                Option.some(MinecraftI18n.resolveText(player.getLanguage(), "mzlibdemo.game.tictactoe.piece.ai"))
            ).build();

            this.putSlot(0, WindowSlotOutput::newInstance);
            for(int i = 0; i < 9; i++)
            {
                this.putSlot(1 + i, WindowSlotIcon::newInstance);
            }
        }

        public boolean finished = false;

        public boolean checkWin()
        {
            if(!this.inventory.getItemStack(1 + 4).isEmpty())
            {
                if(this.inventory.getItemStack(1 + 4).equals(this.inventory.getItemStack(1)) &&
                    this.inventory.getItemStack(1 + 4).equals(this.inventory.getItemStack(1 + 8)))
                    return true;
                if(this.inventory.getItemStack(1 + 4).equals(this.inventory.getItemStack(1 + 2)) &&
                    this.inventory.getItemStack(1 + 4).equals(this.inventory.getItemStack(1 + 6)))
                    return true;
            }
            for(int i = 0; i < 3; i++)
            {
                if(!this.inventory.getItemStack(1 + i * 3).isEmpty())
                {
                    if(this.inventory.getItemStack(1 + i * 3).equals(this.inventory.getItemStack(1 + i * 3 + 1)) &&
                        this.inventory.getItemStack(1 + i * 3).equals(this.inventory.getItemStack(1 + i * 3 + 2)))
                        return true;
                }
                if(!this.inventory.getItemStack(1 + i).isEmpty())
                {
                    if(this.inventory.getItemStack(1 + i).equals(this.inventory.getItemStack(1 + 3 + i)) &&
                        this.inventory.getItemStack(1 + i).equals(this.inventory.getItemStack(1 + 6 + i)))
                        return true;
                }
            }
            return false;
        }

        public void restart()
        {
            this.inventory.clear();
            this.finished = false;
        }

        public void aiDrop()
        {
            // 能赢直接下
            for(int i = 0; i < 9; i++)
            {
                if(this.inventory.getItemStack(1 + i).isEmpty())
                {
                    this.inventory.setItemStack(1 + i, AI);
                    if(checkWin())
                        return;
                    this.inventory.setItemStack(1 + i, ItemStack.EMPTY);
                }
            }
            for(int i = 0; i < 9; i++)
            {
                if(this.inventory.getItemStack(1 + i).isEmpty())
                {
                    this.inventory.setItemStack(1 + i, PLAYER);
                    if(checkWin())
                    {
                        this.inventory.setItemStack(1 + i, AI);
                        return;
                    }
                    this.inventory.setItemStack(1 + i, ItemStack.EMPTY);
                }
            }
            // 抢中心位
            if(this.inventory.getItemStack(1 + 4).isEmpty())
            {
                this.inventory.setItemStack(1 + 4, AI);
                return;
            }
            List<Integer> list = new ArrayList<>();
            for(int i = 0; i < 9; i++)
            {
                list.add(i);
            }
            Collections.shuffle(list);
            for(int i : list)
            {
                if(this.inventory.getItemStack(1 + i).isEmpty())
                {
                    this.inventory.setItemStack(1 + i, AI);
                    return;
                }
            }
        }

        public boolean isFull()
        {
            for(int i = 0; i < 9; i++)
            {
                if(this.inventory.getItemStack(1 + i).isEmpty())
                    return false;
            }
            return true;
        }

        @Override
        public void onAction(WindowUiWindow window, WindowAction action)
        {
            super.onAction(window, action);
            if(this.inventory.getItemStack(0).isEmpty() && (this.finished || this.isFull()))
            {
                this.restart();
                if(action.getIndex() >= 0 && action.getIndex() < this.inventory.size())
                    window.sendSlotUpdate(action.getPlayer(), action.getIndex());
            }
            else
            {
                if(!this.finished && (action.getType().equals(WindowActionType.CLICK) ||
                    action.getType().equals(WindowActionType.SHIFT_CLICK)) &&
                    action.getIndex() >= 1 && action.getIndex() < 10 &&
                    this.inventory.getItemStack(action.getIndex()).isEmpty())
                {
                    this.inventory.setItemStack(action.getIndex(), PLAYER);
                    if(checkWin())
                    {
                        this.finished = true;
                        this.inventory.setItemStack(
                            0, ((ItemStack) Demo.instance.config.get("game.tictactoe.reward")).copy());
                        action.getPlayer().closeInterface();
                        this.open(action.getPlayer());
                    }
                    else
                    {
                        aiDrop();
                        if(this.checkWin())
                            this.finished = true;
                    }
                }
            }
        }

        @Override
        public Text getTitle(EntityPlayer player)
        {
            return MinecraftI18n.resolveText(player.getLanguage(), "mzlibdemo.game.tictactoe.title");
        }
    }
}
