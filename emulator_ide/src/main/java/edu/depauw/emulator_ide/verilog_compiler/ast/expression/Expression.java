package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

public abstract class Expression extends AstNode{
    
    protected Expression(Position position){
	super(position);
    }
    
}
