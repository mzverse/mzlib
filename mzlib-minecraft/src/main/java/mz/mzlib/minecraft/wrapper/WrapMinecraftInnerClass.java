package mz.mzlib.minecraft.wrapper;

import mz.mzlib.minecraft.MinecraftPlatform;
import mz.mzlib.minecraft.VersionName;
import mz.mzlib.util.ClassUtil;
import mz.mzlib.util.ElementSwitcher;
import mz.mzlib.util.ElementSwitcherClass;
import mz.mzlib.util.wrapper.WrappedClassFinder;
import mz.mzlib.util.wrapper.WrappedClassFinderClass;
import mz.mzlib.util.wrapper.WrapperObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ElementSwitcherClass(WrapMinecraftInnerClass.Handler.class)
@WrappedClassFinderClass(WrapMinecraftInnerClass.Handler.class)
public @interface WrapMinecraftInnerClass
{
    Class<? extends WrapperObject> outer();

    VersionName[] name();

    class Handler implements ElementSwitcher<WrapMinecraftInnerClass>, WrappedClassFinder<WrapMinecraftInnerClass>
    {
        @Override
        public boolean isEnabled(WrapMinecraftInnerClass annotation, AnnotatedElement element)
        {
            if(!ElementSwitcher.isEnabled(annotation.outer()))
                return false;
            for(VersionName n : annotation.name())
            {
                if(MinecraftPlatform.instance.inVersion(n))
                    return true;
            }
            return false;
        }

        public Class<?> find(Class<? extends WrapperObject> wrapperClass, WrapMinecraftInnerClass annotation)
            throws ClassNotFoundException
        {
            ClassLoader classLoader = wrapperClass.getClassLoader();

            ClassNotFoundException lastException = null;
            for(VersionName inner : annotation.name())
            {
                if(!MinecraftPlatform.instance.inVersion(inner))
                    continue;
                if(inner.remap())
                {
                    try
                    {
                        return ClassUtil.classForName(
                            MinecraftPlatform.instance.getMappings().inverse()
                                .mapClass(MinecraftPlatform.instance.getMappings().mapClass(
                                    WrapperObject.getWrappedClass(annotation.outer()).getName()) + "$" +
                                    inner.name()),
                            classLoader
                        );
                    }
                    catch(ClassNotFoundException e)
                    {
                        lastException = e;
                    }
                }
                else
                {
                    try
                    {
                        return Class.forName(
                            MinecraftPlatform.instance.getMappings().inverse().mapClass(
                                WrapperObject.getWrappedClass(annotation.outer()).getName() + "$" + inner.name()),
                            false,
                            classLoader
                        );
                    }
                    catch(ClassNotFoundException e)
                    {
                        lastException = e;
                    }
                }
            }

            if(lastException != null)
                throw lastException;
            return null;
        }
    }
}
