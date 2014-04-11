package org.dynjs.ir;

import org.dynjs.exception.ThrowException;
import org.dynjs.ir.instructions.Add;
import org.dynjs.ir.instructions.BEQ;
import org.dynjs.ir.instructions.Call;
import org.dynjs.ir.instructions.Copy;
import org.dynjs.ir.instructions.Jump;
import org.dynjs.ir.instructions.LE;
import org.dynjs.ir.instructions.LT;
import org.dynjs.ir.instructions.ResultInstruction;
import org.dynjs.ir.instructions.Return;
import org.dynjs.ir.operands.LocalVariable;
import org.dynjs.ir.operands.OffsetVariable;
import org.dynjs.ir.operands.Variable;
import org.dynjs.runtime.EnvironmentRecord;
import org.dynjs.runtime.ExecutionContext;
import org.dynjs.runtime.JSFunction;
import org.dynjs.runtime.Reference;
import org.dynjs.runtime.Types;

/**
 * Created by enebo on 4/11/14.
 */
public class Interpreter {

    public static Object execute(ExecutionContext context, Scope scope, Instruction[] instructions) {
        Object result = Types.UNDEFINED;
        Object[] temps = new Object[scope.getTemporaryVariableSize()];
        Object[] vars = new Object[scope.getLocalVariableSize()];
        int size = instructions.length;
        Object value = Types.UNDEFINED;

        int ipc = 0;
        while (ipc < size) {
            Instruction instr = instructions[ipc];
            ipc++;
            //System.out.println("EX: " + instr);

            switch(instr.getOperation()) {
                case ADD:
                    value = add(context,
                            ((Add) instr).getLHS().retrieve(context, temps, vars),
                            ((Add) instr).getRHS().retrieve(context, temps, vars));
                    break;
                case COPY:
                    value = ((Copy) instr).getValue().retrieve(context, temps, vars);
                    break;
                case JUMP:
                    ipc = ((Jump) instr).getTarget().getTargetIPC();
                    break;
                case CALL:
                    Call call = (Call) instr;
                    Object ref = call.getIdentifier().retrieve(context, temps, vars);
                    Object function = Types.getValue(context, ref);
                    Operand[] opers = call.getArgs();
                    Object[] args = new Object[opers.length];

                    for (int i = 0; i < args.length; i++) {
                        args[i] = opers[i].retrieve(context, temps, vars);
                    }

                    if (!(function instanceof JSFunction)) {
                        throw new ThrowException(context, context.createTypeError(ref + " is not calllable"));
                    }

                    Object thisValue = null;

                    // FIXME: We can probably remove this check for IRJSFunctions since we will not be using references
                    if (ref instanceof Reference) {
                        if (((Reference) ref).isPropertyReference()) {
                            thisValue = ((Reference) ref).getBase();
                        } else {
                            thisValue = ((EnvironmentRecord) ((Reference) ref).getBase()).implicitThisValue();
                        }
                    }

                    value = context.call(ref, (JSFunction) function, thisValue, args);
                    break;
                case LT: {
                    Object arg1  = ((LT) instr).getArg1().retrieve(context, temps, vars);
                    Object arg2  = ((LT) instr).getArg2().retrieve(context, temps, vars);
                    Object r = Types.compareRelational(context, arg1, arg2, true);
                    value = r == Types.UNDEFINED ? false : r;
                    break;
                }
                case LE: {
                    Object arg1  = ((LE) instr).getArg1().retrieve(context, temps, vars);
                    Object arg2  = ((LE) instr).getArg2().retrieve(context, temps, vars);
                    Object r = Types.compareRelational(context, arg1, arg2, true);
                    value = r == Boolean.TRUE || r == Types.UNDEFINED ? false : r;
                    break;
                }
                case BEQ: {
                    BEQ beq = (BEQ) instr;
                    Object arg1 = beq.getArg1().retrieve(context, temps, vars);
                    Object arg2 = beq.getArg2().retrieve(context, temps, vars);

                    if (arg1.equals(arg2)) {
                        ipc = beq.getTarget().getTargetIPC();
                    }
                    break;
                }
                case RETURN:
                    result = ((Return) instr).getValue().retrieve(context, temps, vars);
                    break;
            }

            if (instr instanceof ResultInstruction) {
                Variable variable = ((ResultInstruction) instr).getResult();
                if (variable instanceof OffsetVariable) {
                    int offset = ((OffsetVariable) variable).getOffset();

                    if (variable instanceof LocalVariable) {
                        vars[offset] = value;
                    } else {
                        temps[offset] = value;
                    }
                } else {
                    // FIXME: Lookup dynamicvariable
                }
            }
        }

        System.out.println("RESULT is " + result);

        return result;
    }

    private static Object add(ExecutionContext context, Object lhs, Object rhs) {
        if (lhs instanceof String || rhs instanceof String) {
            return(Types.toString(context, lhs) + Types.toString(context, rhs));
        }

        Number lhsNum = Types.toNumber(context, lhs);
        Number rhsNum = Types.toNumber(context, rhs);

        if (Double.isNaN(lhsNum.doubleValue()) || Double.isNaN(rhsNum.doubleValue())) {
            return(Double.NaN);
        }

        if (lhsNum instanceof Double || rhsNum instanceof Double) {
            if (lhsNum.doubleValue() == 0.0 && rhsNum.doubleValue() == 0.0) {
                if (Double.compare(lhsNum.doubleValue(), 0.0) < 0 && Double.compare(rhsNum.doubleValue(), 0.0) < 0) {
                    return(-0.0);
                } else {
                    return(0.0);
                }
            }
            return(lhsNum.doubleValue() + rhsNum.doubleValue());
        }

        return(lhsNum.longValue() + rhsNum.longValue());
    }
}