package mz.mzlib.minecraft.command;

import mz.mzlib.minecraft.VersionName;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.minecraft.text.Text;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.minecraft.wrapper.WrapMinecraftMethod;
import mz.mzlib.util.wrapper.SpecificImpl;
import mz.mzlib.util.wrapper.WrapperCreator;
import mz.mzlib.util.wrapper.WrapperFactory;
import mz.mzlib.util.wrapper.WrapperObject;

import java.util.UUID;

@WrapMinecraftClass({
    @VersionName(name = "net.minecraft.command.CommandSource", end = 1300),
    @VersionName(name = "net.minecraft.class_3893", begin = 1300, end = 1400),
    @VersionName(name = "net.minecraft.server.command.CommandOutput", begin = 1400)
})
public interface CommandOutput extends WrapperObject
{
    WrapperFactory<CommandOutput> FACTORY = WrapperFactory.of(CommandOutput.class);
    @Deprecated
    @WrapperCreator
    static CommandOutput create(Object wrapped)
    {
        return WrapperObject.create(CommandOutput.class, wrapped);
    }

    void sendMessage(Text msg);

    @SpecificImpl("sendMessage")
    @VersionRange(end = 1600)
    @VersionRange(begin = 1900)
    @WrapMinecraftMethod({
        @VersionName(name = "sendMessage", end = 1300),
        @VersionName(name = "method_5505", begin = 1300, end = 1400),
        @VersionName(name = "sendMessage", begin = 1400)
    })
    void sendMessageV_1600__1900(Text msg);

    @VersionRange(begin = 1600, end = 1900)
    @WrapMinecraftMethod(@VersionName(name = "sendSystemMessage"))
    void sendMessageV1600_1900(Text msg, UUID sender);

    @SpecificImpl("sendMessage")
    @VersionRange(begin = 1600, end = 1900)
    default void sendMessageV1600_1900(Text msg)
    {
        sendMessageV1600_1900(msg, new UUID(0L, 0L));
    }
}
