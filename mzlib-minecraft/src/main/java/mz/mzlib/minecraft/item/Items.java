package mz.mzlib.minecraft.item;

import mz.mzlib.minecraft.Identifier;
import mz.mzlib.minecraft.MinecraftPlatform;

public interface Items
{
    Item AIR_V1100 = MinecraftPlatform.instance.getVersion() < 1100 ? null : Item.fromId(Identifier.ofMinecraft("air"));

    Item BARRIER = Item.fromId("barrier");
}
