package io.github.H20man13.emulator_ide.verilog_interpreter.interpreter;

import javax.swing.tree.ExpandVetoException;
import io.github.H20man13.emulator_ide._interface.Machine;
import io.github.H20man13.emulator_ide.common.Pointer;
import io.github.H20man13.emulator_ide.common.debug.ErrorLog;
import io.github.H20man13.emulator_ide.common.debug.item.ErrorItem;
import io.github.H20man13.emulator_ide.gui.GuiEde;
import io.github.H20man13.emulator_ide.verilog_interpreter.Utils;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.IntVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.LongVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.Value;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.ede.EdeMemVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.ede.EdeRegVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.ede.EdeStatVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.function_call.SystemFunctionCall;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.label.Element;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.label.Identifier;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.label.Slice;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Input;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Output;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Reg;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.assignment.BlockingAssignment;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.task.SystemTaskStatement;

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

    protected IntVal interpretDeclaration(Reg.Vector.Array decl) throws Exception{
        String annotationLexeme = decl.annotationLexeme;
        if(annotationLexeme != null){
            if(annotationLexeme.toLowerCase().equals("@memory")){
                Value index1Val = interpretShallowExpression(decl.GetIndex1());
                int intIndex1 = index1Val.intValue();
                Value index2Val = interpretShallowExpression(decl.GetIndex2());
                int intIndex2 = index2Val.intValue();
                
                int size = (intIndex2 > intIndex1) ? intIndex2 - intIndex1 : intIndex1 - intIndex2;
                if(size != 8){
                    errorLog.addItem(new ErrorItem("Expected vector of size 8 for @Memory annotation but found reg ["+intIndex1+':'+intIndex2+']', decl.position));
                    return Utils.errorOccured();
                } else if(environment.localVariableExists(decl.declarationIdentifier)) {
                    errorLog.addItem(new ErrorItem("Error variable " + decl.declarationIdentifier + " allready exists inside the scope", decl.position));
                    return Utils.errorOccured();
                } else {
                    Value varVal = new EdeMemVal(guiInstance);
                    environment.addVariable(decl.declarationIdentifier, varVal);
                    return Utils.success();
                }
            } else {
                errorLog.addItem(new ErrorItem("Invalid annotation type for reg [] expected @Memory but found " + annotationLexeme, decl.position));
                return Utils.errorOccured();
            }
        }
        return super.interpretDeclaration(decl);
    }

    protected IntVal interpretDeclaration(Reg.Scalar.Ident decl) throws Exception{
        String annotationLexeme = decl.annotationLexeme;
        if(annotationLexeme != null){
            if(annotationLexeme.toLowerCase().equals("@status")){
                if(environment.localVariableExists(decl.declarationIdentifier)){
                    errorLog.addItem(new ErrorItem("Variable allready exists in scope with the name " + decl.declarationIdentifier, decl.position));
                    return Utils.errorOccured();
                } else {
                    environment.addVariable(decl.declarationIdentifier, new EdeStatVal(decl.declarationIdentifier, guiInstance));
                    return Utils.success();
                }
            } else {
                errorLog.addItem(new ErrorItem("Error expected annotation to be @Status but found " + annotationLexeme, decl.position));
                return Utils.errorOccured();
            }
        }
        return super.interpretDeclaration(decl);
    }

    protected IntVal interpretDeclaration(Reg.Vector.Ident decl) throws Exception{
        String annotationLexeme = decl.annotationLexeme;
        if(annotationLexeme != null){
            if(annotationLexeme.toLowerCase().equals("@register")){
                if(environment.localVariableExists(decl.declarationIdentifier)){
                    errorLog.addItem(new ErrorItem("Definition of " + decl.declarationIdentifier + " allready occured", decl.position));
                    return Utils.errorOccured();
                } else {
                    environment.addVariable(decl.declarationIdentifier, new EdeRegVal(decl.declarationIdentifier, guiInstance));
                    return Utils.success();
                }
            } else {
                errorLog.addItem(new ErrorItem("Error expected annotation to be @Register but found " + annotationLexeme, decl.position));
                return Utils.errorOccured();
            }
        }
        return super.interpretDeclaration(decl);
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
                    Object rawValue = Utils.getRawValue(fData);
                    Params[paramIndex] = rawValue;
               }
               

               String formattedString = String.format(fString.toString(), Params);
               guiInstance.appendIoText(standardOutputPane, formattedString + "\r\n");
           } else if (stat.argumentList.size() == 1) {
               Value data = interpretShallowExpression(stat.argumentList.get(0));
               guiInstance.appendIoText(standardOutputPane, data.toString() + "\r\n");
           } else {
               Utils.errorAndExit("Unknown number of print arguments in " + stat.taskName, stat.position);
           }
        } else if(identifier.equals("setRegister")){
            if(stat.argumentList.size() != 2){
               Utils.errorAndExit("Error: Invalid amount of Arguments for Set Register...\nExpected 2 but found " + stat.argumentList.size()); 
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
                Utils.errorAndExit("Error: Invalid amount of arguments for Status...\nExpected 2 but found " + stat.argumentList.size());
            }

            Expression statusNameExp = stat.argumentList.get(0);
            Expression statusValueExp = stat.argumentList.get(1);

            Value statusNameVal = interpretShallowExpression(statusNameExp);
            Value statusValueVal = interpretShallowExpression(statusValueExp);

            guiInstance.setStatusValue(statusNameVal.toString(), statusValueVal.longValue());
        } else if(identifier.equals("setMemory")){
            if(stat.argumentList.size() != 2){
                Utils.errorAndExit("Error: Invalid amount of aruments for settingMemory Address...\nExpected 2 but found " + stat.argumentList.size());
            }

            Expression memAddressExp = stat.argumentList.get(0);
            Expression memValExp = stat.argumentList.get(1);

            Value memAddressVal = interpretShallowExpression(memAddressExp);
            Value memValVal = interpretShallowExpression(memValExp);

            
            guiInstance.setMemoryValue(memAddressVal.intValue(), memValVal.longValue());
        } else {
            return super.interpretSystemTaskCall(stat);
        }

        return Utils.success();
    }

    protected IntVal interpretShallowBlockingAssingment(BlockingAssignment assign) throws Exception{

        if(assign.leftHandSide instanceof Element){
            Element leftHandSide = (Element)assign.leftHandSide;
            Pointer<Value> val = environment.lookupVariable(leftHandSide.labelIdentifier);
            Value deref = val.deRefrence();
            if(deref instanceof EdeMemVal){
                EdeMemVal memory = (EdeMemVal)deref;
                Value rightHandSideValue = interpretShallowExpression(assign.rightHandSide);
                Value indexValue = interpretShallowExpression(leftHandSide.index1);
                memory.setElemAtIndex(indexValue.intValue(), rightHandSideValue.intValue());
                return Utils.success();
            } else if(deref instanceof EdeRegVal){
                EdeRegVal register = (EdeRegVal)deref;
                Value rightHandSideValue = interpretShallowExpression(assign.rightHandSide);
                Value indexValue = interpretShallowExpression(leftHandSide.index1);
                register.setBitAtIndex(indexValue.intValue(), rightHandSideValue.intValue());
                return Utils.success();
            }
        } else if(assign.leftHandSide instanceof Slice){
            Slice leftHandSide = (Slice)assign.leftHandSide;
            Pointer<Value> val = environment.lookupVariable(leftHandSide.labelIdentifier);
            Value deref = val.deRefrence();
            if(deref instanceof EdeRegVal){
                EdeRegVal register = (EdeRegVal)deref;
                Value rightHandSideValue = interpretShallowExpression(assign.rightHandSide);
                Value index1Value = interpretShallowExpression(leftHandSide.index1);
                Value index2Value = interpretShallowExpression(leftHandSide.index2);
                register.setBitsAtIndex(index1Value.intValue(), index2Value.intValue(), rightHandSideValue.intValue());
                return Utils.success();
            }
        } else if(assign.leftHandSide instanceof Identifier){
            Identifier leftHandSide = (Identifier)assign.leftHandSide;
            Pointer<Value> val = environment.lookupVariable(leftHandSide.labelIdentifier);
            Value deref = val.deRefrence();
            if(deref instanceof EdeStatVal){
                EdeStatVal status = (EdeStatVal)deref;
                Value rightHandSide = interpretShallowExpression(assign.rightHandSide);
                status.setStatusValue(rightHandSide.intValue());
                return Utils.success();
            } else if(deref instanceof EdeRegVal){
                EdeRegVal reg = (EdeRegVal)deref;
                Value rightHandSide = interpretShallowExpression(assign.rightHandSide);
                reg.setAllBits(rightHandSide.intValue());
                return Utils.success();
            }
        }

        return super.interpretShallowBlockingAssignment(assign);
    }

    public Value interpretShallowIdentifier(Identifier ident) throws Exception{
        if (environment.variableExists(ident.labelIdentifier)) {
			Pointer<Value> data = environment.lookupVariable(ident.labelIdentifier);
			Value typeData = data.deRefrence();
            if(typeData instanceof EdeStatVal){
                EdeStatVal stat = (EdeStatVal)typeData;
                return new LongVal(stat.intValue());
            } else if(typeData instanceof EdeRegVal){
                EdeRegVal reg = (EdeRegVal)typeData;
                return new LongVal(reg.longValue());
            }
		}
        return super.interpretShallowIdentifier(ident);
    }

    public Value interpretShallowElement(Element elem) throws Exception{
        if(environment.variableExists(elem.labelIdentifier)){
            Pointer<Value> data = environment.lookupVariable(elem.labelIdentifier);
			Value typeData = data.deRefrence();
            if(typeData instanceof EdeMemVal){
                EdeMemVal edeMemVal = (EdeMemVal)typeData;
                Value result = interpretShallowExpression(elem.index1);
                return new LongVal(edeMemVal.elemAtIndex(result.intValue()));
            } else if(typeData instanceof EdeRegVal){
                EdeRegVal edeRegVal = (EdeRegVal)typeData;
                Value result = interpretShallowExpression(elem.index1);
                return new LongVal(edeRegVal.getBitAtIndex(result.intValue()));
            }
        }
        return super.interpretShallowElement(elem);
    }

    public Value interpretShallowSlice(Slice elem) throws Exception{
        if(environment.variableExists(elem.labelIdentifier)){
            Pointer<Value> data = environment.lookupVariable(elem.labelIdentifier);
			Value typeData = data.deRefrence();
            if(typeData instanceof EdeRegVal){
                EdeRegVal edeRegVal = (EdeRegVal)typeData;
                Value result1 = interpretShallowExpression(elem.index1);
                Value result2 = interpretShallowExpression(elem.index2);    
                return new LongVal(edeRegVal.getBitsInRange(result1.intValue(), result2.intValue()));
            }
        }
        return super.interpretShallowSlice(elem);
    }
}
