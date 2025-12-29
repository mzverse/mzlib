package mz.mzlib.minecraft.nbt;

import mz.mzlib.minecraft.MinecraftPlatform;
import mz.mzlib.minecraft.VersionName;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.minecraft.wrapper.WrapMinecraftFieldAccessor;
import mz.mzlib.minecraft.wrapper.WrapMinecraftMethod;
import mz.mzlib.module.MzModule;
import mz.mzlib.tester.SimpleTester;
import mz.mzlib.tester.TesterContext;
import mz.mzlib.util.*;
import mz.mzlib.util.proxy.MapProxy;
import mz.mzlib.util.wrapper.*;

import java.io.DataInput;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

@WrapMinecraftClass({
    @VersionName(name = "net.minecraft.nbt.NbtCompound", end = 1400),
    @VersionName(name = "net.minecraft.nbt.CompoundTag", begin = 1400, end = 1605),
    @VersionName(name = "net.minecraft.nbt.NbtCompound", begin = 1605)
})
public interface NbtCompound extends NbtElement
{
    WrapperFactory<NbtCompound> FACTORY = WrapperFactory.of(NbtCompound.class);
    @Deprecated
    @WrapperCreator
    static NbtCompound create(Object wrapped)
    {
        return WrapperObject.create(NbtCompound.class, wrapped);
    }

    int TYPE_ID = newInstance().getTypeId();
    NbtElementTypeV1500 TYPE_V1500 =
        MinecraftPlatform.instance.getVersion() < 1500 ? null : newInstance().getTypeV1500();

    static <T> Map<NbtCompound, T> cache()
    {
        return new MapProxy<>(new WeakHashMap<>(), FunctionInvertible.wrapper(FACTORY), FunctionInvertible.identity());
    }

    @Deprecated
    static NbtCompound load(DataInput input)
    {
        return FACTORY.getStatic().static$load(input);
    }

    NbtCompound static$load(DataInput input);

    @SpecificImpl("static$load")
    @VersionRange(end = 1500)
    default NbtCompound static$loadV_1500(DataInput input)
    {
        NbtCompound result = newInstance();
        result.loadV_1500(input, 0, NbtSizeTracker.newInstance());
        return result;
    }

    @SpecificImpl("static$load")
    @VersionRange(begin = 1500)
    default NbtCompound static$loadV1500(DataInput input)
    {
        return TYPE_V1500.load(input, NbtSizeTracker.newInstance()).castTo(NbtCompound.FACTORY);
    }

    static NbtCompound parse(String str)
    {
        return NbtScanner.parseCompound(str);
    }

    @WrapConstructor
    NbtCompound static$newInstance();

    static NbtCompound newInstance()
    {
        return FACTORY.getStatic().static$newInstance();
    }

    @WrapMinecraftMethod(@VersionName(name = "get"))
    NbtElement get0(String key);

    default Option<NbtElement> get(String key)
    {
        return Option.fromWrapper(this.get0(key));
    }

    default boolean containsKey(String key)
    {
        return this.get(key).isSome();
    }

    default <T extends NbtElement> Option<T> get(String key, WrapperFactory<T> factory)
    {
        return this.get(key).filter(factory::isInstance).map(ThrowableFunction.wrapperCast(factory));
    }
    @Deprecated
    default <T extends NbtElement> Option<T> get(String key, Function<Object, T> creator)
    {
        return this.get(key, new WrapperFactory<>(creator));
    }

    @WrapMinecraftMethod(@VersionName(name = "put"))
    void put(String key, NbtElement value);

    default void put(String key, byte value)
    {
        this.put(key, NbtByte.newInstance(value));
    }
    default void put(String key, boolean value)
    {
        this.put(key, NbtByte.newInstance(value));
    }
    default void put(String key, short value)
    {
        this.put(key, NbtShort.newInstance(value));
    }
    default void put(String key, int value)
    {
        this.put(key, NbtInt.newInstance(value));
    }
    default void put(String key, long value)
    {
        this.put(key, NbtLong.newInstance(value));
    }
    default void put(String key, float value)
    {
        this.put(key, NbtFloat.newInstance(value));
    }
    default void put(String key, double value)
    {
        this.put(key, NbtDouble.newInstance(value));
    }
    default void put(String key, String value)
    {
        this.put(key, NbtString.newInstance(value));
    }

    default void remove(String key)
    {
        this.asMap0().remove(key);
    }

    default <T extends NbtElement> T getOr(String key, WrapperFactory<T> factory, Supplier<T> newer)
    {
        for(T result : this.get(key, factory))
        {
            return result;
        }
        T result = newer.get();
        this.put(key, result);
        return result;
    }
    /**
     * @deprecated typo
     */
    @Deprecated
    default <T extends NbtElement> T getOrPut(String key, WrapperFactory<T> factory, Supplier<T> newer)
    {
        return this.getOr(key, factory, newer);
    }
    @Deprecated
    default <T extends NbtElement> T getOrPut(String key, Function<Object, T> creator, Supplier<T> newer)
    {
        return this.getOr(key, new WrapperFactory<>(creator), newer);
    }

    default NbtCompound getNbtCompoundOrNew(String key)
    {
        return this.getOr(key, NbtCompound.FACTORY, NbtCompound::newInstance);
    }

    /**
     * @deprecated typo
     */
    @Deprecated
    default NbtCompound getOrPutNewCompound(String key)
    {
        return this.getNbtCompoundOrNew(key);
    }

    default Option<NbtCompound> getNbtCompound(String key)
    {
        return this.get(key, NbtCompound.FACTORY);
    }
    /**
     * @deprecated typo
     */
    @Deprecated
    default Option<NbtCompound> getNBTCompound(String key)
    {
        return this.getNbtCompound(key);
    }

    default Option<Byte> getByte(String key)
    {
        return this.get(key, NbtByte.FACTORY).map(NbtByte::getValue);
    }

    default Option<Boolean> getBoolean(String key)
    {
        return this.getByte(key).map(RuntimeUtil::castByteToBoolean);
    }

    default Option<Integer> getInt(String key)
    {
        return this.get(key, NbtInt.FACTORY).map(NbtInt::getValue);
    }

    default Option<Long> getLong(String key)
    {
        return this.get(key, NbtLong.FACTORY).map(NbtLong::getValue);
    }

    default Option<Float> getFloat(String key)
    {
        return this.get(key, NbtFloat.FACTORY).map(NbtFloat::getValue);
    }

    default Option<Double> getDouble(String key)
    {
        return this.get(key, NbtDouble.FACTORY).map(NbtDouble::getValue);
    }

    default Option<String> getString(String key)
    {
        return this.get(key, NbtString.FACTORY).map(NbtString::getValue);
    }

    default Option<NbtList> getNbtList(String key)
    {
        return this.get(key, NbtList.FACTORY);
    }
    /**
     * @deprecated typo
     */
    @Deprecated
    default Option<NbtList> getNBTList(String key)
    {
        return this.getNbtList(key);
    }

    default Option<NbtByteArray> getByteArray(String key)
    {
        return this.get(key, NbtByteArray.FACTORY);
    }

    default Option<NbtIntArray> getIntArray(String key)
    {
        return this.get(key, NbtIntArray.FACTORY);
    }

    @VersionRange(begin = 1200)
    default Option<NbtLongArrayV1200> getLongArrayV1200(String key)
    {
        return this.get(key, NbtLongArrayV1200.FACTORY);
    }

    @Override
    default NbtCompound copy()
    {
        return (NbtCompound) NbtElement.super.copy();
    }

    default Editor<NbtCompound> reviseNbtCompound(String key)
    {
        return this.reviseNbtCompoundOr(key, () -> RuntimeUtil.valueThrow(new NoSuchElementException()));
    }
    default Editor<NbtCompound> reviseNbtCompoundOrNew(String key)
    {
        return this.reviseNbtCompoundOr(key, NbtCompound::newInstance);
    }
    default Editor<NbtCompound> reviseNbtCompoundOr(String key, Supplier<NbtCompound> supplier)
    {
        return Editor.of(
            () -> this.getNbtCompound(key).map(NbtCompound::clone).unwrapOrGet(supplier),
            child -> this.put(key, child)
        );
    }

    default Editor<NbtList> reviseNbtList(String key)
    {
        return this.reviseNbtListOr(key, () -> RuntimeUtil.valueThrow(new NoSuchElementException()));
    }
    default Editor<NbtList> reviseNbtListOrNew(String key)
    {
        return this.reviseNbtListOr(key, NbtList::newInstance);
    }
    default Editor<NbtList> reviseNbtListOr(String key, Supplier<NbtList> supplier)
    {
        return Editor.of(
            () -> this.getNbtList(key).map(NbtList::clone).unwrapOrGet(supplier), child -> this.put(key, child));
    }

    @WrapMinecraftFieldAccessor(
        {
            @VersionName(name = "data", end = 1400),
            @VersionName(name = "field_11515", begin = 1400)
        }
    )
    Map<String, Object> asMap0();
    default Map<String, NbtElement> asMap()
    {
        return new MapProxy<>(
            this.asMap0(), FunctionInvertible.identity(), FunctionInvertible.wrapper(NbtElement.FACTORY));
    }

    default boolean isEmpty()
    {
        return this.asMap0().isEmpty();
    }

    @Override
    default NbtCompound clone()
    {
        NbtCompound result = newInstance();
        for(Map.Entry<String, NbtElement> entry : this.asMap().entrySet())
        {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    class Module extends MzModule
    {
        public static Module instance = new Module();

        @Override
        public void onLoad()
        {
            SimpleTester.Builder<TesterContext> testBuilder = SimpleTester.Builder.common()
                .setName(NbtCompound.class.getName()).setMinLevel(1);
            this.register(testBuilder.setFunction(context ->
            {
                int value = 114514;
                NbtCompound root = NbtCompound.newInstance();
                for(NbtCompound nbtCompound : root.reviseNbtCompoundOrNew("path")
                    .then(nbt -> nbt.reviseNbtCompoundOrNew("to")))
                {
                    nbtCompound.put("child", value);
                }
                if(root.getNbtCompound("path").unwrap().getNbtCompound("to").unwrap().getInt("child").unwrap() != value)
                    throw new AssertionError("value");
                if(root.equals(NbtCompound.newInstance()))
                    throw new AssertionError("empty");
                root = NbtCompound.newInstance();
                for(NbtCompound nbtCompound : root.reviseNbtCompoundOrNew("path")
                    .then(nbt -> nbt.reviseNbtCompoundOrNew("to")))
                {
                    nbtCompound.put("child", value);
                    break;
                }
                if(!root.equals(NbtCompound.newInstance()))
                    throw new AssertionError("value break");
                root = NbtCompound.newInstance();
                for(NbtCompound path : root.reviseNbtCompoundOrNew("path"))
                {
                    for(NbtCompound to : path.reviseNbtCompoundOrNew("to"))
                    {
                        to.put("child", value);
                    }
                }
                if(root.getNbtCompound("path").unwrap().getNbtCompound("to").unwrap().getInt("child").unwrap() != value)
                    throw new AssertionError("revise");
                NbtCompound path1 = root.getNbtCompound("path").unwrap();
                for(NbtCompound path : root.reviseNbtCompoundOrNew("path"))
                {
                    path.put("child", value);
                }
                if(path1.equals(root.getNbtCompound("path").unwrap()))
                    throw new AssertionError("revise fork");
                root = NbtCompound.newInstance();
                for(NbtCompound path : root.reviseNbtCompoundOrNew("path"))
                {
                    for(NbtCompound to : path.reviseNbtCompoundOrNew("to"))
                    {
                        to.put("child", value);
                    }
                    break;
                }
                if(!root.equals(NbtCompound.newInstance()))
                    throw new AssertionError("revise break");
            }).build());
        }
    }
}
