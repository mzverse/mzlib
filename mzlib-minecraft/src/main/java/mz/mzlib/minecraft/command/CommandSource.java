package mz.mzlib.minecraft.command;

import mz.mzlib.minecraft.VersionName;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.minecraft.entity.Entity;
import mz.mzlib.minecraft.entity.player.EntityPlayer;
import mz.mzlib.minecraft.text.Text;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.minecraft.wrapper.WrapMinecraftFieldAccessor;
import mz.mzlib.minecraft.wrapper.WrapMinecraftMethod;
import mz.mzlib.util.Option;
import mz.mzlib.util.wrapper.SpecificImpl;
import mz.mzlib.util.wrapper.WrapperFactory;
import mz.mzlib.util.wrapper.WrapperObject;

@WrapMinecraftClass({
    @VersionName(name = "net.minecraft.command.CommandSource", end = 1300),
    @VersionName(name = "net.minecraft.class_3915", begin = 1300, end = 1400),
    @VersionName(name = "net.minecraft.server.command.ServerCommandSource", begin = 1400)
})
public interface CommandSource extends WrapperObject
{
    WrapperFactory<CommandSource> FACTORY = WrapperFactory.of(CommandSource.class);

    boolean isSilent();

    @SpecificImpl("isSilent")
    @VersionRange(end = 1300)
    default boolean isSilentV_1300()
    {
        return false;
    }

    @SpecificImpl("isSilent")
    @VersionRange(begin = 1300)
    @WrapMinecraftFieldAccessor({
        @VersionName(name = "field_19283", end = 1400),
        @VersionName(name = "silent", begin = 1400)
    })
    boolean isSilentV1300();

    CommandOutput getOutput();

    @SpecificImpl("getOutput")
    @VersionRange(end = 1300)
    default CommandOutput getOutputV_1300()
    {
        return this.castTo(CommandOutput.FACTORY);
    }

    @SpecificImpl("getOutput")
    @VersionRange(begin = 1300)
    @WrapMinecraftFieldAccessor({
        @VersionName(name = "field_19276", end = 1400),
        @VersionName(name = "output", begin = 1400)
    })
    CommandOutput getOutputV1300();

    void sendMessage(Text message);

    @VersionRange(end = 1901)
    @SpecificImpl("sendMessage")
    default void sendMessageV_1901(Text message)
    {
        if(this.isSilent())
            return;
        this.getOutput().sendMessage(message);
    }

    @VersionRange(begin = 1901)
    @SpecificImpl("sendMessage")
    @WrapMinecraftMethod(@VersionName(name = "sendMessage"))
    void sendMessageV1901(Text message);

    @WrapMinecraftMethod({
        @VersionName(name = "getEntity", end = 1300),
        @VersionName(name = "method_17469", begin = 1300, end = 1400),
        @VersionName(name = "getEntity", begin = 1400)
    })
    Entity getEntity0();
    default Option<Entity> getEntity()
    {
        return Option.fromWrapper(this.getEntity0());
    }

    default Option<EntityPlayer> getPlayer()
    {
        for(Entity entity : this.getEntity())
        {
            return entity.tryCast(EntityPlayer.FACTORY);
        }
        return Option.none();
    }
}
