package mz.mzlib.util;

import mz.mzlib.util.wrapper.*;

@WrapClass(Class.class)
public interface WrapperClass extends WrapperObject
{
    WrapperFactory<WrapperClass> FACTORY = WrapperFactory.of(WrapperClass.class);

    @JvmVersion(begin = 9)
    @WrapMethod("getModule")
    WrapperModuleJ9 getModuleJ9();
}
