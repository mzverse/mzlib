package mz.mzlib.util.wrapper;

import mz.mzlib.asm.tree.FieldInsnNode;
import mz.mzlib.asm.tree.MethodInsnNode;
import mz.mzlib.util.ClassUtil;
import mz.mzlib.util.Option;
import mz.mzlib.util.RuntimeUtil;
import mz.mzlib.util.asm.AsmUtil;
import mz.mzlib.util.compound.ICompoundImpl;

import java.lang.invoke.*;
import java.lang.reflect.*;
import java.util.function.Function;

@WrapClass(Object.class)
public interface WrapperObject
{
    WrapperFactory<WrapperObject> FACTORY = WrapperFactory.of(WrapperObject.class);
    @Deprecated
    @WrapperCreator
    static WrapperObject create(Object wrapped)
    {
        return WrapperObject.create(WrapperObject.class, wrapped);
    }

    Object getWrapped();

    void setWrapped(Object wrapped);

    default void setWrappedFrom(WrapperObject wrapper)
    {
        this.setWrapped(wrapper.getWrapped());
    }

    static String debugInfo(WrapperObject wrapper)
    {
        return wrapper.getClass().getName() + "{" + wrapper.getWrapped() + "}";
    }

    /**
     * slow
     */
    @Deprecated
    static <T extends WrapperObject> T create(Class<T> type, Object wrapped)
    {
        try
        {
            return RuntimeUtil.cast(
                (WrapperObject) WrapperClassInfo.get(type).getConstructor().invokeExact((Object) wrapped));
        }
        catch(Throwable e)
        {
            throw RuntimeUtil.sneakilyThrow(e);
        }
    }

    static CallSite getConstructorCallSite(
        MethodHandles.Lookup caller,
        String invokedName,
        MethodType invokedType,
        Class<? extends WrapperObject> wrapperClass)
    {
        return new ConstantCallSite(WrapperClassInfo.get(wrapperClass).getConstructor().asType(invokedType));
    }
    static CallSite callSiteFactory(
        MethodHandles.Lookup caller,
        String invokedName,
        MethodType invokedType,
        Class<? extends WrapperObject> wrapperClass)
    {
        return new ConstantCallSite(MethodHandles.constant(WrapperFactory.class, WrapperFactory.of(wrapperClass)).asType(invokedType));
    }

    /**
     * slow
     */
    static Class<?> getWrappedClass(Class<? extends WrapperObject> wrapperClass)
    {
        return WrapperClassInfo.get(wrapperClass).getWrappedClass();
    }

    Class<?> static$getWrappedClass();

    WrapperObject static$create(Object wrapped);

    default boolean static$isInstance(WrapperObject wrapper)
    {
        return this.static$getWrappedClass().isInstance(wrapper.getWrapped());
    }

    default <T extends WrapperObject> boolean is(WrapperFactory<T> factory)
    {
        return factory.isInstance(this);
    }
    default <T extends WrapperObject> T as(WrapperFactory<T> factory)
    {
        if(this.isPresent() && !this.is(factory))
            throw new ClassCastException("Try to cast an object of " + this.getWrapped().getClass() + " to " +
                factory.getStatic().static$getWrappedClass());
        return factory.create(this.getWrapped());
    }
    default <T extends WrapperObject> Option<T> asOption(WrapperFactory<T> factory)
    {
        if(this.is(factory))
            return Option.some(this.as(factory));
        else
            return Option.none();
    }

    default Option<WrapperObject> asCompound()
    {
        if(!(this.getWrapped() instanceof ICompoundImpl))
            return Option.none();
        return Option.some(((ICompoundImpl) this.getWrapped()).compound$getWrapper());
    }


    default boolean isInstanceOf(WrapperFactory<?> factory)
    {
        return this.is(factory);
    }
    default <T extends WrapperObject> T castTo(WrapperFactory<T> factory)
    {
        return this.as(factory);
    }
    default <T extends WrapperObject> Option<T> tryCast(WrapperFactory<T> factory)
    {
        return this.asOption(factory);
    }

    @Deprecated
    default boolean isInstanceOf(Function<Object, ? extends WrapperObject> creator)
    {
        return this.isInstanceOf(new WrapperFactory<>(creator));
    }
    @Deprecated
    default <T extends WrapperObject> T castTo(Function<Object, T> creator)
    {
        return this.castTo(new WrapperFactory<>(creator));
    }
    @Deprecated
    default <T extends WrapperObject> Option<T> tryCast(Function<Object, T> creator)
    {
        return this.tryCast(new WrapperFactory<>(creator));
    }

    /**
     * @see #isPresent()
     * @deprecated wrapper shouldn't be null, please invoke wrapper.isPresent()
     */
    @Deprecated
    static boolean isPresent(WrapperObject wrapper)
    {
        return wrapper != null && wrapper.getWrapped() != null;
    }

    default boolean isPresent()
    {
        return this.getWrapped() != null;
    }

    @Override
    @WrapMethod("toString")
    String toString();
    @Override
    @WrapMethod("hashCode")
    int hashCode();
    @Override
    boolean equals(Object object);
    @SpecificImpl("equals")
    default boolean equals$impl(Object object)
    {
        if(this == object)
            return true;
        if(!(object instanceof WrapperObject))
            return false;
        return this.equals$impl((WrapperObject) object);
    }
    @WrapMethod("equals")
    boolean equals$impl(WrapperObject object);
    @WrapMethod("clone")
    WrapperObject clone();

    static FieldInsnNode insnField(int opcode, Class<? extends WrapperObject> owner, String getterName)
        throws NoSuchMethodException
    {
        Field target = (Field) WrapperClassInfo.get(owner).getWrappedMembers().get(owner.getMethod(getterName));
        return new FieldInsnNode(
            opcode, AsmUtil.getType(target.getDeclaringClass()), target.getName(), AsmUtil.getDesc(target.getType()));
    }

    static MethodInsnNode insnMethod(
        int opcode,
        Class<? extends WrapperObject> owner,
        String name,
        MethodType methodType,
        boolean isInterface) throws NoSuchMethodException
    {
        Executable target = (Executable) WrapperClassInfo.get(owner).getWrappedMembers()
            .get(owner.getMethod(name, methodType.parameterArray()));
        return new MethodInsnNode(
            opcode, AsmUtil.getType(target.getDeclaringClass()),
            target instanceof Constructor ? "<init>" : target.getName(), AsmUtil.getDesc(target), isInterface
        );
    }

    final class Bsm
    {
        private Bsm()
        {
        }

        public static CallSite fromWrapper(
            MethodHandles.Lookup caller,
            String wrapperMethodName,
            MethodType invokedType,
            Class<? extends WrapperObject> wrapperClass,
            MethodType wrapperMethodType) throws NoSuchMethodException, NoSuchFieldException
        {
            Member member = WrapperClassInfo.get(wrapperClass).getWrappedMembers()
                .get(wrapperClass.getMethod(wrapperMethodName, wrapperMethodType.parameterArray()));
            MethodHandle result;
            if(member instanceof Method)
            {
                Method method = (Method) member;
                result = ClassUtil.findMethod(
                    method.getDeclaringClass(), Modifier.isStatic(method.getModifiers()), method.getName(),
                    method.getReturnType(), method.getParameterTypes()
                );
            }
            else if(member instanceof Constructor)
            {
                Constructor<?> constructor = (Constructor<?>) member;
                result = ClassUtil.findConstructor(constructor.getDeclaringClass(), constructor.getParameterTypes());
            }
            else if(member instanceof Field)
            {
                Field field = (Field) member;
                switch(wrapperMethodType.parameterCount())
                {
                    case 0:
                        result = ClassUtil.findFieldGetter(field.getDeclaringClass(), Modifier.isStatic(field.getModifiers()), field.getName(), field.getType());
                        break;
                    case 1:
                        result = ClassUtil.findFieldSetter(field.getDeclaringClass(), Modifier.isStatic(field.getModifiers()), field.getName(), field.getType());
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported field accessor type: " + wrapperMethodType);
                }
            }
            else
                throw new UnsupportedOperationException("Unsupported member: " + member);
            return new ConstantCallSite(result.asType(invokedType));
        }
    }

    @WrapSameClass(WrapperObject.class)
    interface Generic<T> extends WrapperObject
    {
        WrapperFactory<Generic<?>> FACTORY = RuntimeUtil.cast(WrapperFactory.of(Generic.class));

        static <T> WrapperFactory<Generic<T>> factory()
        {
            return RuntimeUtil.cast(FACTORY);
        }

        @Override
        T getWrapped();

        @Override
        default void setWrappedFrom(WrapperObject wrapper)
        {
            WrapperObject.super.setWrappedFrom(wrapper);
        }
    }
}
