package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.ExpressionList;

import java.util.List;
import java.util.ArrayList;

public class FunctionCall extends Expression{

    private final Identifier functionName;
    private final ExpressionList expList;
    
    public FunctionCall(Identifier functionName){
	super(functionName.getPosition());
	this.functionName = functionName;
	this.expList = null;
    }

    public FunctionCall(Identifier functionName, ExpressionList expList){
	super(functionName.getPosition());
	this.functionName = functionName;
	this.expList = expList;
    }

    public Identifier getFunctionName(){
	return this.functionName;
    }

    public Expression getExpression(int index){
	return expList.getExpression(index);
    }

    public int getExpressionListSize(){
	return expList.getSize();
    }
}
