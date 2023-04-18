package io.github.h20man13.emulator_ide.verilog_interpreter.interpreter;

import io.github.h20man13.emulator_ide._interface.Machine;
import io.github.h20man13.emulator_ide.common.debug.ErrorLog;
import io.github.h20man13.emulator_ide.verilog_interpreter.OpUtil;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.IntVal;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.LongVal;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.Value;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.expression.function_call.SystemFunctionCall;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.statement.task.SystemTaskStatement;

public class EdeInterpreter extends VerilogInterpreter {
    private Machine guiInstance;
    private String standardOutputPane;
    private String standardInputPane;
    
    public EdeInterpreter(ErrorLog errLog, Machine guiInstance, String standardOutputPane, String standardInputPane){
        super(errLog);
        this.guiInstance = guiInstance;
        this.standardOutputPane = standardOutputPane;
        this.standardInputPane = standardInputPane;
    }

    protected Value interpretSystemFunctionCall(SystemFunctionCall call) throws Exception{
        String identifier = call.functionName;
        if(identifier.equals("getRegister")){
            Expression regExp = call.argumentList.get(0);
            Value regName = interpretShallowExpression(regExp);

            if(regName.isStringValue()){
                return new LongVal(this.guiInstance.getRegisterValue(regName.toString()));
            } else {
                return new LongVal(this.guiInstance.getRegisterValue(regName.intValue()));
            }
        } else if(identifier.equals("getStatus")){
            Expression statusNameExp = call.argumentList.get(0);
            Value statusName = interpretShallowExpression(statusNameExp);

            return new LongVal(this.guiInstance.getStatusValue(statusName.toString()));
        } else if(identifier.equals("getMemory")){
            Expression memoryAddressExp = call.argumentList.get(0);
            Value memAddressVal = interpretShallowExpression(memoryAddressExp);
            return new LongVal(this.guiInstance.getMemoryValue(memAddressVal.intValue()));
        } else {
            return super.interpretSystemFunctionCall(call);
        }
    }

    protected IntVal interpretSystemTaskCall(SystemTaskStatement stat) throws Exception{
        String identifier = stat.taskName;

        if(identifier.equals("display")){
           if (stat.argumentList.size() >= 2) {
               Value fString = interpretShallowExpression(stat.argumentList.get(0));

               Object[] Params = new Object[stat.argumentList.size() - 1];
               for(int paramIndex = 0, i = 1; i < stat.argumentList.size(); i++, paramIndex++){
                    Value  fData = interpretShallowExpression(stat.argumentList.get(i));
                    Object rawValue = OpUtil.getRawValue(fData);
                    Params[paramIndex] = rawValue;
               }
               

               String formattedString = String.format(fString.toString(), Params);
               guiInstance.appendIoText(standardOutputPane, formattedString + "\r\n");
           } else if (stat.argumentList.size() == 1) {
               Value data = interpretShallowExpression(stat.argumentList.get(0));
               guiInstance.appendIoText(standardOutputPane, data.toString() + "\r\n");
           } else {
               OpUtil.errorAndExit("Unknown number of print arguments in " + stat.taskName, stat.position);
           }
        } else if(identifier.equals("setRegister")){
            if(stat.argumentList.size() != 2){
               OpUtil.errorAndExit("Error: Invalid amount of Arguments for Set Register...\nExpected 2 but found " + stat.argumentList.size()); 
            } else {
                Expression registerNameExp = stat.argumentList.get(0);
                Expression registerValueExp = stat.argumentList.get(1);

                Value registerNameVal = interpretShallowExpression(registerNameExp);
                Value registerValueVal = interpretShallowExpression(registerValueExp);

                if(registerNameVal.isStringValue()){
                    guiInstance.setRegisterValue(registerNameVal.toString(), registerValueVal.longValue());
                } else {
                    guiInstance.setRegisterValue(registerNameVal.intValue(), registerValueVal.longValue());
                }
            }
        } else if(identifier.equals("setStatus")){
            if(stat.argumentList.size() != 2){
                OpUtil.errorAndExit("Error: Invalid amount of arguments for Status...\nExpected 2 but found " + stat.argumentList.size());
            }

            Expression statusNameExp = stat.argumentList.get(0);
            Expression statusValueExp = stat.argumentList.get(1);

            Value statusNameVal = interpretShallowExpression(statusNameExp);
            Value statusValueVal = interpretShallowExpression(statusValueExp);

            guiInstance.setStatusValue(statusNameVal.toString(), statusValueVal.longValue());
        } else if(identifier.equals("setMemory")){
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
