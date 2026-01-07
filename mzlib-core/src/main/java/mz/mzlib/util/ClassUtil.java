package mz.mzlib.util;

import moe.karla.usf.root.RootAccess;
import moe.karla.usf.unsafe.Unsafe;
import mz.mzlib.asm.ClassWriter;
import mz.mzlib.asm.Opcodes;
import mz.mzlib.asm.tree.ClassNode;
import mz.mzlib.asm.tree.MethodNode;
import mz.mzlib.util.asm.AsmUtil;
import mz.mzlib.util.wrapper.WrapperClassInfo;
import mz.mzlib.util.wrapper.WrapperObject;
import net.bytebuddy.agent.ByteBuddyAgent;

import java.io.FileOutputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.invoke.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ClassUtil
{
    private ClassUtil()
    {
    }

    public static ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();
    public static ClassLoader extClassLoader = sysClassLoader.getParent();

    private static final MethodHandle method$Class$getName = RuntimeUtil.sneakilyRun(
        () -> RootAccess.getTrustedLookupIn(Class.class).findVirtual(Class.class, "getName", MethodType.methodType(String.class)));
    public static String getName(Class<?> clazz)
    {
        try
        {
            return (String) method$Class$getName.invokeExact(clazz);
        }
        catch(Throwable e)
        {
            throw RuntimeUtil.sneakilyThrow(e);
        }
    }
    public static Class<?> classForName(String name, ClassLoader cl) throws ClassNotFoundException
    {
        switch(name)
        {
            case "void":
                return void.class;
            case "byte":
                return byte.class;
            case "short":
                return short.class;
            case "int":
                return int.class;
            case "long":
                return long.class;
            case "float":
                return float.class;
            case "double":
                return double.class;
            case "boolean":
                return boolean.class;
            case "char":
                return char.class;
            default:
                return Class.forName(name, false, cl);
        }
    }

    public static MethodType methodType(Method method)
    {
        return MethodType.methodType(method.getReturnType(), method.getParameterTypes());
    }
    public static MethodType methodType(Constructor<?> constructor)
    {
        return MethodType.methodType(void.class, constructor.getParameterTypes());
    }
    public static MethodType methodType(Member member)
    {
        if(member instanceof Method)
            return methodType((Method) member);
        else if(member instanceof Constructor)
            return methodType((Constructor<?>) member);
        else
            throw new IllegalArgumentException("Unsupported member type: " + member);
    }

    public static Field getField(Class<?> clazz, String name) throws Throwable
    {
        try
        {
            return clazz.getDeclaredField(name);
        }
        catch(Throwable e)
        {
            try
            {
                if(clazz != Object.class)
                    return getField(getSuperclass(clazz), name);
            }
            catch(Throwable ignored)
            {
            }
            for(Class<?> i : clazz.getInterfaces())
            {
                try
                {
                    return getField(i, name);
                }
                catch(Throwable ignored)
                {
                }
            }
            throw e;
        }
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) throws Throwable
    {
        try
        {
            return clazz.getDeclaredMethod(name, parameterTypes);
        }
        catch(Throwable e)
        {
            try
            {
                if(clazz != Object.class)
                    return getMethod(getSuperclass(clazz), name, parameterTypes);
            }
            catch(Throwable ignored)
            {
            }
            for(Class<?> i : clazz.getInterfaces())
            {
                try
                {
                    return getMethod(i, name, parameterTypes);
                }
                catch(Throwable ignored)
                {
                }
            }
            throw e;
        }
    }

    public static Class<?> baseType(Class<?> type)
    {
        if(type.isPrimitive())
            return type;
        else
            return Object.class;
    }

    public static Class<?> getReturnType(Member m)
    {
        if(m instanceof Method)
        {
            return ((Method) m).getReturnType();
        }
        if(!(m instanceof Constructor))
            throw new IllegalArgumentException(Objects.toString(m));
        return void.class;
    }

    public static List<? extends Member> getDeclaredMembers(Class<?> clazz)
    {
        List<Member> result = new ArrayList<>(Arrays.asList(clazz.getDeclaredConstructors()));
        result.addAll(Arrays.asList(clazz.getDeclaredFields()));
        result.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        return result;
    }

    public static Method getDeclaredMethod(Class<?> clazz, Method method)
    {
        try
        {
            return clazz.getDeclaredMethod(method.getName(), method.getParameterTypes());
        }
        catch(Throwable e)
        {
            throw RuntimeUtil.sneakilyThrow(e);
        }
    }

    public static MethodHandle findConstructor(Class<?> declaringClass, Class<?>... parameterTypes)
        throws NoSuchMethodException
    {
        try
        {
            return RootAccess.getTrustedLookupIn(declaringClass)
                .findConstructor(declaringClass, MethodType.methodType(void.class, parameterTypes));
        }
        catch(IllegalAccessException e)
        {
            throw new AssertionError(e);
        }
    }

    public static MethodHandle findMethod(
        Class<?> declaringClass,
        boolean isStatic,
        String name,
        Class<?> returnType,
        Class<?>... parameterTypes) throws NoSuchMethodException
    {
        try
        {
            if(isStatic)
                return RootAccess.getTrustedLookupIn(declaringClass)
                    .findStatic(declaringClass, name, MethodType.methodType(returnType, parameterTypes));
            else
                return RootAccess.getTrustedLookupIn(declaringClass)
                    .findVirtual(declaringClass, name, MethodType.methodType(returnType, parameterTypes));
        }
        catch(IllegalAccessException e)
        {
            throw new AssertionError(e);
        }
    }

    public static MethodHandle findMethodSpecial(
        Class<?> declaringClass,
        String name,
        Class<?> returnType,
        Class<?>... parameterTypes) throws NoSuchMethodException
    {
        try
        {
            return RootAccess.getTrustedLookupIn(declaringClass)
                .findSpecial(declaringClass, name, MethodType.methodType(returnType, parameterTypes), declaringClass);
        }
        catch(IllegalAccessException e)
        {
            throw new AssertionError(e);
        }
    }

    public static MethodHandle findFieldGetter(Class<?> declaringClass, boolean isStatic, String name, Class<?> type)
        throws NoSuchFieldException
    {
        try
        {
            if(isStatic)
                return RootAccess.getTrustedLookupIn(declaringClass).findStaticGetter(declaringClass, name, type);
            else
                return RootAccess.getTrustedLookupIn(declaringClass).findGetter(declaringClass, name, type);
        }
        catch(IllegalAccessException e)
        {
            throw new AssertionError(e);
        }
    }
    public static MethodHandle findFieldGetter(Class<?> declaringClass, boolean isStatic, String name)
        throws NoSuchFieldException, IllegalAccessException
    {
        return findFieldGetter(declaringClass, isStatic, name, declaringClass.getDeclaredField(name).getType());
    }

    public static MethodHandle findFieldSetter(Class<?> declaringClass, boolean isStatic, String name, Class<?> type)
        throws NoSuchFieldException
    {
        try
        {
            if(isStatic)
            {
                return RootAccess.getTrustedLookupIn(declaringClass).findStaticSetter(declaringClass, name, type);
            }
            else
            {
                return RootAccess.getTrustedLookupIn(declaringClass).findSetter(declaringClass, name, type);
            }
        }
        catch(IllegalAccessException e)
        {
            throw new AssertionError(e);
        }
    }
    public static MethodHandle findFieldSetter(Class<?> declaringClass, boolean isStatic, String name)
        throws NoSuchFieldException, IllegalAccessException
    {
        return findFieldSetter(declaringClass, isStatic, name, declaringClass.getDeclaredField(name).getType());
    }

    public static MethodHandle unreflect(Constructor<?> constructor)
    {
        try
        {
            return RootAccess.getTrustedLookupIn(constructor.getDeclaringClass()).unreflectConstructor(constructor);
        }
        catch(IllegalAccessException e)
        {
            throw new AssertionError(e);
        }
    }

    public static MethodHandle unreflectGetter(Field field)
    {
        try
        {
            return RootAccess.getTrustedLookupIn(field.getDeclaringClass()).unreflectGetter(field);
        }
        catch(IllegalAccessException e)
        {
            throw new AssertionError(e);
        }
    }

    public static MethodHandle unreflectSetter(Field field)
    {
        try
        {
            return RootAccess.getTrustedLookupIn(field.getDeclaringClass()).unreflectSetter(field);
        }
        catch(IllegalAccessException e)
        {
            throw new AssertionError(e);
        }
    }

    public static MethodHandle unreflect(Method method)
    {
        try
        {
            return RootAccess.getTrustedLookupIn(method.getDeclaringClass()).unreflect(method);
        }
        catch(IllegalAccessException e)
        {
            throw new AssertionError(e);
        }
    }

    public static MethodHandle unreflectSpecial(Method method)
    {
        try
        {
            return RootAccess.getTrustedLookupIn(method.getDeclaringClass()).unreflectSpecial(method, method.getDeclaringClass());
        }
        catch(IllegalAccessException e)
        {
            throw new AssertionError(e);
        }
    }

    public static Class<?> getSuperclass(Class<?> clazz)
    {
        if(clazz.isInterface())
            return Object.class;
        return clazz.getSuperclass();
    }

    public static <E extends Throwable> void forEachSuper(Class<?> clazz, ThrowableConsumer<Class<?>, E> proc) throws E
    {
        proc.accept(clazz);
        if(clazz != Object.class)
            forEachSuper(getSuperclass(clazz), proc);
        for(Class<?> i : clazz.getInterfaces())
        {
            forEachSuper(i, proc);
        }
    }

    public static <E extends Throwable> void forEachSuperUnique(Class<?> clazz, ThrowableConsumer<Class<?>, E> proc)
        throws E
    {
        Set<Class<?>> history = new HashSet<>();
        forEachSuper(
            clazz, c ->
            {
                if(history.add(c))
                    proc.acceptOrThrow(c);
            }
        );
    }

    /**
     * Iterate through all super classes in topological order
     * From super to children
     */
    public static <E extends Throwable> void forEachSuperTopology(Class<?> clazz, ThrowableConsumer<Class<?>, E> proc)
        throws E
    {
        Map<Class<?>, Integer> degreeIn = new HashMap<>();
        Map<Class<?>, Set<Class<?>>> edgeOut = new HashMap<>();
        forEachSuperUnique(
            clazz, c ->
            {
                if(c != Object.class)
                {
                    edgeOut.computeIfAbsent(getSuperclass(c), ThrowableSupplier.of(HashSet<Class<?>>::new).ignore())
                        .add(c);
                    degreeIn.compute(c, (k, v) -> Option.fromNullable(v).unwrapOr(0) + 1);
                }
                for(Class<?> i : c.getInterfaces())
                {
                    edgeOut.computeIfAbsent(i, ThrowableSupplier.of(HashSet<Class<?>>::new).ignore()).add(c);
                }
                degreeIn.compute(c, (k, v) -> Option.fromNullable(v).unwrapOr(0) + c.getInterfaces().length);
            }
        );
        Queue<Class<?>> q = new ArrayDeque<>();
        q.add(Object.class);
        while(!q.isEmpty())
        {
            Class<?> now = q.poll();
            proc.acceptOrThrow(now);
            for(Set<Class<?>> es : Option.fromNullable(edgeOut.get(now)))
            {
                for(Class<?> c : es)
                {
                    if(degreeIn.compute(c, (k, v) -> Objects.requireNonNull(v) - 1) == 0)
                        q.add(c);
                }
            }
        }
        for(Integer value : degreeIn.values())
        {
            assert value == 0;
        }
    }

    public static Instrumentation instrumentation;

    public static Instrumentation getInstrumentation()
    {
        if(instrumentation == null)
        {
            try
            {
                ByteBuddyAgent.install();
            }
            catch(Throwable e)
            {
                System.err.println("Unable to inject JavaAgent");
                System.err.println(
                    "Please remove the startup parameters -XX:+DisableAttachMechanism and -Djdk.attach.allowAttachSelf=false");
                System.err.println(
                    "You can also try installing ByteBuddyAgent manually (this is not a plugin, check the installation method on the MzLib official website)");
                System.err.println("无法注入 JavaAgent");
                System.err.println(
                    "请删除启动参数-XX:+DisableAttachMechanism 和 -D" + "jdk.attach.allowAttachSelf=false");
                System.err.println("也可以尝试手动安装 ByteBuddyAgent（这不是一个插件，在 MzLib 官网查看安装方法）");
                throw e;
            }
            instrumentation = ByteBuddyAgent.getInstrumentation();
        }
        return instrumentation;
    }

    public synchronized static byte[] getByteCode(Class<?> clazz)
    {
        try
        {
            RefStrong<byte[]> result = new RefStrong<>(null);
            while(result.get() == null)
            {
                ClassFileTransformer tr = new ClassFileTransformer()
                {
                    @Override
                    public byte[] transform(
                        ClassLoader cl,
                        String name,
                        Class<?> c,
                        ProtectionDomain d,
                        byte[] byteCode)
                    {
                        if(c == clazz)
                            result.set(byteCode);
                        getInstrumentation().removeTransformer(this);
                        return null;
                    }
                };
                getInstrumentation().addTransformer(tr, true);
                getInstrumentation().retransformClasses(clazz);
            }
            return result.get();
        }
        catch(Throwable e)
        {
            throw RuntimeUtil.sneakilyThrow(e);
        }
    }

    public static Class<?> defineClass(ClassLoader classLoader, String name, byte[] byteCode)
    {
        synchronized(ClassUtil.class)
        {
            try
            {
                try
                {
                    try
                    {
                        getInstrumentation().redefineClasses(
                            new ClassDefinition(Class.forName(name.replace('/', '.'), false, classLoader), byteCode));
                        return Class.forName(name.replace('/', '.'), false, classLoader);
                    }
                    catch(Throwable e)
                    {
                        try
                        {
                            return Unsafe.getUnsafe().defineClass(name, byteCode, 0, byteCode.length, classLoader, null);
                        }
                        catch(Throwable e1)
                        {
                            e1.addSuppressed(e);
                            throw e1;
                        }
                    }
                }
                catch(VerifyError e)
                {
                    try(FileOutputStream fos = new FileOutputStream("test.class"))
                    {
                        fos.write(byteCode);
                    }
                    catch(Throwable e1)
                    {
                        throw RuntimeUtil.sneakilyThrow(e1);
                    }
                    throw RuntimeUtil.sneakilyThrow(e);
                }
                catch(Throwable e)
                {
                    throw RuntimeUtil.sneakilyThrow(e);
                }
            }
            catch(Throwable e)
            {
                try(FileOutputStream fos = new FileOutputStream("test.class"))
                {
                    fos.write(byteCode);
                }
                catch(Throwable ignored)
                {
                }
                throw e;
            }
        }
    }

    public static void makeReference(ClassLoader classLoader, Object target)
    {
        try
        {
            String attachedName = "0MzAttachedObjects";
            Class<?> attached;
            try
            {
                attached = Class.forName(attachedName, false, classLoader);
            }
            catch(ClassNotFoundException e)
            {
                ClassNode cn = new ClassNode();
                cn.visit(
                    Opcodes.V1_8, Opcodes.ACC_PUBLIC, attachedName, null, AsmUtil.getType(Object.class),
                    new String[]{}
                );
                cn.visitField(
                        Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "instance", AsmUtil.getDesc(Set.class), null, null)
                    .visitEnd();
                cn.visitEnd();
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                cn.accept(cw);
                attached = defineClass(classLoader, attachedName, cw.toByteArray());
                attached.getDeclaredField("instance").set(null, ConcurrentHashMap.newKeySet());
            }
            RuntimeUtil.<Set<Object>>cast(attached.getDeclaredField("instance").get(null)).add(new RefStrong<>(target));
        }
        catch(Throwable e)
        {
            throw RuntimeUtil.sneakilyThrow(e);
        }
    }

    public static <T> Class<T> getPrimitive(Class<T> src)
    {
        if(src == Character.class)
        {
            return RuntimeUtil.castClass(char.class);
        }
        else if(src == Boolean.class)
        {
            return RuntimeUtil.castClass(boolean.class);
        }
        else if(src == Byte.class)
        {
            return RuntimeUtil.castClass(byte.class);
        }
        else if(src == Short.class)
        {
            return RuntimeUtil.castClass(short.class);
        }
        else if(src == Integer.class)
        {
            return RuntimeUtil.castClass(int.class);
        }
        else if(src == Long.class)
        {
            return RuntimeUtil.castClass(long.class);
        }
        else if(src == Float.class)
        {
            return RuntimeUtil.castClass(float.class);
        }
        else if(src == Double.class)
        {
            return RuntimeUtil.castClass(double.class);
        }
        else if(src == Void.class)
        {
            return RuntimeUtil.castClass(void.class);
        }
        else
        {
            return src;
        }
    }

    public static <T> Class<T> getWrapper(Class<T> src)
    {
        if(src == char.class)
        {
            return RuntimeUtil.castClass(Character.class);
        }
        else if(src == boolean.class)
        {
            return RuntimeUtil.castClass(Boolean.class);
        }
        else if(src == byte.class)
        {
            return RuntimeUtil.castClass(Byte.class);
        }
        else if(src == short.class)
        {
            return RuntimeUtil.castClass(Short.class);
        }
        else if(src == int.class)
        {
            return RuntimeUtil.castClass(Integer.class);
        }
        else if(src == long.class)
        {
            return RuntimeUtil.castClass(Long.class);
        }
        else if(src == float.class)
        {
            return RuntimeUtil.castClass(Float.class);
        }
        else if(src == double.class)
        {
            return RuntimeUtil.castClass(Double.class);
        }
        else if(src == void.class)
        {
            return RuntimeUtil.castClass(Void.class);
        }
        else
        {
            return src;
        }
    }

    public static MethodHandle defineMethod(ClassLoader cl, MethodNode mn)
    {
        ClassNode cn = new ClassNode();
        cn.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, "$Method", null, AsmUtil.getType(Object.class), new String[0]);
        cn.methods.add(mn);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return unreflect(defineClass(new SimpleClassLoader(cl), cn.name, cw.toByteArray()).getDeclaredMethods()[0]);
    }

    public static CallSite getConstructorCallSite(
        MethodHandles.Lookup caller,
        String invokedName,
        MethodType invokedType,
        String ownerName,
        MethodType methodType) throws NoSuchMethodException, IllegalAccessException, ClassNotFoundException
    {
        return new ConstantCallSite(
            findConstructor(
                Class.forName(ownerName, false, caller.lookupClass().getClassLoader()),
                methodType.parameterArray()
            ).asType(invokedType));
    }

    public static CallSite getMethodCallSite(
        MethodHandles.Lookup caller,
        String invokedName,
        MethodType invokedType,
        String ownerName,
        MethodType methodType,
        int isStatic) throws NoSuchMethodException, IllegalAccessException, ClassNotFoundException
    {
        return new ConstantCallSite(
            findMethod(
                Class.forName(ownerName, false, caller.lookupClass().getClassLoader()), isStatic != 0, invokedName,
                methodType.returnType(), methodType.parameterArray()
            ).asType(invokedType));
    }

    public static CallSite getMethodSpecialCallSite(
        MethodHandles.Lookup caller,
        String invokedName,
        MethodType invokedType,
        String ownerName,
        MethodType methodType) throws NoSuchMethodException, IllegalAccessException, ClassNotFoundException
    {
        return new ConstantCallSite(
            findMethodSpecial(
                Class.forName(ownerName, false, caller.lookupClass().getClassLoader()), invokedName,
                methodType.returnType(), methodType.parameterArray()
            ).asType(invokedType));
    }

    public static CallSite getFieldGetterCallSite(
        MethodHandles.Lookup caller,
        String invokedName,
        MethodType invokedType,
        String ownerName,
        MethodType methodType) throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException
    {
        return new ConstantCallSite(
            findFieldGetter(
                Class.forName(ownerName, false, caller.lookupClass().getClassLoader()),
                invokedType.parameterCount() == 0, invokedName, methodType.returnType()
            ).asType(invokedType));
    }

    public static CallSite getFieldSetterCallSite(
        MethodHandles.Lookup caller,
        String invokedName,
        MethodType invokedType,
        String ownerName,
        MethodType methodType) throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException
    {
        return new ConstantCallSite(
            findFieldSetter(
                Class.forName(ownerName, false, caller.lookupClass().getClassLoader()),
                invokedType.parameterCount() == 1, invokedName,
                methodType.parameterType(methodType.parameterCount() - 1)
            ).asType(invokedType));
    }

    public static Class<?> toWrappedClass(Class<?> wrapperClass)
    {
        if(WrapperObject.class.isAssignableFrom(wrapperClass))
            return WrapperClassInfo.get(RuntimeUtil.cast(wrapperClass)).getWrappedClass();
        return wrapperClass;
    }
    public static MethodType getWrappedType(MethodType wrapperType)
    {
        return MethodType.methodType(
            toWrappedClass(wrapperType.returnType()),
            Arrays.stream(wrapperType.parameterArray()).map(ClassUtil::toWrappedClass).collect(Collectors.toList())
        );
    }

    public static CallSite getConstructorCallSiteWithWrapperType(
        MethodHandles.Lookup caller,
        String invokedName,
        MethodType invokedType,
        String ownerName,
        MethodType methodType) throws NoSuchMethodException, IllegalAccessException, ClassNotFoundException
    {
        return getConstructorCallSite(caller, invokedName, invokedType, ownerName, getWrappedType(methodType));
    }

    public static CallSite getMethodCallSiteWithWrapperType(
        MethodHandles.Lookup caller,
        String invokedName,
        MethodType invokedType,
        String ownerName,
        MethodType methodType,
        int isStatic) throws NoSuchMethodException, IllegalAccessException, ClassNotFoundException
    {
        return getMethodCallSite(caller, invokedName, invokedType, ownerName, getWrappedType(methodType), isStatic);
    }

    public static CallSite getFieldGetterCallSite(
        MethodHandles.Lookup caller,
        String invokedName,
        MethodType invokedType,
        String ownerName) throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException
    {
        return new ConstantCallSite(
            findFieldGetter(
                Class.forName(ownerName, false, caller.lookupClass().getClassLoader()),
                invokedType.parameterCount() == 0, invokedName
            ).asType(invokedType));
    }

    public static CallSite getFieldSetterCallSite(
        MethodHandles.Lookup caller,
        String invokedName,
        MethodType invokedType,
        String ownerName) throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException
    {
        return new ConstantCallSite(
            findFieldSetter(
                Class.forName(ownerName, false, caller.lookupClass().getClassLoader()),
                invokedType.parameterCount() == 1, invokedName
            ).asType(invokedType));
    }
}
