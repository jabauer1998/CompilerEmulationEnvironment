package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;

public class Vector extends Expression{
    
    private final Expression index1;
    private final Expression index2;
    
    private final Identifier ident;

    public Vector(Identifier ident, Expression index1, Expression index2){
	super(ident.getPosition());
	this.index1 = index1;
	this.index2 = index2;
	this.ident = ident;
    }
    
    public Vector(Identifier ident, Expression index1){
	super(ident.getPosition());
	this.index1 = index1;
	this.index2 = null;
	this.ident = ident;
    }

    public Identifier getIdentifier(){
	return ident;
    }

    public Expression getExpression(int index){
	if(index == 2){
	    return index1;
	} else {
	    return index2;
	}
    }
}
