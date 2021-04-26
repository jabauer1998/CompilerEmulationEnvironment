package edu.depauw.emulator_ide.verilog_compiler.symbol_table.table_entry;

import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.ModItem;

import java.util.ArrayList;

public class InterpreterFunctionData{

    private final ModItem func;
    private final Position position;

    public InterpreterFunctionData(ModItem func, Position position){
	this.func = func;
	this.position = position;
    }

    public ModItem getFuncDeclaration(){
	return func;
    }

    public Position getPosition(){
	return this.position;
    }
    
    
}
