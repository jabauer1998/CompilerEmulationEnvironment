package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

public class PortConnection extends Expression{

    private final Identifier ident;
    private final Expression exp;
    
    public PortConnection(Identifier ident, Expression exp){
	super(ident.getPosition());
	this.ident = ident;
	this.exp = exp;
    }

    public Expression getExpression(){
	return exp;
    }

    public Identifier getIdentifier(){
	return ident;
    }
    
}
