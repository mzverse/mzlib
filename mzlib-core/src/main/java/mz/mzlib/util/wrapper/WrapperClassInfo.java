package mz.mzlib.util.wrapper;

import mz.mzlib.asm.*;
import mz.mzlib.asm.Type;
import mz.mzlib.asm.tree.*;
import mz.mzlib.util.*;
import mz.mzlib.util.asm.AsmUtil;

import java.io.FileOutputStream;
import java.lang.annotation.Annotation;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WrapperClassInfo
{
    public Class<? extends WrapperObject> wrapperClass;
    public Annotation wrapperClassAnnotation;
    public Class<?> wrappedClass;

    public WrapperClassInfo(Class<? extends WrapperObject> wrapperClass)
    {
        this.wrapperClass = wrapperClass;
    }

    public Class<? extends WrapperObject> getWrapperClass()
    {
        return this.wrapperClass;
    }

    public Class<?> getWrappedClass()
    {
        if(this.wrappedClass == null)
            throw new IllegalStateException("Wrapped class not found: " + this.wrapperClass);
        return this.wrappedClass;
    }

    void analyseWrappedClass()
    {
        Throwable lastException = null;
        for(Annotation i : this.wrapperClass.getDeclaredAnnotations())
        {
            WrappedClassFinderClass finder = i.annotationType().getDeclaredAnnotation(WrappedClassFinderClass.class);
            if(finder != null)
            {
                try
                {
                    this.wrappedClass = finder.value().newInstance().find(this.wrapperClass, RuntimeUtil.cast(i));
                }
                catch(Throwable e)
                {
                    lastException = e;
                }
                if(this.wrappedClass != null)
                {
                    this.wrapperClassAnnotation = i;
                    break;
                }
            }
        }
        if(this.wrappedClass == null)
            throw new IllegalStateException("Wrapped class not found: " + this.wrapperClass, lastException);
    }

    static ClassCache<Class<? extends WrapperObject>, WrapperClassInfo> cache = new ClassCache<>(clazz ->
    {
        WrapperClassInfo result = new WrapperClassInfo(clazz);
        if(ElementSwitcher.isEnabled(clazz))
            result.analyseWrappedClass();
        ClassUtil.makeReference(clazz.getClassLoader(), result);
        return result;
    });

    public static WrapperClassInfo get(Class<? extends WrapperObject> clazz)
    {
        return cache.get(clazz);
    }

    public static Class<?>[] toUnwrappedClasses(Class<?>[] classes)
    {
        Class<?>[] result = new Class[classes.length];
        for(int i = 0; i < classes.length; i++)
        {
            if(WrapperObject.class.isAssignableFrom(classes[i]))
            {
                if(ElementSwitcher.isEnabled(classes[i]))
                    result[i] = WrapperClassInfo.get(RuntimeUtil.cast(classes[i])).getWrappedClass();
            }
            else
                result[i] = classes[i];
        }
        return result;
    }

    public Map<Method, Member> wrappedMembers;
    public Map<Method, Member> inheritableWrappedMembers;
    public synchronized Map<Method, Member> getWrappedMembers()
    {
        if(this.wrappedMembers == null)
            this.analyseWrappedMembers();
        return this.wrappedMembers;
    }
    public synchronized Map<Method, Member> getInheritableWrappedMembers()
    {
        if(this.inheritableWrappedMembers == null)
            this.analyseWrappedMembers();
        return this.inheritableWrappedMembers;
    }
    public void analyseWrappedMembers()
    {
        try
        {
            this.wrappedMembers = new ConcurrentHashMap<>();
            this.inheritableWrappedMembers = new ConcurrentHashMap<>();
            for(Method i : this.getWrapperClass().getMethods())
            {
                Pair<Member, Boolean> result = analyseWrappedMember(i);
                if(result == null)
                    continue;
                this.wrappedMembers.put(i, result.getFirst());
                if(result.getSecond())
                    this.inheritableWrappedMembers.put(i, result.getFirst());
            }
        }
        catch(Throwable e)
        {
            throw new IllegalStateException(
                "Failed to analyze wrapped members, of Wrapper: " + this.getWrapperClass(), e);
        }
    }
    Pair<Member, Boolean> analyseWrappedMember(Method method)
    {
        if(!Modifier.isAbstract(method.getModifiers()) || !ElementSwitcher.isEnabled(method))
            return null;
        if(method.isBridge() || method.isSynthetic())
            return null;
        Class<?> returnType = method.getReturnType();
        if(WrapperObject.class.isAssignableFrom(returnType))
        {
            returnType = WrapperClassInfo.get(RuntimeUtil.cast(returnType)).getWrappedClass();
        }
        Class<?>[] argTypes = toUnwrappedClasses(method.getParameterTypes());
        Exception lastException1 = null;
        for(Annotation j : method.getDeclaredAnnotations())
        {
            WrappedMemberFinderClass finder = j.annotationType()
                .getDeclaredAnnotation(WrappedMemberFinderClass.class);
            if(finder != null && (method.getDeclaringClass() == this.getWrapperClass() || !finder.inheritable()))
            {
                try
                {
                    Member m = finder.value().newInstance().find(
                        this.getWrapperClass(), this.wrappedClass, method, RuntimeUtil.cast(j), returnType,
                        argTypes
                    );
                    if(m != null)
                        return Pair.of(m, finder.inheritable() && !(m instanceof Constructor));
                }
                catch(NoSuchMethodException | NoSuchFieldException | InstantiationException | IllegalAccessException e)
                {
                    lastException1 = e;
                }
            }
        }
        if(lastException1 != null)
        {
            //noinspection UnnecessaryInitCause
            throw RuntimeUtil.sneakilyThrow(
                new NoSuchElementException("Of wrapper: " + method).initCause(lastException1));
        }

        if(Modifier.isAbstract(method.getModifiers()) && ElementSwitcher.isEnabled(method) &&
            method.getDeclaringClass() != this.getWrapperClass() &&
            WrapperObject.class.isAssignableFrom(method.getDeclaringClass()))
        {
            Member tar = WrapperClassInfo.get(RuntimeUtil.cast(method.getDeclaringClass()))
                .getInheritableWrappedMembers().get(method);
            if(tar != null)
                return Pair.of(tar, true);
        }
        return null;
    }

    public MethodHandle constructorCache = null;

    public synchronized MethodHandle getConstructor()
    {
        if(this.constructorCache == null)
            genAClassAndPhuckTheJvm();
        return this.constructorCache;
    }

    public boolean hasAccessTo(Class<?> klass)
    {
        if(!Modifier.isPublic(klass.getModifiers()))
            return false;
        if(RuntimeUtil.jvmVersion >= 9 && this.getWrapperClass() != WrapperClass.class &&
            this.getWrapperClass() != WrapperModuleJ9.class)
        {
            //noinspection RedundantIfStatement
            if(WrapperClass.FACTORY.create(Object.class).getModuleJ9().getWrapped() !=
                WrapperClass.FACTORY.create(klass).getModuleJ9().getWrapped() &&
                !WrapperClass.FACTORY.create(klass).getModuleJ9().isOpen(
                    klass.getPackage() == null ? "" : klass.getPackage().getName(),
                    WrapperClass.FACTORY.create(this.getWrapperClass()).getModuleJ9()
                ))
                return false;
        }
        return true;
    }

    void genAClassAndPhuckTheJvm()
    {
        try
        {
            List<ThrowableConsumer<Class<?>, Throwable>> tasksLater = new ArrayList<>();
            ClassNode cn = new ClassNode();
            cn.visit(
                Opcodes.V1_8, Opcodes.ACC_PUBLIC, AsmUtil.getType(this.getWrapperClass()) + "$0WrapperImpl", null,
                AsmUtil.getType(AbsWrapper.class), new String[]{ AsmUtil.getType(getWrapperClass()) }
            );
            MethodNode mn = new MethodNode(
                Opcodes.ACC_PUBLIC, "<init>", AsmUtil.getDesc(void.class, Object.class), null, new String[0]);
            mn.instructions.add(AsmUtil.insnVarLoad(getWrapperClass(), 0));
            mn.instructions.add(AsmUtil.insnVarLoad(Object.class, 1));
            mn.instructions.add(
                new MethodInsnNode(Opcodes.INVOKESPECIAL, AsmUtil.getType(AbsWrapper.class), mn.name, mn.desc, false));
            mn.instructions.add(AsmUtil.insnReturn(void.class));
            mn.visitEnd();
            cn.methods.add(mn);
            for(Method m : this.getWrapperClass().getMethods())
            {
                if(!ElementSwitcher.isEnabled(m))
                    continue;
                if(m.isSynthetic() || m.isBridge())
                    continue;
                if(m.getName().equals("getWrapped") && m.getParameterCount() == 0 &&
                    !Modifier.isStatic(m.getModifiers()))
                {
                    mn = new MethodNode(Opcodes.ACC_PUBLIC, m.getName(), AsmUtil.getDesc(m), null, new String[0]);
                    mn.instructions.add(AsmUtil.insnVarLoad(WrapperObject.class, 0));
                    mn.visitMethodInsn(
                        Opcodes.INVOKESPECIAL, AsmUtil.getType(AbsWrapper.class), m.getName(),
                        AsmUtil.getDesc(Object.class, new Class[0]), false
                    );
                    mn.instructions.add(AsmUtil.insnCast(m.getReturnType(), Object.class));
                    mn.instructions.add(AsmUtil.insnReturn(m.getReturnType()));
                    mn.visitEnd();
                    cn.methods.add(mn);
                }
                SpecificImpl specificImpl = m.getDeclaredAnnotation(SpecificImpl.class);
                if(specificImpl != null)
                {
                    Class<?>[] pts = m.getParameterTypes();
                    Method target = this.getWrapperClass().getMethod(specificImpl.value(), pts);
                    if(target.getDeclaringClass().isAssignableFrom(m.getDeclaringClass()))
                    {
                        if(AsmUtil.getMethodNode(cn, target.getName(), AsmUtil.getDesc(target)) != null)
                            throw new IllegalStateException("Multiple implementations for method: " + target); // TODO: allow override
                        mn = new MethodNode(
                            Opcodes.ACC_PUBLIC, target.getName(), AsmUtil.getDesc(target), null, new String[0]);
                        mn.instructions.add(AsmUtil.insnVarLoad(WrapperObject.class, 0));
                        for(int i = 0, j = 1; i < pts.length; i++)
                        {
                            mn.instructions.add(AsmUtil.insnVarLoad(pts[i], j));
                            j += AsmUtil.getCategory(pts[i]);
                        }
                        mn.visitMethodInsn(
                            Opcodes.INVOKEINTERFACE, AsmUtil.getType(getWrapperClass()), m.getName(),
                            AsmUtil.getDesc(m),
                            true
                        );
                        mn.instructions.add(AsmUtil.insnReturn(target.getReturnType()));
                        mn.visitEnd();
                        cn.methods.add(mn);
                    }
                }
            }
            mn = new MethodNode(
                Opcodes.ACC_PUBLIC, "static$getWrappedClass", AsmUtil.getDesc(Class.class, new Class[0]), null,
                new String[0]
            );
            if(this.hasAccessTo(this.getWrappedClass()))
                mn.instructions.add(AsmUtil.insnConst(this.getWrappedClass()));
            else
            {
                String fieldName = "0wrappedClass";
                cn.visitField(
                        Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, fieldName, AsmUtil.getDesc(Class.class), null, null)
                    .visitEnd();
                mn.visitFieldInsn(Opcodes.GETSTATIC, cn.name, fieldName, AsmUtil.getDesc(Class.class));
                tasksLater.add(c -> c.getDeclaredField(fieldName).set(null, this.getWrappedClass()));
            }
            mn.instructions.add(AsmUtil.insnReturn(Class.class));
            cn.methods.add(mn);
            //noinspection RedundantArrayCreation
            mn = new MethodNode(
                Opcodes.ACC_PUBLIC, "static$create", AsmUtil.getDesc(WrapperObject.class, new Class[]{ Object.class }),
                null, new String[0]
            );
            mn.visitTypeInsn(Opcodes.NEW, cn.name);
            mn.instructions.add(AsmUtil.insnDup(WrapperObject.class));
            mn.instructions.add(AsmUtil.insnVarLoad(Object.class, 1));
            mn.visitMethodInsn(
                Opcodes.INVOKESPECIAL, cn.name, "<init>", AsmUtil.getDesc(void.class, Object.class), false);
            mn.instructions.add(AsmUtil.insnReturn(WrapperObject.class));
            cn.methods.add(mn);
            for(Map.Entry<Method, Member> i : this.getWrappedMembers().entrySet())
            {
                boolean accessible = Modifier.isPublic(i.getValue().getModifiers()) &&
                    this.hasAccessTo(i.getValue().getDeclaringClass());
                Class<?>[] pts = i.getKey().getParameterTypes();
                mn = new MethodNode(
                    Opcodes.ACC_PUBLIC, i.getKey().getName(), AsmUtil.getDesc(i.getKey()), null, new String[0]);
                if(i.getValue() instanceof Constructor)
                {
                    Class<?>[] ptsTar = ((Constructor<?>) i.getValue()).getParameterTypes();
                    for(Class<?> j : ptsTar)
                    {
                        accessible = accessible && this.hasAccessTo(j);
                    }
                    if(accessible)
                    {
                        mn.instructions.add(
                            new TypeInsnNode(Opcodes.NEW, AsmUtil.getType(i.getValue().getDeclaringClass())));
                        mn.instructions.add(AsmUtil.insnDup(i.getValue().getDeclaringClass()));
                        for(int j = 0, k = 1; j < pts.length; j++)
                        {
                            mn.instructions.add(AsmUtil.insnVarLoad(pts[j], k));
                            if(WrapperObject.class.isAssignableFrom(pts[j]))
                            {
                                mn.instructions.add(AsmUtil.insnGetWrapped());
                                mn.instructions.add(AsmUtil.insnCast(ptsTar[j], Object.class));
                            }
                            else
                            {
                                mn.instructions.add(AsmUtil.insnCast(ptsTar[j], pts[j]));
                            }
                            k += AsmUtil.getCategory(pts[j]);
                        }
                        mn.instructions.add(new MethodInsnNode(
                            Opcodes.INVOKESPECIAL, AsmUtil.getType(i.getValue().getDeclaringClass()), "<init>",
                            AsmUtil.getDesc((Constructor<?>) i.getValue())
                        ));
                    }
                    else
                    {
                        for(int j = 0, k = 1; j < pts.length; j++)
                        {
                            mn.instructions.add(AsmUtil.insnVarLoad(pts[j], k));
                            if(WrapperObject.class.isAssignableFrom(pts[j]))
                            {
                                mn.instructions.add(AsmUtil.insnGetWrapped());
                                ptsTar[j] = Object.class;
                            }
                            else
                            {
                                ptsTar[j] = pts[j];
                            }
                            k += AsmUtil.getCategory(pts[j]);
                        }
                        mn.visitInvokeDynamicInsn(
                            "new", AsmUtil.getDesc(Object.class, ptsTar), new Handle(
                                Opcodes.H_INVOKESTATIC, AsmUtil.getType(ClassUtil.class),
                                "getConstructorCallSiteWithWrapperType", AsmUtil.getDesc(
                                CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class,
                                String.class, MethodType.class
                            ), false
                            ), i.getValue().getDeclaringClass().getName(),
                            Type.getMethodType(AsmUtil.getDesc(i.getKey()))
                        );
                    }
                    mn.instructions.add(AsmUtil.insnCreateWrapper(this.getWrapperClass()));
                    mn.instructions.add(AsmUtil.insnReturn(this.getWrapperClass()));
                }
                else if(i.getValue() instanceof Method)
                {
                    Class<?>[] ptsTar = ((Method) i.getValue()).getParameterTypes();
                    Class<?> rt = ((Method) i.getValue()).getReturnType();
                    for(Class<?> j : ptsTar)
                    {
                        accessible = accessible && this.hasAccessTo(j);
                    }
                    accessible = accessible && this.hasAccessTo(rt);
                    if(accessible)
                    {
                        if(!Modifier.isStatic(i.getValue().getModifiers()))
                        {
                            mn.instructions.add(AsmUtil.insnVarLoad(this.getWrapperClass(), 0));
                            mn.instructions.add(AsmUtil.insnGetWrapped());
                            mn.instructions.add(AsmUtil.insnCast(i.getValue().getDeclaringClass(), Object.class));
                        }
                        for(int j = 0, loc = 1; j < pts.length; j++)
                        {
                            mn.instructions.add(AsmUtil.insnVarLoad(pts[j], loc));
                            if(WrapperObject.class.isAssignableFrom(pts[j]))
                            {
                                mn.instructions.add(AsmUtil.insnGetWrapped());
                                pts[j] = Object.class;
                            }
                            loc += AsmUtil.getCategory(pts[j]);
                            mn.instructions.add(AsmUtil.insnCast(ptsTar[j], pts[j]));
                        }
                        mn.instructions.add(new MethodInsnNode(
                            Modifier.isStatic(i.getValue().getModifiers()) ?
                                Opcodes.INVOKESTATIC :
                                Modifier.isInterface(i.getValue().getDeclaringClass().getModifiers()) ?
                                    Opcodes.INVOKEINTERFACE :
                                    Opcodes.INVOKEVIRTUAL, AsmUtil.getType(i.getValue().getDeclaringClass()),
                            i.getValue().getName(), AsmUtil.getDesc((Method) i.getValue()),
                            Modifier.isInterface(i.getValue().getDeclaringClass().getModifiers())
                        ));
                    }
                    else
                    {
                        if(!Modifier.isStatic(i.getValue().getModifiers()))
                        {
                            mn.instructions.add(AsmUtil.insnVarLoad(this.getWrapperClass(), 0));
                            mn.instructions.add(AsmUtil.insnGetWrapped());
                        }
                        for(int j = 0, loc = 1; j < pts.length; j++)
                        {
                            mn.instructions.add(AsmUtil.insnVarLoad(pts[j], loc));
                            loc += AsmUtil.getCategory(pts[j]);
                            if(WrapperObject.class.isAssignableFrom(pts[j]))
                            {
                                mn.instructions.add(AsmUtil.insnGetWrapped());
                                ptsTar[j] = Object.class;
                            }
                            else
                                ptsTar[j] = pts[j];
                        }
                        if(!Modifier.isStatic(i.getValue().getModifiers()))
                            ptsTar = CollectionUtil.addAll(CollectionUtil.newArrayList(Object.class), ptsTar)
                                .toArray(new Class[0]);
                        mn.visitInvokeDynamicInsn(
                            i.getValue().getName(), AsmUtil.getDesc(rt, ptsTar), new Handle(
                                Opcodes.H_INVOKESTATIC, AsmUtil.getType(ClassUtil.class),
                                "getMethodCallSiteWithWrapperType", AsmUtil.getDesc(
                                CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class,
                                String.class, MethodType.class, int.class
                            ), false
                            ), i.getValue().getDeclaringClass().getName(), Type.getMethodType(
                                AsmUtil.getDesc(
                                    ((Method) i.getValue()).getReturnType(),
                                    i.getKey().getParameterTypes()
                                )), Modifier.isStatic(i.getValue().getModifiers()) ? 1 : 0
                        );
                    }
                    if(WrapperObject.class.isAssignableFrom(i.getKey().getReturnType()))
                    {
                        mn.instructions.add(AsmUtil.insnCast(Object.class, rt));
                        mn.instructions.add(AsmUtil.insnCreateWrapper(
                            RuntimeUtil.<Class<WrapperObject>>cast(i.getKey().getReturnType())));
                    }
                    else
                    {
                        mn.instructions.add(AsmUtil.insnCast(i.getKey().getReturnType(), rt));
                    }
                    mn.instructions.add(AsmUtil.insnReturn(i.getKey().getReturnType()));
                }
                else if(i.getValue() instanceof Field)
                {
                    Class<?> type = ((Field) i.getValue()).getType();
                    accessible = accessible && this.hasAccessTo(type);
                    switch(pts.length)
                    {
                        case 0:
                            if(accessible)
                            {
                                if(Modifier.isStatic(i.getValue().getModifiers()))
                                {
                                    mn.instructions.add(new FieldInsnNode(
                                        Opcodes.GETSTATIC, AsmUtil.getType(i.getValue().getDeclaringClass()),
                                        i.getValue().getName(), AsmUtil.getDesc(type)
                                    ));
                                }
                                else
                                {
                                    mn.instructions.add(AsmUtil.insnVarLoad(getWrapperClass(), 0));
                                    mn.instructions.add(AsmUtil.insnGetWrapped());
                                    mn.instructions.add(
                                        AsmUtil.insnCast(i.getValue().getDeclaringClass(), Object.class));
                                    mn.instructions.add(new FieldInsnNode(
                                        Opcodes.GETFIELD, AsmUtil.getType(i.getValue().getDeclaringClass()),
                                        i.getValue().getName(), AsmUtil.getDesc(type)
                                    ));
                                }
                            }
                            else
                            {
                                if(Modifier.isStatic(i.getValue().getModifiers()))
                                    mn.visitInvokeDynamicInsn(
                                        i.getValue().getName(),
                                        AsmUtil.getDesc(type.isPrimitive() ? type : Object.class, new Class[0]),
                                        new Handle(
                                            Opcodes.H_INVOKESTATIC, AsmUtil.getType(ClassUtil.class),
                                            "getFieldGetterCallSite", AsmUtil.getDesc(
                                            CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class,
                                            String.class
                                        ), false
                                        ), i.getValue().getDeclaringClass().getName()
                                    );
                                else
                                {
                                    mn.instructions.add(AsmUtil.insnVarLoad(getWrapperClass(), 0));
                                    mn.instructions.add(AsmUtil.insnGetWrapped());
                                    mn.visitInvokeDynamicInsn(
                                        i.getValue().getName(),
                                        AsmUtil.getDesc(type.isPrimitive() ? type : Object.class, Object.class),
                                        new Handle(
                                            Opcodes.H_INVOKESTATIC, AsmUtil.getType(ClassUtil.class),
                                            "getFieldGetterCallSite", AsmUtil.getDesc(
                                            CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class,
                                            String.class
                                        ), false
                                        ), i.getValue().getDeclaringClass().getName()
                                    );
                                }
                            }
                            if(WrapperObject.class.isAssignableFrom(i.getKey().getReturnType()))
                            {
                                mn.instructions.add(AsmUtil.insnCast(Object.class, ClassUtil.baseType(type)));
                                mn.instructions.add(AsmUtil.insnCreateWrapper(
                                    RuntimeUtil.<Class<WrapperObject>>cast(i.getKey().getReturnType())));
                            }
                            else
                                mn.instructions.add(
                                    AsmUtil.insnCast(i.getKey().getReturnType(), ClassUtil.baseType(type)));
                            mn.instructions.add(AsmUtil.insnReturn(i.getKey().getReturnType()));
                            break;
                        case 1:
                            Class<?> inputType = i.getKey().getParameterTypes()[0];
                            mn.instructions.add(AsmUtil.insnVarLoad(inputType, 1));
                            if(WrapperObject.class.isAssignableFrom(inputType))
                            {
                                mn.instructions.add(AsmUtil.insnGetWrapped());
                                inputType = Object.class;
                            }
                            if(accessible && !Modifier.isFinal(i.getValue().getModifiers()))
                            {
                                mn.instructions.add(AsmUtil.insnCast(type, inputType));
                                if(Modifier.isStatic(i.getValue().getModifiers()))
                                {
                                    mn.instructions.add(new FieldInsnNode(
                                        Opcodes.PUTSTATIC, AsmUtil.getType(i.getValue().getDeclaringClass()),
                                        i.getValue().getName(), AsmUtil.getDesc(type)
                                    ));
                                }
                                else
                                {
                                    mn.instructions.add(AsmUtil.insnVarLoad(getWrapperClass(), 0));
                                    mn.instructions.add(AsmUtil.insnGetWrapped());
                                    mn.instructions.add(
                                        AsmUtil.insnCast(i.getValue().getDeclaringClass(), Object.class));
                                    mn.instructions.add(AsmUtil.insnSwap(i.getValue().getDeclaringClass(), type));
                                    mn.instructions.add(new FieldInsnNode(
                                        Opcodes.PUTFIELD, AsmUtil.getType(i.getValue().getDeclaringClass()),
                                        i.getValue().getName(), AsmUtil.getDesc(type)
                                    ));
                                }
                            }
                            else
                            {
                                if(Modifier.isStatic(i.getValue().getModifiers()))
                                {
                                    MethodType mt = MethodType.methodType(void.class, inputType);
                                    mn.visitInvokeDynamicInsn(
                                        i.getValue().getName(), AsmUtil.getDesc(mt), new Handle(
                                            Opcodes.H_INVOKESTATIC, AsmUtil.getType(ClassUtil.class),
                                            "getFieldSetterCallSite", AsmUtil.getDesc(
                                            CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class,
                                            String.class
                                        ), false
                                        ), i.getValue().getDeclaringClass().getName()
                                    );
                                }
                                else
                                {
                                    mn.instructions.add(AsmUtil.insnVarLoad(getWrapperClass(), 0));
                                    mn.instructions.add(AsmUtil.insnGetWrapped());
                                    mn.instructions.add(AsmUtil.insnSwap(getWrapperClass(), inputType));
                                    MethodType mt = MethodType.methodType(void.class, Object.class, inputType);
                                    mn.visitInvokeDynamicInsn(
                                        i.getValue().getName(), AsmUtil.getDesc(mt), new Handle(
                                            Opcodes.H_INVOKESTATIC, AsmUtil.getType(ClassUtil.class),
                                            "getFieldSetterCallSite", AsmUtil.getDesc(
                                            CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class,
                                            String.class
                                        ), false
                                        ), i.getValue().getDeclaringClass().getName()
                                    );
                                }
                            }
                            mn.instructions.add(AsmUtil.insnReturn(void.class));
                            break;
                        default:
                            throw new AssertionError();
                    }
                }
                else
                    throw new UnsupportedOperationException(Objects.toString(i.getValue()));
                mn.visitEnd();
                cn.methods.add(mn);
            }
            Set<Pair<String, MethodType>> callOnceMethods = new HashSet<>();
            ClassUtil.forEachSuperUnique(
                this.getWrapperClass(), c ->
                {
                    for(Method m : c.getDeclaredMethods())
                    {
                        if(!m.isAnnotationPresent(CallOnce.class))
                            continue;
                        if(Modifier.isStatic(m.getModifiers()) || m.getReturnType() != void.class)
                            throw new IllegalStateException(
                                "@CallOnce method must be non-static and return void: " + m);
                        callOnceMethods.add(
                            new Pair<>(m.getName(), MethodType.methodType(m.getReturnType(), m.getParameterTypes())));
                    }
                }
            );
            for(Pair<String, MethodType> i : callOnceMethods)
            {
                mn = new MethodNode(
                    Opcodes.ACC_PUBLIC, i.getFirst(), AsmUtil.getDesc(i.getSecond()), null, new String[0]);
                final MethodNode finalMn = mn;
                ClassUtil.forEachSuperTopology(
                    this.getWrapperClass(), c ->
                    {
                        try
                        {
                            if(Modifier.isAbstract(
                                c.getDeclaredMethod(i.getFirst(), i.getSecond().parameterArray()).getModifiers()))
                                return;
                        }
                        catch(NoSuchMethodException ignored)
                        {
                            return;
                        }
                        finalMn.instructions.add(AsmUtil.insnVarLoad(c, 0));
                        for(int j = 0, k = 1; j < i.getSecond().parameterCount(); j++)
                        {
                            finalMn.instructions.add(AsmUtil.insnVarLoad(i.getSecond().parameterType(j), k));
                            k += AsmUtil.getCategory(i.getSecond().parameterType(j));
                        }
                        finalMn.visitInvokeDynamicInsn(
                            i.getFirst(), AsmUtil.getDesc(i.getSecond().insertParameterTypes(0, c)), new Handle(
                                Opcodes.H_INVOKESTATIC, AsmUtil.getType(ClassUtil.class), "getMethodSpecialCallSite",
                                AsmUtil.getDesc(
                                    CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class,
                                    String.class, MethodType.class
                                ), false
                            ), c.getName(), Type.getMethodType(AsmUtil.getDesc(i.getSecond()))
                        );
                    }
                );
                mn.instructions.add(AsmUtil.insnReturn(void.class));
                cn.methods.add(mn);
            }
            this.wrapperClassAnnotation.annotationType().getDeclaredAnnotation(WrappedClassFinderClass.class).value()
                .newInstance().extra(RuntimeUtil.cast(this.wrapperClassAnnotation), cn);
            cn.visitEnd();
            ClassWriter cw = new ClassWriter(wrapperClass.getClassLoader());
            cn.accept(cw);
            Class<?> c = ClassUtil.defineClass(
                new SimpleClassLoader(this.wrapperClass.getClassLoader()), cn.name, cw.toByteArray());
            for(ThrowableConsumer<Class<?>, Throwable> task : tasksLater)
            {
                task.acceptOrThrow(c);
            }
            try
            {
                constructorCache = ClassUtil.unreflect(c.getDeclaredConstructor(Object.class))
                    .asType(MethodType.methodType(WrapperObject.class, Object.class));
            }
            catch(VerifyError e)
            {
                try(FileOutputStream fos = new FileOutputStream("test" + UUID.randomUUID() + ".class"))
                {
                    fos.write(cw.toByteArray());
                }
                catch(Throwable e1)
                {
                    throw RuntimeUtil.sneakilyThrow(e1);
                }
                throw e;
            }

            cn = new ClassNode();
            new ClassReader(ClassUtil.getByteCode(wrapperClass)).accept(cn, 0);
            for(Method m : wrapperClass.getDeclaredMethods())
            {
                //noinspection deprecation
                if(!m.isAnnotationPresent(WrapperCreator.class))
                    continue;
                mn = AsmUtil.getMethodNode(cn, m.getName(), AsmUtil.getDesc(m));
                mn.instructions = new InsnList();
                mn.instructions.add(AsmUtil.insnVarLoad(Object.class, 0));
                mn.instructions.add(AsmUtil.insnCreateWrapper(wrapperClass));
                mn.instructions.add(AsmUtil.insnCast(m.getReturnType(), wrapperClass));
                mn.instructions.add(AsmUtil.insnReturn(m.getReturnType()));
                mn.visitEnd();
            }
            cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);
            ClassUtil.defineClass(wrapperClass.getClassLoader(), cn.name, cw.toByteArray());
        }
        catch(Throwable e)
        {
            throw new RuntimeException("Of wrapper " + this.getWrapperClass().getName(), e);
        }
    }
}
