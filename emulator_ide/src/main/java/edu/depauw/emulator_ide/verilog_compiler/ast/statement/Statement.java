package edu.depauw.emulator_ide.verilog_compiler.ast.statement;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

public abstract class Statement extends AstNode{
    
    public Statement(Position position){
	super(position);
    }
    
}
