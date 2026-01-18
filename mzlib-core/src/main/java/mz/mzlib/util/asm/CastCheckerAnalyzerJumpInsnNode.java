package mz.mzlib.util.asm;

import mz.mzlib.asm.Opcodes;
import mz.mzlib.asm.tree.JumpInsnNode;

import java.util.*;

public class CastCheckerAnalyzerJumpInsnNode extends CastCheckerAnalyzer<JumpInsnNode>
{
    public static CastCheckerAnalyzerJumpInsnNode instance = new CastCheckerAnalyzerJumpInsnNode();

    public Set<Integer> analyze(
        CastChecker analyzer,
        int index,
        JumpInsnNode insn,
        Stack<CastChecker.OperandVisitor> context)
    {
        switch(insn.getOpcode())
        {
            case Opcodes.IFEQ:
            case Opcodes.IFNE:
            case Opcodes.IFLT:
            case Opcodes.IFGE:
            case Opcodes.IFGT:
            case Opcodes.IFLE:
            case Opcodes.IFNULL:
            case Opcodes.IFNONNULL:
                context.pop();
                break;
            case Opcodes.IF_ICMPEQ:
            case Opcodes.IF_ICMPNE:
            case Opcodes.IF_ICMPLT:
            case Opcodes.IF_ICMPGE:
            case Opcodes.IF_ICMPGT:
            case Opcodes.IF_ICMPLE:
            case Opcodes.IF_ACMPEQ:
            case Opcodes.IF_ACMPNE:
                context.pop();
                context.pop();
                break;
            case Opcodes.GOTO:
                return Collections.singleton(analyzer.labels.get(insn.label.getLabel()));
            case Opcodes.JSR:
            default:
                throw new UnsupportedOperationException();
        }
        return new HashSet<>(Arrays.asList(index + 1, analyzer.labels.get(insn.label.getLabel())));
    }
}