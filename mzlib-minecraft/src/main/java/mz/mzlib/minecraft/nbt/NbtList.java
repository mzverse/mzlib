package mz.mzlib.minecraft.nbt;

import mz.mzlib.minecraft.VersionName;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.minecraft.wrapper.WrapMinecraftFieldAccessor;
import mz.mzlib.util.AutoCompletable;
import mz.mzlib.util.FunctionInvertible;
import mz.mzlib.util.proxy.ListProxy;
import mz.mzlib.util.wrapper.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@WrapMinecraftClass({
    @VersionName(end = 1400, name = "net.minecraft.nbt.NbtList"),
    @VersionName(begin = 1400, end = 1605, name = "net.minecraft.nbt.ListTag"),
    @VersionName(begin = 1605, name = "net.minecraft.nbt.NbtList")
})
public interface NbtList extends NbtElement
{
    WrapperFactory<NbtList> FACTORY = WrapperFactory.of(NbtList.class);
    @Deprecated
    @WrapperCreator
    static NbtList create(Object wrapped)
    {
        return WrapperObject.create(NbtList.class, wrapped);
    }

    @WrapConstructor
    NbtList static$newInstance();

    static NbtList newInstance()
    {
        return FACTORY.getStatic().static$newInstance();
    }

    static NbtList newInstance(NbtElement... elements)
    {
        return newInstance(Arrays.asList(elements));
    }
    static NbtList newInstance(List<? extends NbtElement> elements)
    {
        return newInstance0(elements.stream().map(NbtElement::getWrapped).collect(Collectors.toList()));
    }
    static NbtList newInstance0(List<Object> elements0)
    {
        return FACTORY.getStatic().static$newInstance0(elements0);
    }

    @WrapMinecraftFieldAccessor(@VersionName(name = "value"))
    List<Object> getValue0();

    @WrapMinecraftFieldAccessor(@VersionName(name = "value"))
    void setValue0(List<Object> value);

    default List<NbtElement> asList()
    {
        return this.asList(NbtElement.FACTORY);
    }

    default <T extends NbtElement> List<T> asList(WrapperFactory<T> factory)
    {
        return new ListProxy<>(this.getValue0(), FunctionInvertible.wrapper(factory));
    }
    @Deprecated
    default <T extends NbtElement> List<T> asList(Function<Object, T> creator)
    {
        return this.asList(new WrapperFactory<>(creator));
    }

    default int size()
    {
        return this.getValue0().size();
    }

    @VersionRange(end = 2105)
    @WrapMinecraftFieldAccessor(@VersionName(name = "type"))
    byte getElementTypeIdV_2105();

    @VersionRange(end = 2105)
    @WrapMinecraftFieldAccessor(@VersionName(name = "type"))
    void setElementTypeIdV_2105(byte value);

    default NbtElement get(int index)
    {
        return NbtElement.FACTORY.create(this.getValue0().get(index));
    }

    default <T extends NbtElement> T get(int index, WrapperFactory<T> factory)
    {
        return this.get(index).castTo(factory);
    }
    @Deprecated
    default <T extends NbtElement> T get(int index, Function<Object, T> creator)
    {
        return this.get(index, new WrapperFactory<>(creator));
    }

    default void set(int index, NbtElement value)
    {
        this.getValue0().set(index, value.getWrapped());
    }

    void add(NbtElement value);
    @VersionRange(end = 2105)
    @SpecificImpl("add")
    default void addV_2105(NbtElement value)
    {
        this.setElementTypeIdV_2105(value.getTypeId());
        this.addV2105(value);
    }
    @VersionRange(begin = 2105)
    @SpecificImpl("add")
    default void addV2105(NbtElement value)
    {
        this.getValue0().add(value.getWrapped());
    }

    default NbtCompound getNbtCompound(int index)
    {
        return this.get(index).castTo(NbtCompound.FACTORY);
    }

    /**
     * @deprecated typo
     */
    @Deprecated
    default NbtCompound getNBTCompound(int index)
    {
        return this.getNbtCompound(index);
    }

    default byte getByte(int index)
    {
        return this.get(index).castTo(NbtByte.FACTORY).getValue();
    }

    default int getInt(int index)
    {
        return this.get(index).castTo(NbtInt.FACTORY).getValue();
    }

    default long getLong(int index)
    {
        return this.get(index).castTo(NbtLong.FACTORY).getValue();
    }

    default float getFloat(int index)
    {
        return this.get(index).castTo(NbtFloat.FACTORY).getValue();
    }

    default double getDouble(int index)
    {
        return this.get(index).castTo(NbtDouble.FACTORY).getValue();
    }

    default String getString(int index)
    {
        return this.get(index).castTo(NbtString.FACTORY).getValue();
    }

    default NbtList getNbtList(int index)
    {
        return this.get(index).castTo(NbtList.FACTORY);
    }
    /**
     * @deprecated typo
     */
    @Deprecated
    default NbtList getNBTList(int index)
    {
        return this.getNbtList(index);
    }

    default NbtByteArray getByteArray(int index)
    {
        return this.get(index).castTo(NbtByteArray.FACTORY);
    }

    default NbtIntArray getIntArray(int index)
    {
        return this.get(index).castTo(NbtIntArray.FACTORY);
    }

    @VersionRange(begin = 1200)
    default NbtLongArrayV1200 getLongArrayV1200(int index)
    {
        return this.get(index).castTo(NbtLongArrayV1200.FACTORY);
    }

    @Override
    default NbtList copy()
    {
        return ((NbtList) NbtElement.super.copy());
    }

    default AutoCompletable<NbtCompound, ?> reviseNbtCompound(int index)
    {
        return AutoCompletable.of(() -> this.getNbtCompound(index).clone(), child -> this.set(index, child));
    }
    default AutoCompletable<NbtList, ?> reviseNbtList(int index)
    {
        return AutoCompletable.of(() -> this.getNbtList(index).clone(), child -> this.set(index, child));
    }

    @Override
    default NbtList clone()
    {
        NbtList result = newInstance();
        for(NbtElement i : this.asList())
        {
            result.add(i);
        }
        return result;
    }


    NbtList static$newInstance0(List<Object> elements0);
    @SpecificImpl("static$newInstance0")
    @VersionRange(end = 2105)
    default NbtList static$newInstance0V_2105(List<Object> elements0)
    {
        NbtList result = newInstance();
        result.setValue0(elements0);
        if(!elements0.isEmpty())
            result.setElementTypeIdV_2105(NbtElement.FACTORY.create(elements0.get(0)).getTypeId());
        return result;
    }
    @SpecificImpl("static$newInstance0")
    @VersionRange(begin = 2105)
    default NbtList static$newInstance0V2105(List<Object> elements0)
    {
        NbtList result = newInstance();
        result.setValue0(elements0);
        return result;
    }
}
