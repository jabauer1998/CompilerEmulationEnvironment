package edu.depauw.emulator_ide.verilog_compiler.symbol_table.table_entry;

import edu.depauw.emulator_ide.verilog_compiler.token.Position;

public class InterpreterVariableData{

    private Object value;
    private final Position position;

    public InterpreterVariableData(Object value, Position position){
	this.value = value;
	this.position = position;
    }

    public Object getObject(){
	return value;
    }

    public void setObject(Object value){
	this.value = value;
    }

    public Position getPosition(){
	return this.position;
    }
    
}
