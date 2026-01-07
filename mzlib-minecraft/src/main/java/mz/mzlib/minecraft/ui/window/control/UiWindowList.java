package mz.mzlib.minecraft.ui.window.control;


import mz.mzlib.minecraft.entity.player.EntityPlayer;
import mz.mzlib.minecraft.i18n.MinecraftI18n;
import mz.mzlib.minecraft.item.Item;
import mz.mzlib.minecraft.item.ItemStack;
import mz.mzlib.minecraft.text.Text;
import mz.mzlib.minecraft.ui.window.UiWindowUtil;
import mz.mzlib.minecraft.window.WindowAction;
import mz.mzlib.minecraft.window.WindowActionType;
import mz.mzlib.util.*;
import mz.mzlib.util.proxy.ListProxy;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UiWindowList<T> extends UiWindowControlReadOnly
{
    List<T> list;
    Function<? super Entry<T>, ? extends ItemStack> iconGetter;
    Option<Consumer<Entry<T>>> viewer, remover, adder;

    boolean insertable, appendable;

    List<Consumer<UiWindowList<T>>> listenersPageChanged;

    public UiWindowList(Builder<T> builder)
    {
        super(builder.bounds);
        this.list = builder.list;
        this.iconGetter = builder.iconGetter;
        this.viewer = builder.viewer;
        this.remover = builder.remover;
        this.adder = builder.adder;
        this.setBackground(builder.background);
        this.insertable = builder.insertable;
        this.appendable = builder.appendable;
        this.listenersPageChanged = builder.listenersPageChanged;
    }

    @Override
    public void init()
    {
        this.setPage(0);
    }

    public List<T> getList()
    {
        return this.list;
    }
    public List<T> proxy()
    {
        return new ListProxy<>(
            this.getList(), FunctionInvertible.identity(),
            new ModifyMonitor.Simple(ThrowableRunnable.nothing(), this::invalidate)
        );
    }

    public List<Consumer<UiWindowList<T>>> getListenersPageChanged()
    {
        return this.listenersPageChanged;
    }
    public void listenPageChanged(Consumer<UiWindowList<T>> listener)
    {
        this.listenersPageChanged.add(listener);
    }

    int page;
    public int getPage()
    {
        return this.page;
    }
    public void setPage(int value)
    {
        this.page = value;
        this.invalidate();
        for(Consumer<UiWindowList<T>> listener : this.getListenersPageChanged())
        {
            listener.accept(this);
        }
    }

    public int getPageCount()
    {
        return 1 + (this.getList().size() + RuntimeUtil.castBooleanToByte(this.adder.isSome()) - 1) /
            (this.getSize().height * 9);
    }

    Option<Option<Integer>> point2index(Point point)
    {
        int i = this.getSize().width * this.getSize().height * this.page + point.y * this.getSize().width + point.x;
        if(i < 0 || i > this.list.size())
            return Option.none();
        if(i == this.list.size())
            return Option.some(Option.none());
        return Option.some(Option.some(i));
    }

    @Override
    public Option<ItemStack> getIcon(EntityPlayer player, Point point)
    {
        for(Option<Integer> indexOrNone : this.point2index(point))
        {
            for(int index : indexOrNone)
            {
                ItemStack icon = this.iconGetter.apply(new Entry<>(this, player, index)).clone();
                for(List<Text> lore : Item.LORE.revise(icon))
                {
                    lore.addAll(MinecraftI18n.resolveTexts(
                        player, "mzlib.ui.list.lore", MapBuilder.hashMap()
                            .put("view", this.viewer.isSome())
                            .put("remove", this.remover.isSome())
                            .put("insert", this.insertable && this.adder.isSome())
                            .get()
                    ));
                }
                return Option.some(icon);
            }
            if(this.appendable && this.adder.isSome())
                return Option.some(ItemStack.builder().fromId("nether_star")
                    .customName(MinecraftI18n.resolveText(player, "mzlib.ui.list.append")).build());
        }
        return super.getIcon(player, point);
    }
    @Override
    public void onAction(WindowAction action, Point point)
    {
        for(Option<Integer> indexOrNone : this.point2index(point))
        {
            for(int index : indexOrNone)
            {
                Option<Consumer<Entry<T>>> handler = Option.none();
                if(action.getType().equals(WindowActionType.CLICK) && action.getData() == 0)
                    handler = this.viewer;
                else if(action.getType().equals(WindowActionType.SHIFT_CLICK) && action.getData() == 0)
                    handler = this.remover;
                else if(action.getType().equals(WindowActionType.CLICK) && action.getData() == 1)
                {
                    if(this.insertable)
                        handler = this.adder;
                }
                for(Consumer<Entry<T>> h : handler)
                {
                    h.accept(new Entry<>(this, action.getPlayer(), index));
                }
                return;
            }
            if(this.appendable)
                for(Consumer<Entry<T>> adder : this.adder)
                {
                    adder.accept(new Entry<>(this, action.getPlayer(), this.list.size()));
                }
        }
    }

    public static <T> Builder<T> builder(List<T> list)
    {
        return new Builder<>(list);
    }
    @SuppressWarnings("UnusedReturnValue")
    public static class Builder<T>
    {
        Rectangle bounds = new Rectangle();
        List<T> list;
        Function<? super Entry<T>, ? extends ItemStack> iconGetter;
        Option<Consumer<Entry<T>>> viewer = Option.none(), remover = Option.none(), adder = Option.none();
        Function<EntityPlayer, ItemStack> background = player -> ItemStack.EMPTY;
        List<Consumer<UiWindowList<T>>> listenersPageChanged = new ArrayList<>();
        boolean insertable = true, appendable = true;
        public Builder(List<T> list)
        {
            this.list = list;
        }
        public Builder<T> bounds(Rectangle value)
        {
            this.bounds = value;
            return this;
        }
        public Builder<T> location(Point value)
        {
            this.bounds.setLocation(value);
            return this;
        }
        public Builder<T> size(Dimension value)
        {
            this.bounds.setSize(value);
            return this;
        }
        public Builder<T> viewer(Consumer<Entry<T>> value)
        {
            this.viewer = Option.some(value);
            return this;
        }
        public Builder<T> remover(Consumer<Entry<T>> value)
        {
            this.remover = Option.some(value);
            return this;
        }
        public Builder<T> remover()
        {
            return this.remover(entry ->
            {
                entry.getControl().getList().remove(entry.getIndex());
                entry.getControl().invalidate();
            });
        }
        public Builder<T> adder(Consumer<Entry<T>> value)
        {
            this.adder = Option.some(value);
            return this;
        }
        public Builder<T> adder(Supplier<T> value)
        {
            return this.adder(entry ->
            {
                entry.getControl().getList().add(value.get());
                entry.getControl().invalidate();
            });
        }
        public Builder<T> iconGetter(Function<? super Entry<T>, ? extends ItemStack> value)
        {
            this.iconGetter = value;
            return this;
        }
        public Builder<T> background(Function<EntityPlayer, ItemStack> background)
        {
            this.background = background;
            return this;
        }
        public Builder<T> listenerPageChanged(Consumer<UiWindowList<T>> value)
        {
            this.listenersPageChanged.add(value);
            return this;
        }
        public Builder<T> insertable(boolean value)
        {
            this.insertable = value;
            return this;
        }
        public Builder<T> appendable(boolean value)
        {
            this.appendable = value;
            return this;
        }
        public UiWindowList<T> build()
        {
            return new UiWindowList<>(this);
        }
    }

    public static <T> OverlappedBuilder<T> overlappedBuilder(List<T> list)
    {
        return new OverlappedBuilder<>(list);
    }
    public static class OverlappedBuilder<T>
    {
        Rectangle bounds = new Rectangle();
        Builder<T> controlBuilder;
        Function<EntityPlayer, ItemStack> background;
        Integer buttonNew = 7;
        Integer buttonPrev = 2, buttonNext = 6;
        boolean buttonPrevFirst = true, buttonNextLast = true;
        public OverlappedBuilder(List<T> list)
        {
            this.controlBuilder = UiWindowList.builder(list);
            this.background = it -> ItemStack.builder().stainedGlassPane().blue().emptyName().build();
        }
        public OverlappedBuilder<T> bounds(Rectangle value)
        {
            this.bounds = value;
            return this;
        }
        public OverlappedBuilder<T> location(Point value)
        {
            this.bounds.setLocation(value);
            return this;
        }
        public OverlappedBuilder<T> size(Dimension value)
        {
            this.bounds.setSize(value);
            return this;
        }
        public OverlappedBuilder<T> viewer(Consumer<Entry<T>> value)
        {
            this.controlBuilder.viewer(value);
            return this;
        }
        public OverlappedBuilder<T> remover(Consumer<Entry<T>> value)
        {
            this.controlBuilder.remover(value);
            return this;
        }
        public OverlappedBuilder<T> remover()
        {
            this.controlBuilder.remover();
            return this;
        }
        public OverlappedBuilder<T> adder(Consumer<Entry<T>> value)
        {
            this.controlBuilder.adder(value);
            return this;
        }
        public OverlappedBuilder<T> adder(Supplier<T> value)
        {
            this.controlBuilder.adder(value);
            return this;
        }
        public OverlappedBuilder<T> iconGetter(Function<? super Entry<T>, ? extends ItemStack> value)
        {
            this.controlBuilder.iconGetter(value);
            return this;
        }
        public OverlappedBuilder<T> listenerPageChanged(Consumer<UiWindowList<T>> value)
        {
            this.controlBuilder.listenerPageChanged(value);
            return this;
        }
        public OverlappedBuilder<T> background(Function<EntityPlayer, ItemStack> background)
        {
            this.background = background;
            return this;
        }
        public OverlappedBuilder<T> buttonNew(Integer value)
        {
            this.buttonNew = value;
            return this;
        }
        public OverlappedBuilder<T> buttonPrev(Integer value)
        {
            this.buttonPrev = value;
            return this;
        }
        public OverlappedBuilder<T> buttonNext(Integer value)
        {
            this.buttonNext = value;
            return this;
        }
        public OverlappedBuilder<T> buttonPrevFirst(boolean value)
        {
            this.buttonPrevFirst = value;
            return this;
        }
        public OverlappedBuilder<T> buttonNextLast(boolean value)
        {
            this.buttonNextLast = value;
            return this;
        }
        public Overlapped<T> build()
        {
            return new Overlapped<>(this);
        }
    }
    public static class Overlapped<T> extends UiWindowControlReadOnly
    {
        public UiWindowButton buttonBack, buttonNew, buttonPrev, buttonNext;
        public UiWindowList<T> controlList;
        public Overlapped(OverlappedBuilder<T> builder)
        {
            super(builder.bounds);
            this.setBackground(builder.background);
            this.addChild(this.buttonBack = UiWindowUtil.buttonBack(new Point(0, 0)));
            builder.controlBuilder.bounds(new Rectangle(0, 1, builder.bounds.width, builder.bounds.height - 2));
            if(builder.buttonNew != null)
            {
                for(Consumer<Entry<T>> adder : builder.controlBuilder.adder)
                {
                    this.addChild(this.buttonNew = new UiWindowButton(
                        new Point(builder.buttonNew, 0),
                        player -> ItemStack.builder().fromId("nether_star")
                            .customName(MinecraftI18n.resolveText(player, "mzlib.ui.list.new")).build(),
                        action -> adder.accept(new Entry<>(this.controlList, action.getPlayer(), this.controlList.getList().size()))
                    ));
                }
            }
            if(builder.buttonPrev != null)
            {
                this.addChild(this.buttonPrev = new UiWindowButton(
                    new Point(builder.buttonPrev, builder.bounds.height - 1),
                    player -> ItemStack.builder().playerHead().texturesUrl(
                            "https://textures.minecraft.net/texture/69ea1d86247f4af351ed1866bca6a3040a06c68177c78e42316a1098e60fb7d3")
                        .customName(MinecraftI18n.resolveText(player, "mzlib.ui.list.prev")).build(),
                    action -> controlList.setPage(Math.max(0, controlList.getPage() - 1))
                ));
                if(!builder.buttonPrevFirst)
                    builder.controlBuilder.listenerPageChanged(
                        control -> this.buttonPrev.setVisible(control.getPage() > 0));
            }
            if(builder.buttonNext != null)
            {
                this.addChild(this.buttonNext = new UiWindowButton(
                    new Point(builder.buttonNext, builder.bounds.height - 1),
                    player -> ItemStack.builder().playerHead().texturesUrl(
                            "https://textures.minecraft.net/texture/8271a47104495e357c3e8e80f511a9f102b0700ca9b88e88b795d33ff20105eb")
                        .customName(MinecraftI18n.resolveText(player, "mzlib.ui.list.next")).build(),
                    action -> controlList.setPage(Math.min(controlList.getPageCount() - 1, controlList.getPage() + 1))
                ));
                if(!builder.buttonNextLast)
                    builder.controlBuilder.listenerPageChanged(
                        control -> this.buttonNext.setVisible(control.getPage() < control.getPageCount() - 1));
            }
            this.addChild(this.controlList = builder.controlBuilder.build());
        }
    }

    public static class Entry<T>
    {
        UiWindowList<T> control;
        EntityPlayer player;
        int index;
        Entry(UiWindowList<T> control, EntityPlayer player, int index)
        {
            this.control = control;
            this.player = player;
            this.index = index;
        }

        public UiWindowList<T> getControl()
        {
            return this.control;
        }
        public EntityPlayer getPlayer()
        {
            return this.player;
        }
        public int getIndex()
        {
            return this.index;
        }
        public T getElement()
        {
            return this.getControl().getList().get(this.getIndex());
        }
    }
}
