package mz.mzlib.util.asm;

import mz.mzlib.asm.Handle;
import mz.mzlib.asm.Opcodes;
import mz.mzlib.asm.Type;
import mz.mzlib.asm.tree.*;
import mz.mzlib.util.ClassUtil;
import mz.mzlib.util.CollectionUtil;
import mz.mzlib.util.PublicValues;
import mz.mzlib.util.RuntimeUtil;
import mz.mzlib.util.wrapper.AbsWrapper;
import mz.mzlib.util.wrapper.WrapperClassInfo;
import mz.mzlib.util.wrapper.WrapperFactory;
import mz.mzlib.util.wrapper.WrapperObject;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class AsmUtil
{
    public static HashSet<Integer> opcodesVarLoad = CollectionUtil.addAll(
        new HashSet<>(), Opcodes.ALOAD, Opcodes.LLOAD, Opcodes.DLOAD, Opcodes.FLOAD, Opcodes.ILOAD);
    public static HashSet<Integer> opcodesVarStore = CollectionUtil.addAll(
        new HashSet<>(), Opcodes.ASTORE, Opcodes.LSTORE, Opcodes.DSTORE, Opcodes.FSTORE, Opcodes.ISTORE);

    public static Class<?> getClass(String desc, ClassLoader cl) throws ClassNotFoundException
    {
        switch(desc.charAt(0))
        {
            case 'V':
                return void.class;
            case 'B':
                return byte.class;
            case 'S':
                return short.class;
            case 'I':
                return int.class;
            case 'J':
                return long.class;
            case 'F':
                return float.class;
            case 'D':
                return double.class;
            case 'Z':
                return boolean.class;
            case 'C':
                return char.class;
            case 'L':
                return ClassUtil.classForName(desc.substring(1, desc.length() - 1).replace('/', '.'), cl);
            case '[':
                return ClassUtil.classForName(desc.replace('/', '.'), cl);
            default:
                throw new ClassNotFoundException(desc);
        }
    }

    public static FieldNode getFieldNode(ClassNode cn, String name)
    {
        for(FieldNode f : cn.fields)
        {
            if(f.name.equals(name))
            {
                return f;
            }
        }
        return null;
    }

    public static MethodNode getMethodNode(ClassNode clazz, String name, String desc)
    {
        for(MethodNode m : clazz.methods)
        {
            if(Objects.equals(m.name, name) && Objects.equals(m.desc, desc))
                return m;
        }
        return RuntimeUtil.nul();
    }

    public static boolean equals(AbstractInsnNode a, AbstractInsnNode b)
    {
        if(a == b)
            return true;
        if(a == null || b == null)
            return false;
        if(a.getClass() != b.getClass())
            return false;
        if(a.getOpcode() != b.getOpcode())
            return false;
        if(a instanceof InsnNode)
            return true;
        if(a instanceof FieldInsnNode)
        {
            FieldInsnNode a1 = (FieldInsnNode) a, b1 = (FieldInsnNode) b;
            return Objects.equals(a1.owner, b1.owner) && Objects.equals(a1.name, b1.name) &&
                Objects.equals(a1.desc, b1.desc);
        }
        else if(a instanceof MethodInsnNode)
        {
            MethodInsnNode a1 = (MethodInsnNode) a, b1 = (MethodInsnNode) b;
            return Objects.equals(a1.owner, b1.owner) && Objects.equals(a1.name, b1.name) &&
                Objects.equals(a1.desc, b1.desc) && Objects.equals(a1.itf, b1.itf);
        }
        else if(a instanceof TypeInsnNode)
        {
            TypeInsnNode a1 = (TypeInsnNode) a, b1 = (TypeInsnNode) b;
            return Objects.equals(a1.desc, b1.desc);
        }
        else if(a instanceof VarInsnNode)
        {
            VarInsnNode a1 = (VarInsnNode) a, b1 = (VarInsnNode) b;
            return a1.var == b1.var;
        }
        else if(a instanceof LdcInsnNode)
        {
            LdcInsnNode a1 = (LdcInsnNode) a, b1 = (LdcInsnNode) b;
            return Objects.equals(a1.cst, b1.cst);
        }
        else if(a instanceof FrameNode)
        {
            FrameNode a1 = (FrameNode) a, b1 = (FrameNode) b;
            return a1.type == b1.type && Objects.equals(a1.local, b1.local) && Objects.equals(a1.stack, b1.stack);
        }
        else if(a instanceof IincInsnNode)
        {
            IincInsnNode a1 = (IincInsnNode) a, b1 = (IincInsnNode) b;
            return a1.var == b1.var && a1.incr == b1.incr;
        }
        else if(a instanceof IntInsnNode)
        {
            IntInsnNode a1 = (IntInsnNode) a, b1 = (IntInsnNode) b;
            return a1.operand == b1.operand;
        }
        else if(a instanceof InvokeDynamicInsnNode)
        {
            InvokeDynamicInsnNode a1 = (InvokeDynamicInsnNode) a, b1 = (InvokeDynamicInsnNode) b;
            return Objects.equals(a1.name, b1.name) && Objects.equals(a1.desc, b1.desc) &&
                Objects.equals(a1.bsm, b1.bsm) && Arrays.equals(a1.bsmArgs, b1.bsmArgs);
        }
        else if(a instanceof JumpInsnNode)
        {
            JumpInsnNode a1 = (JumpInsnNode) a, b1 = (JumpInsnNode) b;
            return a1.label.getLabel() == b1.label.getLabel();
        }
        else if(a instanceof LabelNode)
        {
            LabelNode a1 = (LabelNode) a, b1 = (LabelNode) b;
            return a1.getLabel() == b1.getLabel();
        }
        else if(a instanceof LineNumberNode)
        {
            LineNumberNode a1 = (LineNumberNode) a, b1 = (LineNumberNode) b;
            return a1.line == b1.line && a1.start.getLabel() == b1.start.getLabel();
        }
        else if(a instanceof LookupSwitchInsnNode)
        {
            LookupSwitchInsnNode a1 = (LookupSwitchInsnNode) a, b1 = (LookupSwitchInsnNode) b;
            return a1.dflt == b1.dflt && Objects.equals(a1.keys, b1.keys) && Objects.equals(a1.labels, b1.labels);
        }
        else if(a instanceof MultiANewArrayInsnNode)
        {
            MultiANewArrayInsnNode a1 = (MultiANewArrayInsnNode) a, b1 = (MultiANewArrayInsnNode) b;
            return Objects.equals(a1.desc, b1.desc) && a1.dims == b1.dims;
        }
        else if(a instanceof TableSwitchInsnNode)
        {
            TableSwitchInsnNode a1 = (TableSwitchInsnNode) a, b1 = (TableSwitchInsnNode) b;
            return a1.min == b1.min && a1.max == b1.max && equals(a1.dflt, b1.dflt) &&
                Objects.equals(a1.labels, b1.labels);
        }
        else
            throw new UnsupportedOperationException();
    }

    public static InsnList insnSwap(Class<?> stackTop, Class<?> belowTop)
    {
        InsnList result = new InsnList();
        if(getCategory(stackTop) == 1)
        {
            if(getCategory(belowTop) == 1)
            {
                result.add(new InsnNode(Opcodes.SWAP));
            }
            else
            {
                result.add(new InsnNode(Opcodes.DUP_X2));
                result.add(new InsnNode(Opcodes.POP));
            }
        }
        else
        {
            if(getCategory(belowTop) == 1)
            {
                result.add(new InsnNode(Opcodes.DUP2_X1));
            }
            else
            {
                result.add(new InsnNode(Opcodes.DUP2_X2));
            }
            result.add(new InsnNode(Opcodes.POP2));
        }
        return result;
    }

    public static AbstractInsnNode insnCreateWrapper(Class<? extends WrapperObject> type)
    {
        return insnCreateWrapper(Type.getType(type));
    }

    public static AbstractInsnNode insnCreateWrapper(Type type)
    {
        return new InvokeDynamicInsnNode(
            "create", Type.getMethodType(type, Type.getType(Object.class)).getDescriptor(),
            new Handle(
                Opcodes.H_INVOKESTATIC, getType(WrapperObject.class), "getConstructorCallSite",
                getDesc(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, Class.class), true
            ), type
        );
    }
    public static AbstractInsnNode insnWrapperFactory(Class<? extends WrapperObject> type)
    {
        return insnWrapperFactory(Type.getType(type));
    }
    public static AbstractInsnNode insnWrapperFactory(Type type)
    {
        return new InvokeDynamicInsnNode(
            "factory", AsmUtil.getDesc(MethodType.methodType(WrapperFactory.class)),
            new Handle(
                Opcodes.H_INVOKESTATIC, getType(WrapperObject.class), "callSiteFactory",
                getDesc(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, Class.class), true
            ), type
        );
    }

    public static InsnList insnGetWrapped()
    {
        InsnList result = new InsnList();
        result.add(insnCast(AbsWrapper.class, Object.class));
        result.add(new FieldInsnNode(Opcodes.GETFIELD, getType(AbsWrapper.class), "wrapped", getDesc(Object.class)));
        return result;
    }

    public static VarInsnNode insnVarStore(Class<?> type, int index)
    {
        if(!type.isPrimitive())
        {
            return new VarInsnNode(Opcodes.ASTORE, index);
        }
        else if(type == boolean.class || type == byte.class || type == short.class || type == int.class)
        {
            return new VarInsnNode(Opcodes.ISTORE, index);
        }
        else if(type == long.class)
        {
            return new VarInsnNode(Opcodes.LSTORE, index);
        }
        else if(type == float.class)
        {
            return new VarInsnNode(Opcodes.FSTORE, index);
        }
        else if(type == double.class)
        {
            return new VarInsnNode(Opcodes.DSTORE, index);
        }
        else
        {
            throw new IllegalArgumentException("type: " + type);
        }
    }

    public static VarInsnNode insnVarLoad(Class<?> type, int index)
    {
        if(!type.isPrimitive())
        {
            return new VarInsnNode(Opcodes.ALOAD, index);
        }
        else if(type == boolean.class || type == byte.class || type == short.class || type == int.class ||
            type == char.class)
        {
            return new VarInsnNode(Opcodes.ILOAD, index);
        }
        else if(type == long.class)
        {
            return new VarInsnNode(Opcodes.LLOAD, index);
        }
        else if(type == float.class)
        {
            return new VarInsnNode(Opcodes.FLOAD, index);
        }
        else if(type == double.class)
        {
            return new VarInsnNode(Opcodes.DLOAD, index);
        }
        else
        {
            throw new IllegalArgumentException("type: " + type);
        }
    }

    public static InsnList insnDup(Class<?> type)
    {
        switch(getCategory(type))
        {
            case 0:
                return new InsnList();
            case 1:
                return toList(new InsnNode(Opcodes.DUP));
            case 2:
                return toList(new InsnNode(Opcodes.DUP2));
            default:
                throw new IllegalArgumentException(type + "");
        }
    }

    public static AbstractInsnNode insnConst(Object obj)
    {
        if(obj == null)
        {
            return new InsnNode(Opcodes.ACONST_NULL);
        }
        else if(obj instanceof Boolean)
        {
            return new InsnNode(((Boolean) obj) ? Opcodes.ICONST_1 : Opcodes.ICONST_0);
        }
        else if(obj instanceof Integer || obj instanceof Byte || obj instanceof Short)
        {
            switch(((Number) obj).intValue())
            {
                case -1:
                    return new InsnNode(Opcodes.ICONST_M1);
                case 0:
                    return new InsnNode(Opcodes.ICONST_0);
                case 1:
                    return new InsnNode(Opcodes.ICONST_1);
                case 2:
                    return new InsnNode(Opcodes.ICONST_2);
                case 3:
                    return new InsnNode(Opcodes.ICONST_3);
                case 4:
                    return new InsnNode(Opcodes.ICONST_4);
                case 5:
                    return new InsnNode(Opcodes.ICONST_5);
            }
        }
        else if(obj instanceof Long)
        {
            if((Long) obj == 0)
            {
                return new InsnNode(Opcodes.LCONST_0);
            }
            else if((Long) obj == 1)
            {
                return new InsnNode(Opcodes.LCONST_1);
            }
        }
        else if(obj instanceof Float)
        {
            if((Float) obj == 0)
            {
                return new InsnNode(Opcodes.FCONST_0);
            }
            else if((Float) obj == 1)
            {
                return new InsnNode(Opcodes.FCONST_1);
            }
            else if((Float) obj == 2)
            {
                return new InsnNode(Opcodes.FCONST_2);
            }
        }
        else if(obj instanceof Double)
        {
            if((Double) obj == 0)
            {
                return new InsnNode(Opcodes.DCONST_0);
            }
            else if((Double) obj == 1)
            {
                return new InsnNode(Opcodes.DCONST_1);
            }
        }
        else if(obj instanceof Class)
        {
            return new LdcInsnNode(Type.getType((Class<?>) obj));
        }
        return new LdcInsnNode(obj);
    }

    public static int getCategory(Type type)
    {
        if(type == null)
        {
            return 1;
        }
        switch(type.getSort())
        {
            case Type.VOID:
                return 0;
            case Type.LONG:
            case Type.DOUBLE:
                return 2;
            default:
                return 1;
        }
    }

    public static int getCategory(Class<?> clazz)
    {
        if(clazz == null || clazz == void.class)
        {
            return 0;
        }
        else if(clazz == long.class || clazz == double.class)
        {
            return 2;
        }
        else
        {
            return 1;
        }
    }

    public static InsnList insnArrayLoad(Class<?> type, InsnList index)
    {
        InsnList r = new InsnList();
        r.add(index);
        if(!type.isPrimitive())
        {
            r.add(new InsnNode(Opcodes.AALOAD));
        }
        else if(type == byte.class)
        {
            r.add(new InsnNode(Opcodes.BALOAD));
        }
        else if(type == short.class)
        {
            r.add(new InsnNode(Opcodes.SALOAD));
        }
        else if(type == int.class)
        {
            r.add(new InsnNode(Opcodes.IALOAD));
        }
        else if(type == long.class)
        {
            r.add(new InsnNode(Opcodes.LALOAD));
        }
        else if(type == float.class)
        {
            r.add(new InsnNode(Opcodes.FALOAD));
        }
        else if(type == double.class)
        {
            r.add(new InsnNode(Opcodes.DALOAD));
        }
        else if(type == boolean.class)
        {
            r.add(new InsnNode(Opcodes.BALOAD));
        }
        else if(type == char.class)
        {
            r.add(new InsnNode(Opcodes.CALOAD));
        }
        return r;
    }

    public static InsnList insnArrayStore(Class<?> type, InsnList index, InsnList value)
    {
        InsnList r = new InsnList();
        r.add(index);
        r.add(value);
        if(!type.isPrimitive())
        {
            r.add(new InsnNode(Opcodes.AASTORE));
        }
        else if(type == byte.class)
        {
            r.add(new InsnNode(Opcodes.BASTORE));
        }
        else if(type == short.class)
        {
            r.add(new InsnNode(Opcodes.SASTORE));
        }
        else if(type == int.class)
        {
            r.add(new InsnNode(Opcodes.IASTORE));
        }
        else if(type == long.class)
        {
            r.add(new InsnNode(Opcodes.LASTORE));
        }
        else if(type == float.class)
        {
            r.add(new InsnNode(Opcodes.FASTORE));
        }
        else if(type == double.class)
        {
            r.add(new InsnNode(Opcodes.DASTORE));
        }
        else if(type == boolean.class)
        {
            r.add(new InsnNode(Opcodes.BASTORE));
        }
        else if(type == char.class)
        {
            r.add(new InsnNode(Opcodes.CASTORE));
        }
        return r;
    }

    public static InsnList insnArray(Class<?> type, InsnList... elements)
    {
        return insnArray(toList(insnConst(elements.length)), type, elements);
    }

    public static InsnList insnArray(InsnList length, Class<?> type, InsnList... elements)
    {
        InsnList r = new InsnList();
        r.add(length);
        if(type.isPrimitive())
        {
            int t = 0;
            if(type == byte.class)
            {
                t = Opcodes.T_BYTE;
            }
            else if(type == short.class)
            {
                t = Opcodes.T_SHORT;
            }
            else if(type == int.class)
            {
                t = Opcodes.T_INT;
            }
            else if(type == long.class)
            {
                t = Opcodes.T_LONG;
            }
            else if(type == float.class)
            {
                t = Opcodes.T_FLOAT;
            }
            else if(type == double.class)
            {
                t = Opcodes.T_DOUBLE;
            }
            else if(type == boolean.class)
            {
                t = Opcodes.T_BOOLEAN;
            }
            else if(type == char.class)
            {
                t = Opcodes.T_CHAR;
            }
            r.add(new IntInsnNode(Opcodes.NEWARRAY, t));
        }
        else
        {
            r.add(new TypeInsnNode(Opcodes.ANEWARRAY, getType(type)));
        }
        for(int i = 0; i < elements.length; i++)
        {
            r.add(new InsnNode(Opcodes.DUP));
            r.add(insnArrayStore(type, toList(insnConst(i)), elements[i]));
        }
        return r;
    }

    public static AbstractInsnNode insnReturn(Class<?> type)
    {
        if(type.isPrimitive())
        {
            if(type == void.class)
            {
                return new InsnNode(Opcodes.RETURN);
            }
            else if(type == boolean.class || type == char.class || type == byte.class || type == short.class ||
                type == int.class)
            {
                return new InsnNode(Opcodes.IRETURN);
            }
            else if(type == float.class)
            {
                return new InsnNode(Opcodes.FRETURN);
            }
            else if(type == double.class)
            {
                return new InsnNode(Opcodes.DRETURN);
            }
            else if(type == long.class)
            {
                return new InsnNode(Opcodes.LRETURN);
            }
            else
            {
                throw new IllegalArgumentException("type: " + type);
            }
        }
        else
        {
            return new InsnNode(Opcodes.ARETURN);
        }
    }

    public static List<AbstractInsnNode> toList(InsnList nodes)
    {
        return CollectionUtil.newArrayList(nodes.toArray());
    }

    public static InsnList toList(AbstractInsnNode... nodes)
    {
        InsnList r = new InsnList();
        for(AbstractInsnNode node : nodes)
        {
            r.add(node);
        }
        return r;
    }

    public static InsnList mergeList(InsnList... nodes)
    {
        InsnList r = new InsnList();
        for(InsnList ns : nodes)
        {
            r.add(ns);
        }
        return r;
    }

    public static InsnList insnPop(Class<?> type)
    {
        switch(getCategory(type))
        {
            case 0:
                return new InsnList();
            case 1:
                return toList(new InsnNode(Opcodes.POP));
            case 2:
                return toList(new InsnNode(Opcodes.POP2));
        }
        throw new IllegalArgumentException("type: " + type);
    }

    public static InsnList insnCast(Class<?> tar, Class<?> src)
    {
        if(tar.equals(src))
            return new InsnList();
        if(tar.isPrimitive())
        {
            if(tar == void.class)
            {
                return insnPop(src);
            }
            if(src.isPrimitive())
            {
                if(src == boolean.class || src == byte.class || src == short.class || src == int.class)
                {
                    if(tar == boolean.class || tar == byte.class || tar == short.class || tar == int.class)
                    {
                        if(tar == byte.class)
                        {
                            return toList(new InsnNode(Opcodes.I2B));
                        }
                        else if(tar == short.class && src == int.class)
                        {
                            return toList(new InsnNode(Opcodes.I2S));
                        }
                        else
                        {
                            return new InsnList();
                        }
                    }
                    else if(tar == long.class)
                    {
                        return toList(new InsnNode(Opcodes.I2L));
                    }
                    else if(tar == float.class)
                    {
                        return toList(new InsnNode(Opcodes.I2F));
                    }
                    else if(tar == double.class)
                    {
                        return toList(new InsnNode(Opcodes.I2D));
                    }
                    else if(tar == char.class)
                    {
                        return toList(new InsnNode(Opcodes.I2C));
                    }
                }
                else if(src == long.class)
                {
                    if(tar == boolean.class || tar == int.class)
                    {
                        return toList(new InsnNode(Opcodes.L2I));
                    }
                    else if(tar == byte.class)
                    {
                        return toList(new InsnNode(Opcodes.L2I), new InsnNode(Opcodes.I2B));
                    }
                    else if(tar == short.class)
                    {
                        return toList(new InsnNode(Opcodes.L2I), new InsnNode(Opcodes.I2S));
                    }
                    else if(tar == float.class)
                    {
                        return toList(new InsnNode(Opcodes.L2F));
                    }
                    else if(tar == double.class)
                    {
                        return toList(new InsnNode(Opcodes.L2D));
                    }
                    else if(tar == char.class)
                    {
                        return toList(new InsnNode(Opcodes.L2I), new InsnNode(Opcodes.I2C));
                    }
                }
                else if(src == float.class)
                {
                    if(tar == boolean.class || tar == int.class)
                    {
                        return toList(new InsnNode(Opcodes.F2I));
                    }
                    else if(tar == byte.class)
                    {
                        return toList(new InsnNode(Opcodes.F2I), new InsnNode(Opcodes.I2B));
                    }
                    else if(tar == short.class)
                    {
                        return toList(new InsnNode(Opcodes.F2I), new InsnNode(Opcodes.I2S));
                    }
                    else if(tar == long.class)
                    {
                        return toList(new InsnNode(Opcodes.F2L));
                    }
                    else if(tar == double.class)
                    {
                        return toList(new InsnNode(Opcodes.F2D));
                    }
                    else if(tar == char.class)
                    {
                        return toList(new InsnNode(Opcodes.F2I), new InsnNode(Opcodes.I2C));
                    }
                }
                else if(src == double.class)
                {
                    if(tar == boolean.class || tar == int.class)
                    {
                        return toList(new InsnNode(Opcodes.D2I));
                    }
                    else if(tar == byte.class)
                    {
                        return toList(new InsnNode(Opcodes.D2I), new InsnNode(Opcodes.I2B));
                    }
                    else if(tar == short.class)
                    {
                        return toList(new InsnNode(Opcodes.D2I), new InsnNode(Opcodes.I2S));
                    }
                    else if(tar == long.class)
                    {
                        return toList(new InsnNode(Opcodes.D2L));
                    }
                    else if(tar == float.class)
                    {
                        return toList(new InsnNode(Opcodes.D2F));
                    }
                    else if(tar == char.class)
                    {
                        return toList(new InsnNode(Opcodes.D2I), new InsnNode(Opcodes.I2C));
                    }
                }
            }
            else if(ClassUtil.getPrimitive(src).isPrimitive())
            {
                InsnList r = toList(new MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL, getType(src),
                    ClassUtil.getPrimitive(src).getName() + "Value", getDesc(ClassUtil.getPrimitive(src), new Class[0])
                ));
                r.add(insnCast(tar, ClassUtil.getPrimitive(src)));
                return r;
            }
            else
            {
                InsnList r = insnCast(ClassUtil.getWrapper(tar), src);
                r.add(insnCast(tar, ClassUtil.getWrapper(tar)));
                return r;
            }
        }
        else if(src.isPrimitive())
        {
            if(src == void.class)
                return toList(insnConst(null));
            InsnList r = toList(new MethodInsnNode(
                Opcodes.INVOKESTATIC, getType(ClassUtil.getWrapper(src)), "valueOf",
                getDesc(ClassUtil.getWrapper(src), src), false
            ));
            r.add(insnCast(tar, ClassUtil.getWrapper(src)));
            return r;
        }
        else if(tar.isAssignableFrom(src))
        {
            return new InsnList();
        }
        else
        {
            return toList(new TypeInsnNode(Opcodes.CHECKCAST, getType(tar)));
        }
        throw new IllegalArgumentException("tar: " + tar + ", src:" + src);
    }

    public static String getType(String className)
    {
        return className.replace('.', '/');
    }
    public static String getType(Class<?> clazz)
    {
        return getType(clazz.getName());
    }

    public static String getDesc(String type)
    {
        switch(type)
        {
            case "void":
                return "V";
            case "byte":
                return "B";
            case "short":
                return "S";
            case "int":
                return "I";
            case "long":
                return "J";
            case "float":
                return "F";
            case "double":
                return "D";
            case "char":
                return "C";
            case "boolean":
                return "Z";
            default:
                return "L" + type + ";";
        }
    }

    public static String getDesc(Class<?> clazz)
    {
        if(clazz.isArray())
            return "[" + getDesc(clazz.getComponentType());
        else if(!clazz.isPrimitive())
            return "L" + getType(clazz) + ";";
        else if(clazz == void.class)
            return "V";
        else if(clazz == byte.class)
            return "B";
        else if(clazz == short.class)
            return "S";
        else if(clazz == int.class)
            return "I";
        else if(clazz == long.class)
            return "J";
        else if(clazz == float.class)
            return "F";
        else if(clazz == double.class)
            return "D";
        else if(clazz == char.class)
            return "C";
        else if(clazz == boolean.class)
            return "Z";
        else
            throw new IllegalArgumentException(clazz.toString());
    }

    public static String getDesc(Method method)
    {
        return getDesc(ClassUtil.methodType(method));
    }

    public static String getDesc(Constructor<?> constructor)
    {
        return getDesc(ClassUtil.methodType(constructor));
    }
    public static String getDesc(Class<?> retType, Class<?>... argsTypes)
    {
        StringBuilder r = new StringBuilder("(");
        for(Class<?> a : argsTypes)
        {
            r.append(getDesc(a));
        }
        r.append(')');
        r.append(getDesc(retType));
        return r.toString();
    }
    public static String getDesc(MethodType type)
    {
        return getDesc(type.returnType(), type.parameterArray());
    }

    public static String getDesc(Member member)
    {
        if(member instanceof Method || member instanceof Constructor)
            return getDesc(ClassUtil.methodType(member));
        else if(member instanceof Field)
            return getDesc(((Field) member).getType());
        else
            throw new IllegalArgumentException("Unsupported member type: " + member);
    }

    @Deprecated
    public static InsnList insnGetPublicValue(int index)
    {
        InsnList result = new InsnList();
        result.add(AsmUtil.insnConst(index));
        result.add(new MethodInsnNode(
            Opcodes.INVOKESTATIC, AsmUtil.getType(PublicValues.class), "get",
            AsmUtil.getDesc(Object.class, int.class), false
        ));
        return result;
    }

    public static boolean isVisitingWrapped(
        AbstractInsnNode insn,
        Class<? extends WrapperObject> wrapperClass,
        String wrapperMethodName,
        Class<?>... wrapperMethodParams)
    {
        try
        {
            return isVisiting(
                insn, WrapperClassInfo.get(wrapperClass).getWrappedMembers()
                    .get(wrapperClass.getMethod(wrapperMethodName, wrapperMethodParams))
            );
        }
        catch(Throwable e)
        {
            throw RuntimeUtil.sneakilyThrow(e);
        }
    }
    public static boolean isVisiting(AbstractInsnNode insn, Member member)
    {
        if(insn instanceof MethodInsnNode)
        {
            if(member instanceof Method)
            {
                if(Modifier.isStatic(member.getModifiers()))
                {
                    if(insn.getOpcode() != Opcodes.INVOKESTATIC)
                        return false;
                }
                else if(member.getDeclaringClass().isInterface())
                {
                    if(insn.getOpcode() != Opcodes.INVOKEINTERFACE && insn.getOpcode() != Opcodes.INVOKESPECIAL)
                        return false;
                }
                if(!member.getName().equals(((MethodInsnNode) insn).name))
                    return false;
                if(!((MethodInsnNode) insn).owner.equals(AsmUtil.getType(member.getDeclaringClass())))
                    return false;
                return ((MethodInsnNode) insn).desc.equals(AsmUtil.getDesc(member));
            }
            else if(member instanceof Constructor)
            {
                if(insn.getOpcode() != Opcodes.INVOKESPECIAL)
                    return false;
                if(!"<init>".equals(((MethodInsnNode) insn).name))
                    return false;
                if(!((MethodInsnNode) insn).owner.equals(AsmUtil.getType(member.getDeclaringClass())))
                    return false;
                return ((MethodInsnNode) insn).desc.equals(AsmUtil.getDesc(member));
            }
            else
                return false;
        }
        else if(insn instanceof FieldInsnNode)
        {
            if(!(member instanceof Field))
                return false;
            if(!((FieldInsnNode) insn).owner.equals(AsmUtil.getType(member.getDeclaringClass())))
                return false;
            return ((FieldInsnNode) insn).name.equals(member.getName());
        }
        else
            return false;
    }

    public static String toString(AbstractInsnNode insn)
    {
        String r;
        if(insn instanceof FrameNode)
        {
            r = "FRAME";
            r += " ";
            switch(((FrameNode) insn).type)
            {
                case -1:
                    r += "NEW";
                    break;
                case 0:
                    r += "FULL";
                    break;
                case 1:
                    r += "APPEND";
                    break;
                case 2:
                    r += "CHOP";
                    break;
                case 3:
                    r += "SAME";
                    break;
                case 4:
                    r += "SAME1";
                    break;
            }
        }
        else if(insn instanceof LineNumberNode)
        {
            r = "LINENUMBER";
            r += " ";
            r += ((LineNumberNode) insn).line;
        }
        else if(insn instanceof LabelNode)
        {
            r = "LABEL " + ((LabelNode) insn).getLabel().hashCode();
        }
        else
        {
            switch(insn.getOpcode())
            {
                case 0:
                    r = "NOP";
                    break;
                case 1:
                    r = "ACONST_NULL";
                    break;
                case 2:
                    r = "ICONST_M1";
                    break;
                case 3:
                    r = "ICONST_0";
                    break;
                case 4:
                    r = "ICONST_1";
                    break;
                case 5:
                    r = "ICONST_2";
                    break;
                case 6:
                    r = "ICONST_3";
                    break;
                case 7:
                    r = "ICONST_4";
                    break;
                case 8:
                    r = "ICONST_5";
                    break;
                case 9:
                    r = "LCONST_0";
                    break;
                case 10:
                    r = "LCONST_1";
                    break;
                case 11:
                    r = "FCONST_0";
                    break;
                case 12:
                    r = "FCONST_1";
                    break;
                case 13:
                    r = "FCONST_2";
                    break;
                case 14:
                    r = "DCONST_0";
                    break;
                case 15:
                    r = "DCONST_1";
                    break;
                case 16:
                    r = "BIPUSH";
                    break;
                case 17:
                    r = "SIPUSH";
                    break;
                case 18:
                    r = "LDC";
                    break;
                case 21:
                    r = "ILOAD";
                    break;
                case 22:
                    r = "LLOAD";
                    break;
                case 23:
                    r = "FLOAD";
                    break;
                case 24:
                    r = "DLOAD";
                    break;
                case 25:
                    r = "ALOAD";
                    break;
                case 46:
                    r = "IALOAD";
                    break;
                case 47:
                    r = "LALOAD";
                    break;
                case 48:
                    r = "FALOAD";
                    break;
                case 49:
                    r = "DALOAD";
                    break;
                case 50:
                    r = "AALOAD";
                    break;
                case 51:
                    r = "BALOAD";
                    break;
                case 52:
                    r = "CALOAD";
                    break;
                case 53:
                    r = "SALOAD";
                    break;
                case 54:
                    r = "ISTORE";
                    break;
                case 55:
                    r = "LSTORE";
                    break;
                case 56:
                    r = "FSTORE";
                    break;
                case 57:
                    r = "DSTORE";
                    break;
                case 58:
                    r = "ASTORE";
                    break;
                case 79:
                    r = "IASTORE";
                    break;
                case 80:
                    r = "LASTORE";
                    break;
                case 81:
                    r = "FASTORE";
                    break;
                case 82:
                    r = "DASTORE";
                    break;
                case 83:
                    r = "AASTORE";
                    break;
                case 84:
                    r = "BASTORE";
                    break;
                case 85:
                    r = "CASTORE";
                    break;
                case 86:
                    r = "SASTORE";
                    break;
                case 87:
                    r = "POP";
                    break;
                case 88:
                    r = "POP2";
                    break;
                case 89:
                    r = "DUP";
                    break;
                case 90:
                    r = "DUP_X1";
                    break;
                case 91:
                    r = "DUP_X2";
                    break;
                case 92:
                    r = "DUP2";
                    break;
                case 93:
                    r = "DUP2_X1";
                    break;
                case 94:
                    r = "DUP2_X2";
                    break;
                case 95:
                    r = "SWAP";
                    break;
                case 96:
                    r = "IADD";
                    break;
                case 97:
                    r = "LADD";
                    break;
                case 98:
                    r = "FADD";
                    break;
                case 99:
                    r = "DADD";
                    break;
                case 100:
                    r = "ISUB";
                    break;
                case 101:
                    r = "LSUB";
                    break;
                case 102:
                    r = "FSUB";
                    break;
                case 103:
                    r = "DSUB";
                    break;
                case 104:
                    r = "IMUL";
                    break;
                case 105:
                    r = "LMUL";
                    break;
                case 106:
                    r = "FMUL";
                    break;
                case 107:
                    r = "DMUL";
                    break;
                case 108:
                    r = "IDIV";
                    break;
                case 109:
                    r = "LDIV";
                    break;
                case 110:
                    r = "FDIV";
                    break;
                case 111:
                    r = "DDIV";
                    break;
                case 112:
                    r = "IREM";
                    break;
                case 113:
                    r = "LREM";
                    break;
                case 114:
                    r = "FREM";
                    break;
                case 115:
                    r = "DREM";
                    break;
                case 116:
                    r = "INEG";
                    break;
                case 117:
                    r = "LNEG";
                    break;
                case 118:
                    r = "FNEG";
                    break;
                case 119:
                    r = "DNEG";
                    break;
                case 120:
                    r = "ISHL";
                    break;
                case 121:
                    r = "LSHL";
                    break;
                case 122:
                    r = "ISHR";
                    break;
                case 123:
                    r = "LSHR";
                    break;
                case 124:
                    r = "IUSHR";
                    break;
                case 125:
                    r = "LUSHR";
                    break;
                case 126:
                    r = "IAND";
                    break;
                case 127:
                    r = "LAND";
                    break;
                case 128:
                    r = "IOR";
                    break;
                case 129:
                    r = "LOR";
                    break;
                case 130:
                    r = "IXOR";
                    break;
                case 131:
                    r = "LXOR";
                    break;
                case 132:
                    r = "IINC";
                    break;
                case 133:
                    r = "I2L";
                    break;
                case 134:
                    r = "I2F";
                    break;
                case 135:
                    r = "I2D";
                    break;
                case 136:
                    r = "L2I";
                    break;
                case 137:
                    r = "L2F";
                    break;
                case 138:
                    r = "L2D";
                    break;
                case 139:
                    r = "F2I";
                    break;
                case 140:
                    r = "F2L";
                    break;
                case 141:
                    r = "F2D";
                    break;
                case 142:
                    r = "D2I";
                    break;
                case 143:
                    r = "D2L";
                    break;
                case 144:
                    r = "D2F";
                    break;
                case 145:
                    r = "I2B";
                    break;
                case 146:
                    r = "I2C";
                    break;
                case 147:
                    r = "I2S";
                    break;
                case 148:
                    r = "LCMP";
                    break;
                case 149:
                    r = "FCMPL";
                    break;
                case 150:
                    r = "FCMPG";
                    break;
                case 151:
                    r = "DCMPL";
                    break;
                case 152:
                    r = "DCMPG";
                    break;
                case 153:
                    r = "IFEQ";
                    break;
                case 154:
                    r = "IFNE";
                    break;
                case 155:
                    r = "IFLT";
                    break;
                case 156:
                    r = "IFGE";
                    break;
                case 157:
                    r = "IFGT";
                    break;
                case 158:
                    r = "IFLE";
                    break;
                case 159:
                    r = "IF_ICMPEQ";
                    break;
                case 160:
                    r = "IF_ICMPNE";
                    break;
                case 161:
                    r = "IF_ICMPLT";
                    break;
                case 162:
                    r = "IF_ICMPGE";
                    break;
                case 163:
                    r = "IF_ICMPGT";
                    break;
                case 164:
                    r = "IF_ICMPLE";
                    break;
                case 165:
                    r = "IF_ACMPEQ";
                    break;
                case 166:
                    r = "IF_ACMPNE";
                    break;
                case 167:
                    r = "GOTO";
                    break;
                case 168:
                    r = "JSR";
                    break;
                case 169:
                    r = "RET";
                    break;
                case 170:
                    r = "TABLESWITCH";
                    break;
                case 171:
                    r = "LOOKUPSWITCH";
                    break;
                case 172:
                    r = "IRETURN";
                    break;
                case 173:
                    r = "LRETURN";
                    break;
                case 174:
                    r = "FRETURN";
                    break;
                case 175:
                    r = "DRETURN";
                    break;
                case 176:
                    r = "ARETURN";
                    break;
                case 177:
                    r = "RETURN";
                    break;
                case 178:
                    r = "GETSTATIC";
                    break;
                case 179:
                    r = "PUTSTATIC";
                    break;
                case 180:
                    r = "GETFIELD";
                    break;
                case 181:
                    r = "PUTFIELD";
                    break;
                case Opcodes.INVOKEVIRTUAL:
                    r = "INVOKEVIRTUAL";
                    break;
                case Opcodes.INVOKESPECIAL:
                    r = "INVOKESPECIAL";
                    break;
                case 184:
                    r = "INVOKESTATIC";
                    break;
                case 185:
                    r = "INVOKEINTERFACE";
                    break;
                case 186:
                    r = "INVOKEDYNAMIC";
                    break;
                case 187:
                    r = "NEW";
                    break;
                case 188:
                    r = "NEWARRAY";
                    break;
                case 189:
                    r = "ANEWARRAY";
                    break;
                case 190:
                    r = "ARRAYLENGTH";
                    break;
                case 191:
                    r = "ATHROW";
                    break;
                case 192:
                    r = "CHECKCAST";
                    break;
                case 193:
                    r = "INSTANCEOF";
                    break;
                case 194:
                    r = "MONITORENTER";
                    break;
                case 195:
                    r = "MONITOREXIT";
                    break;
                case 197:
                    r = "MULTIANEWARRAY";
                    break;
                case 198:
                    r = "IFNULL";
                    break;
                case 199:
                    r = "IFNONNULL";
                    break;
                default:
                    r = insn.getOpcode() + "";
                    break;
            }
            if(insn instanceof FieldInsnNode)
            {
                r += " ";
                r += ((FieldInsnNode) insn).owner;
                r += ".";
                r += ((FieldInsnNode) insn).name;
                r += " : ";
                r += ((FieldInsnNode) insn).desc;
            }
            else if(insn instanceof JumpInsnNode)
            {
                r += " ";
                r += ((JumpInsnNode) insn).label.getLabel().hashCode();
            }
            else if(insn instanceof IincInsnNode)
            {
                r += " ";
                r += ((IincInsnNode) insn).var;
                r += " ";
                r += ((IincInsnNode) insn).incr;
            }
            else if(insn instanceof IntInsnNode)
            {
                r += " ";
                r += ((IntInsnNode) insn).operand;
            }
            else if(insn instanceof InvokeDynamicInsnNode)
            {
                r += " ";
                r += ((InvokeDynamicInsnNode) insn).name;
                r += " : ";
                r += ((InvokeDynamicInsnNode) insn).desc;
            }
            else if(insn instanceof LdcInsnNode)
            {
                r += " ";
                r += ((LdcInsnNode) insn).cst;
            }
            else if(insn instanceof MethodInsnNode)
            {
                r += " ";
                r += ((MethodInsnNode) insn).owner;
                r += ".";
                r += ((MethodInsnNode) insn).name;
                r += " ";
                r += ((MethodInsnNode) insn).desc;
            }
            else if(insn instanceof MultiANewArrayInsnNode)
            {
                r += " ";
                r += ((MultiANewArrayInsnNode) insn).desc;
                r += " ";
                r += ((MultiANewArrayInsnNode) insn).dims;
            }
            else if(insn instanceof TypeInsnNode)
            {
                r += " ";
                r += ((TypeInsnNode) insn).desc;
            }
            else if(insn instanceof VarInsnNode)
            {
                r += " ";
                r += ((VarInsnNode) insn).var;
            }
        }
        return r;
    }
    public static String getName(Member member)
    {
        if(member instanceof Constructor)
            return "<init>";
        else
            return member.getName();
    }
}
