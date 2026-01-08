package mz.mzlib.minecraft.wrapper;

import mz.mzlib.minecraft.MinecraftPlatform;
import mz.mzlib.minecraft.VersionName;
import mz.mzlib.util.ElementSwitcher;
import mz.mzlib.util.ElementSwitcherClass;
import mz.mzlib.util.RuntimeUtil;
import mz.mzlib.util.wrapper.WrapFieldAccessor;
import mz.mzlib.util.wrapper.WrappedMemberFinder;
import mz.mzlib.util.wrapper.WrappedMemberFinderClass;
import mz.mzlib.util.wrapper.WrapperObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ElementSwitcherClass(WrapMinecraftFieldAccessor.Handler.class)
@WrappedMemberFinderClass(WrapMinecraftFieldAccessor.Handler.class)
public @interface WrapMinecraftFieldAccessor
{
    VersionName[] value();

    class Handler implements ElementSwitcher<WrapMinecraftFieldAccessor>, WrappedMemberFinder<WrapMinecraftFieldAccessor>
    {
        @Override
        public boolean isEnabled(WrapMinecraftFieldAccessor annotation, AnnotatedElement element)
        {
            for(VersionName n : annotation.value())
            {
                if(MinecraftPlatform.instance.inVersion(n))
                    return true;
            }
            return false;
        }

        @Override
        public Member find(
            Class<? extends WrapperObject> wrapperClass,
            Class<?> wrappedClass,
            Method wrapperMethod,
            WrapMinecraftFieldAccessor annotation,
            Class<?> returnType,
            Class<?>[] argTypes) throws NoSuchFieldException
        {
            NoSuchFieldException lastException = null;
            for(VersionName name : annotation.value())
            {
                if(!MinecraftPlatform.instance.inVersion(name))
                    continue;
                if(name.remap())
                {
                    try
                    {
                        return WrapFieldAccessor.Handler.class.newInstance().find(
                            wrapperClass, wrappedClass, wrapperMethod, new WrapFieldAccessor()
                            {
                                @Override
                                public Class<WrapFieldAccessor> annotationType()
                                {
                                    return WrapFieldAccessor.class;
                                }

                                @Override
                                public String[] value()
                                {
                                    return new String[]{
                                        MinecraftPlatform.instance.getMappings().inverse().mapField(
                                            MinecraftPlatform.instance.getMappings().mapClass(
                                                WrapperObject.getWrappedClass(wrapperClass).getName()),
                                            name.name()
                                        )
                                    };
                                }
                            }, returnType, argTypes
                        );
                    }
                    catch(NoSuchFieldException e)
                    {
                        lastException = e;
                    }
                    catch(InstantiationException | IllegalAccessException e)
                    {
                        throw RuntimeUtil.sneakilyThrow(e);
                    }
                }
                else
                {
                    try
                    {
                        return WrapFieldAccessor.Handler.class.newInstance().find(
                            wrapperClass, wrappedClass, wrapperMethod, new WrapFieldAccessor()
                            {
                                @Override
                                public Class<WrapFieldAccessor> annotationType()
                                {
                                    return WrapFieldAccessor.class;
                                }

                                @Override
                                public String[] value()
                                {
                                    return new String[]{ name.name() };
                                }
                            }, returnType, argTypes
                        );
                    }
                    catch(NoSuchFieldException e)
                    {
                        lastException = e;
                    }
                    catch(InstantiationException | IllegalAccessException e)
                    {
                        throw RuntimeUtil.sneakilyThrow(e);
                    }
                }
            }
            if(lastException != null)
                throw lastException;
            return null;
        }
    }
}
