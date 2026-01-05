package mz.mzlib.util;

import mz.mzlib.util.wrapper.*;

@JvmVersion(begin = 9)
@WrapClassForName("java.lang.Module")
public interface WrapperModuleJ9 extends WrapperObject
{
    WrapperFactory<WrapperModuleJ9> FACTORY = WrapperFactory.of(WrapperModuleJ9.class);
    @Deprecated
    @WrapperCreator
    static WrapperModuleJ9 create(Object wrapped)
    {
        return WrapperObject.create(WrapperModuleJ9.class, wrapped);
    }

    @WrapMethod("isOpen")
    boolean isOpen(String packageName, WrapperModuleJ9 target);

    @WrapMethod("isExported")
    boolean isExported(String packageName, WrapperModuleJ9 target);
}
