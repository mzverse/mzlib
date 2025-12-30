package mz.mzlib.minecraft.item;

import mz.mzlib.data.DataKey;
import mz.mzlib.minecraft.Identifier;
import mz.mzlib.minecraft.authlib.GameProfile;
import mz.mzlib.minecraft.text.Text;
import mz.mzlib.util.Option;
import mz.mzlib.util.RuntimeUtil;
import mz.mzlib.util.TypedMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ItemStackBuilderImpl implements ItemStack.Builder
{
    ItemStack from;
    Item item;
    Integer count;
    TypedMap<DataKey<ItemStack, ?, ?>> data = TypedMap.of();

    public ItemStackBuilderImpl()
    {
    }
    public ItemStackBuilderImpl(ItemStack from)
    {
        this.from = from;
    }

    @Override
    public ItemStackBuilderImpl item(Item value)
    {
        this.item = value;
        return this;
    }
    @Override
    public ItemStackBuilderImpl fromId(Identifier id)
    {
        return this.item(Item.fromId(id));
    }
    @Override
    public ItemStackBuilderImpl fromId(String id)
    {
        return this.item(Item.fromId(id));
    }

    @Override
    public ItemStackBuilderImpl count(int value)
    {
        this.count = value;
        return this;
    }

    @Override
    public ItemStackBuilderImpl damageV_1300(int value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemStackBuilderImpl fromId(String idV_1300, int damageV_1300, String idV1300)
    {
        return this.fromId(idV1300);
    }

    @Override
    public <T> ItemStackBuilderImpl data(DataKey<ItemStack, T, ?> key, T value)
    {
        this.data.put(key, value);
        return this;
    }
    @Override
    public StepLore lore()
    {
        return new StepLore(this);
    }

    @Override
    public StepPlayerHead playerHead()
    {
        return new StepPlayerHead(this);
    }

    @Override
    public StepColor colored(String idV_1300, String baseIdV1300)
    {
        return new StepColor(this, idV_1300, baseIdV1300);
    }
    @Override
    public StepColor colored(String baseId)
    {
        return new StepColor(this, baseId);
    }
    public StepColor coloredSpecific(String baseId)
    {
        return new StepColorSpecific(this, baseId);
    }
    @Override
    public StepColor dye()
    {
        return this.colored("dye").reversed();
    }
    @Override
    public StepColor wool()
    {
        return this.colored("wool");
    }
    @Override
    public StepColor stainedGlass()
    {
        return this.colored("stained_glass");
    }
    @Override
    public StepColor stainedGlassPane()
    {
        return this.colored("stained_glass_pane");
    }
    @Override
    public StepColor terracotta()
    {
        return this.colored("stained_hardened_clay", "terracotta");
    }
    @Override
    public StepColor carpet()
    {
        return this.colored("carpet");
    }
    @Override
    public StepColor banner()
    {
        return this.colored("banner").reversed();
    }
    @Override
    public StepColor shulkerBoxV1100()
    {
        return this.coloredSpecific("shulker_box");
    }
    @Override
    public StepColor bedV1200()
    {
        return this.colored("bed");
    }
    @Override
    public StepColor concreteV1200()
    {
        return this.colored("concrete");
    }
    @Override
    public StepColor concretePowderV1200()
    {
        return this.colored("concrete_powder");
    }
    @Override
    public StepColor glazedTerracottaV1200()
    {
        return this.coloredSpecific("glazed_terracotta");
    }

    @Override
    public ItemStack build()
    {
        ItemStack result;
        if(this.from != null)
            result = this.from.clone(this.item != null ? this.item : this.from.getItem()).copy();
        else
        {
            if(this.item == null)
                throw new IllegalStateException("item is not set");
            result = ItemStack.newInstance(this.item);
        }
        if(this.count != null)
            result.setCount(this.count);
        for(Map.Entry<DataKey<ItemStack, ?,?>, ?> entry : this.data.asMap().entrySet())
        {
            entry.getKey().set(result, RuntimeUtil.cast(entry.getValue()));
        }
        return result;
    }

    static class V_1300 extends ItemStackBuilderImpl
    {
        Integer damage;

        public V_1300()
        {
            super();
        }
        public V_1300(ItemStack from)
        {
            super(from);
        }

        @Override
        public ItemStackBuilderImpl damageV_1300(int value)
        {
            this.damage = value;
            return this;
        }

        @Override
        public ItemStackBuilderImpl fromId(String idV_1300, int damageV_1300, String idV1300)
        {
            return this.fromId(idV_1300).damageV_1300(damageV_1300);
        }

        @Override
        public ItemStack build()
        {
            ItemStack result = super.build();
            if(this.damage != null)
                result.setDamageV_1300(this.damage);
            return result;
        }
    }

    static class StepLore implements ItemStack.Builder.StepLore
    {
        ItemStackBuilderImpl base;
        List<Text> lines;
        public StepLore(ItemStackBuilderImpl base)
        {
            this.base = base;
            for(Option<List<Text>> lore : base.data.get(Item.LORE))
            {
                this.lines = lore.map(ArrayList::new).unwrapOrGet(ArrayList::new);
                return;
            }
            if(base.from != null)
                for(List<Text> lore : Item.LORE.get(base.from))
                {
                    this.lines = new ArrayList<>(lore);
                    return;
                }
            lines = new ArrayList<>();
        }
        @Override
        public ItemStack.Builder.StepLore line(Text value)
        {
            this.lines.add(value);
            return this;
        }
        @Override
        public ItemStack.Builder.StepLore lines(List<Text> values)
        {
            this.lines.addAll(values);
            return this;
        }
        @Override
        public ItemStack.Builder.StepLore clear()
        {
            this.lines.clear();
            return this;
        }
        @Override
        public ItemStackBuilderImpl finish()
        {
            return this.base.data(Item.LORE, Option.some(this.lines));
        }
    }

    static class StepPlayerHead implements ItemStack.Builder.StepPlayerHead
    {
        ItemStackBuilderImpl base;
        StepPlayerHead(ItemStackBuilderImpl base)
        {
            this.base = base.fromId("skull", 3, "player_head");
        }
        @Override
        public ItemStackBuilderImpl gameProfile(GameProfile.Description description)
        {
            return this.base.data(ItemPlayerHead.OWNER, Option.some(description));
        }
    }

    static class StepColor implements ItemStack.Builder.StepColor
    {
        ItemStackBuilderImpl base;
        String idV_1300;
        String baseIdV1300;

        Map<String, Integer> damages = DAMAGES_DEFAULT;

        static Map<String, Integer> DAMAGES_DEFAULT;
        static Map<String, Integer> DAMAGES_REVERSED;
        static
        {
            DAMAGES_DEFAULT = new HashMap<>();
            DAMAGES_DEFAULT.put("white", 0);
            DAMAGES_DEFAULT.put("orange", 1);
            DAMAGES_DEFAULT.put("magenta", 2);
            DAMAGES_DEFAULT.put("light_blue", 3);
            DAMAGES_DEFAULT.put("yellow", 4);
            DAMAGES_DEFAULT.put("lime", 5);
            DAMAGES_DEFAULT.put("pink", 6);
            DAMAGES_DEFAULT.put("gray", 7);
            DAMAGES_DEFAULT.put("light_gray", 8);
            DAMAGES_DEFAULT.put("cyan", 9);
            DAMAGES_DEFAULT.put("purple", 10);
            DAMAGES_DEFAULT.put("blue", 11);
            DAMAGES_DEFAULT.put("brown", 12);
            DAMAGES_DEFAULT.put("green", 13);
            DAMAGES_DEFAULT.put("red", 14);
            DAMAGES_DEFAULT.put("black", 15);

            DAMAGES_REVERSED = new HashMap<>(DAMAGES_DEFAULT);
            DAMAGES_REVERSED.replaceAll((k, v) -> 15 - v);
        }

        public StepColor(ItemStackBuilderImpl base, String idV_1300, String baseIdV1300)
        {
            this.base = base;
            this.idV_1300 = idV_1300;
            this.baseIdV1300 = baseIdV1300;
        }
        public StepColor(ItemStackBuilderImpl base, String baseId)
        {
            this(base, baseId, baseId);
        }

        @Override
        public StepColor reversed()
        {
            StepColor result = new StepColor(this.base, this.idV_1300, this.baseIdV1300);
            result.damages = DAMAGES_REVERSED;
            return result;
        }

        @Override
        public ItemStackBuilderImpl color(String value)
        {
            return this.base.fromId(this.idV_1300, this.damages.get(value), value + "_" + this.baseIdV1300);
        }
    }

    static class StepColorSpecific extends StepColor
    {
        public StepColorSpecific(ItemStackBuilderImpl base, String baseId)
        {
            super(base, baseId);
        }

        @Override
        public ItemStackBuilderImpl color(String value)
        {
            String valueV_1300;
            if("light_gray".equals(value))
                valueV_1300 = "silver";
            else
                valueV_1300 = value;
            return this.base.fromId(valueV_1300 + "_" + this.baseIdV1300, 0, value + "_" + this.baseIdV1300);
        }
    }
}
