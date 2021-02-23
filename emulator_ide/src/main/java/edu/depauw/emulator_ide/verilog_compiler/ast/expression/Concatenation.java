package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.ExpressionList;

public class Concatenation extends Expression{
    
    private final ExpressionList expList;
    
    public Concatenation(ExpressionList expList){
	super(expList.getPosition());
	this.expList = expList;
    }

    public Expression getExpression(int index){
	return expList.getExpression(index);
    }

    public int getSize(){
	return expList.getSize();
    }
}
