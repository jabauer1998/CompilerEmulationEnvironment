package edu.depauw.emulator_ide.verilog_compiler.symbol_table.table_entry;

import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.ModItem;

import java.util.ArrayList;
import java.util.Stack;

public class InterpreterFunctionData{

    private final ModItem functionEntry;
    private final Position position;
    private ArrayList<InterpreterVariableData> parameterList;
    private final Stack<ArrayList<InterpreterVariableData>> oldList;

    public InterpreterFunctionData(ModItem functionEntry, Position position){
	this.functionEntry = functionEntry;
	this.position = position;
	this.parameterList = new ArrayList<>();
	this.oldList = new Stack<>();
    }

    public ModItem getFuncDeclaration(){
	return functionEntry;
    }

    public Position getPosition(){
	return this.position;
    }

    public void addParameter(InterpreterVariableData data){
	parameterList.add(data);
    }

    public InterpreterVariableData getParameter(int index){
	return parameterList.get(index);
    }

    public void storeParameterList(){
	oldList.push(parameterList);
	parameterList = new ArrayList<>();
    }

    public void restoreParameterList(){
	parameterList = oldList.pop();
    }

    public int numParameters(){
	return parameterList.size();
    }
    
}
