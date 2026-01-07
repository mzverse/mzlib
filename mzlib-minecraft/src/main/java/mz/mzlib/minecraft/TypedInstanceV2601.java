package mz.mzlib.minecraft;

import mz.mzlib.util.RuntimeUtil;
import mz.mzlib.util.wrapper.WrapClassForName;
import mz.mzlib.util.wrapper.WrapMethod;
import mz.mzlib.util.wrapper.WrapperFactory;
import mz.mzlib.util.wrapper.WrapperObject;

@VersionRange(begin = 2601)
@WrapClassForName("net.minecraft.core.TypedInstance")
public interface TypedInstanceV2601<T> extends WrapperObject
{
    WrapperFactory<TypedInstanceV2601<?>> FACTORY = WrapperFactory.of(RuntimeUtil.castClass(TypedInstanceV2601.class));

    @WrapMethod("is")
    boolean isType(T type);

    class Wrapper<T extends WrapperObject>
    {
        TypedInstanceV2601<?> base;
        WrapperFactory<T> type;
        public Wrapper(TypedInstanceV2601<?> base, WrapperFactory<T> type)
        {
            this.base = base;
            this.type = type;
        }
        public static <T extends WrapperObject> Wrapper<T> of(TypedInstanceV2601<?> base, WrapperFactory<T> type)
        {
            return new Wrapper<>(base, type);
        }

        public TypedInstanceV2601<?> getBase()
        {
            return this.base;
        }
        public WrapperFactory<T> getType()
        {
            return this.type;
        }

        public boolean isType(T type)
        {
            return this.getBase().isType(RuntimeUtil.cast(type.getWrapped()));
        }
    }
}
