package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

public class EmptyExpression extends Expression{
    
    public EmptyExpression(Position position){
	super(position);
    }
    
}
