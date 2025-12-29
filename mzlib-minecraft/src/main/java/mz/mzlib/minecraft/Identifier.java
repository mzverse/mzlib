package mz.mzlib.minecraft;

import jakarta.annotation.Nonnull;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.minecraft.wrapper.WrapMinecraftFieldAccessor;
import mz.mzlib.util.wrapper.SpecificImpl;
import mz.mzlib.util.wrapper.WrapConstructor;
import mz.mzlib.util.wrapper.WrapperFactory;
import mz.mzlib.util.wrapper.WrapperObject;

@WrapMinecraftClass(@VersionName(name = "net.minecraft.util.Identifier"))
public interface Identifier extends WrapperObject, Comparable<Identifier>
{
    WrapperFactory<Identifier> FACTORY = WrapperFactory.of(Identifier.class);

    @WrapMinecraftFieldAccessor(@VersionName(name = "namespace"))
    String getNamespace();

    @WrapMinecraftFieldAccessor(@VersionName(name = "path"))
    String getName();

    static Identifier of(String namespace, String name)
    {
        return FACTORY.getStatic().static$of(namespace, name);
    }
    static Identifier of(String str)
    {
        String[] result = str.split(":", 2);
        if(result.length == 2)
            return of(result[0], result[1]);
        else
            return ofMinecraft(str);
    }
    static Identifier ofMinecraft(String name)
    {
        return of("minecraft", name);
    }

    default boolean isMinecraft()
    {
        return "minecraft".equals(this.getNamespace());
    }

    default String join(char delimiter)
    {
        return this.getNamespace() + delimiter + this.getName();
    }

    @Override
    default int compareTo(@Nonnull Identifier o)
    {
        int result = Boolean.compare(o.isMinecraft(), this.isMinecraft());
        if(result != 0)
            return result;
        result = this.getNamespace().compareTo(o.getNamespace());
        if(result != 0)
            return result;
        return this.getName().compareTo(o.getName());
    }

    Identifier static$of(String namespace, String name);
    @SpecificImpl("static$of")
    @VersionRange(end = 900)
    default Identifier static$ofV_900(String namespace, String name)
    {
        Identifier result = static$ofV_900("");
        result.setNamespace(namespace);
        result.setName(name);
        return result;
    }
    @VersionRange(end = 900)
    @WrapConstructor
    Identifier static$ofV_900(String name);
    @SpecificImpl("static$of")
    @VersionRange(begin = 900)
    @WrapConstructor
    Identifier static$ofV900(String namespace, String name);


    /**
     * @see #of(String)
     */
    @Deprecated
    static Identifier newInstance(String str)
    {
        return of(str);
    }
    /**
     * @see #of(String, String)
     */
    @Deprecated
    static Identifier newInstance(String namespace, String name)
    {
        return of(namespace, name);
    }
    /**
     * @see #ofMinecraft(String)
     */
    @Deprecated
    static Identifier minecraft(String name)
    {
        return ofMinecraft(name);
    }

    @Deprecated
    @WrapMinecraftFieldAccessor(@VersionName(name = "namespace"))
    @SuppressWarnings("DeprecatedIsStillUsed")
    void setNamespace(String value);
    @Deprecated
    @WrapMinecraftFieldAccessor(@VersionName(name = "path"))
    void setName(String value);
}
