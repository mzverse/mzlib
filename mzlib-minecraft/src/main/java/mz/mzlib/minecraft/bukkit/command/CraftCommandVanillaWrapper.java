package mz.mzlib.minecraft.bukkit.command;

import moe.karla.usf.unsafe.Unsafe;
import mz.mzlib.minecraft.MinecraftPlatform;
import mz.mzlib.minecraft.VersionName;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.minecraft.bukkit.wrapper.WrapCraftbukkitClass;
import mz.mzlib.minecraft.command.CommandSource;
import mz.mzlib.util.RuntimeUtil;
import mz.mzlib.util.wrapper.*;
import org.bukkit.command.CommandSender;

@MinecraftPlatform.Enabled(MinecraftPlatform.Tag.BUKKIT)
@WrapCraftbukkitClass(@VersionName(name = "OBC.command.VanillaCommandWrapper"))
public interface CraftCommandVanillaWrapper extends WrapperObject
{
    WrapperFactory<CraftCommandVanillaWrapper> FACTORY = WrapperFactory.of(CraftCommandVanillaWrapper.class);
    @Deprecated
    @WrapperCreator
    static CraftCommandVanillaWrapper create(Object wrapped)
    {
        return WrapperObject.create(CraftCommandVanillaWrapper.class, wrapped);
    }

    CraftCommandVanillaWrapper unsafe = RuntimeUtil.sneakilyRun(
        () -> create(Unsafe.getUnsafe().allocateInstance(FACTORY.getStatic().static$getWrappedClass())));

    static CommandSource toCommandSource(CommandSender object)
    {
        return FACTORY.getStatic().static$toCommandSource(object);
    }

    CommandSource static$toCommandSource(CommandSender object);

    @VersionRange(end = 1300)
    @WrapMethod("getListener")
    CommandSource toCommandSourceV_1300(CommandSender object);

    @SpecificImpl("static$toCommandSource")
    @VersionRange(end = 1300)
    default CommandSource static$toCommandSourceV_1300(CommandSender object)
    {
        return unsafe.toCommandSourceV_1300(object);
    }

    @SpecificImpl("static$toCommandSource")
    @VersionRange(begin = 1300)
    @WrapMethod("getListener")
    CommandSource static$toCommandSourceV1300(CommandSender object);
}
