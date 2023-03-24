package edu.depauw.emulator_ide.verilog_interpreter.interpreter;

import edu.depauw.emulator_ide._interface.Machine;
import edu.depauw.emulator_ide.common.debug.ErrorLog;
import edu.depauw.emulator_ide.verilog_interpreter.OpUtil;
import edu.depauw.emulator_ide.verilog_interpreter.interpreter.value.IntVal;
import edu.depauw.emulator_ide.verilog_interpreter.interpreter.value.LongVal;
import edu.depauw.emulator_ide.verilog_interpreter.interpreter.value.Value;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.function_call.SystemFunctionCall;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement.task.SystemTaskStatement;

public class EdeInterpreter extends VerilogInterpreter {
    private Machine guiInstance;
    
    public EdeInterpreter(ErrorLog errLog, Machine guiInstance){
        super(errLog);
        this.guiInstance = guiInstance;
    }

    protected Value interpretSystemFunctionCall(SystemFunctionCall call){
        String identifier = call.functionName;
        if(identifier.equals("$getRegister")){
            Expression regExp = call.argumentList.get(0);
            Value regName = interpretShallowExpression(regExp);

            return new LongVal(this.guiInstance.getRegisterValue(regName.toString()));
        } else if(identifier.equals("$getStatus")){
            Expression statusNameExp = call.argumentList.get(0);
            Value statusName = interpretShallowExpression(statusNameExp);

            return new LongVal(this.guiInstance.getStatusValue(statusName.toString()));
        } else if(identifier.equals("$getMemory")){
            Expression memoryAddressExp = call.argumentList.get(0);
            Value memAddressVal = interpretShallowExpression(memoryAddressExp);
            return new LongVal(this.guiInstance.getMemoryValue(memAddressVal.intValue()));
        } else {
            return super.interpretSystemFunctionCall(call);
        }
    }

    protected IntVal interpretSystemTaskCall(SystemTaskStatement stat){
        String identifier = stat.taskName;

        if(identifier.equals("$setRegister")){
            if(stat.argumentList.size() != 2){
               OpUtil.errorAndExit("Error: Invalid amount of Arguments for Set Register...\nExpected 2 but found " + stat.argumentList.size()); 
            } else {
                Expression registerNameExp = stat.argumentList.get(0);
                Expression registerValueExp = stat.argumentList.get(1);

                Value registerNameVal = interpretShallowExpression(registerNameExp);
                Value registerValueVal = interpretShallowExpression(registerValueExp);

                guiInstance.setRegisterValue(registerNameVal.toString(), registerValueVal.longValue());
            }
        } else if(identifier.equals("$setStatus")){
            if(stat.argumentList.size() != 2){
                OpUtil.errorAndExit("Error: Invalid amount of arguments for Status...\nExpected 2 but found " + stat.argumentList.size());
            }

            Expression statusNameExp = stat.argumentList.get(0);
            Expression statusValueExp = stat.argumentList.get(1);

            Value statusNameVal = interpretShallowExpression(statusNameExp);
            Value statusValueVal = interpretShallowExpression(statusValueExp);

            guiInstance.setStatusValue(statusNameVal.toString(), statusValueVal.longValue());
        } else if(identifier.equals("$setMemory")){
            if(stat.argumentList.size() != 2){
                OpUtil.errorAndExit("Error: Invalid amount of aruments for setingMemory Address...\nExpected 2 but found " + stat.argumentList.size());
            }

            Expression memAddressExp = stat.argumentList.get(0);
            Expression memValExp = stat.argumentList.get(1);

            Value memAddressVal = interpretShallowExpression(memAddressExp);
            Value memValVal = interpretShallowExpression(memValExp);

            
            guiInstance.setMemoryValue(memAddressVal.intValue(), memValVal.longValue());
        } else {
            return super.interpretSystemTaskCall(stat);
        }

        return OpUtil.success();
    }
}
