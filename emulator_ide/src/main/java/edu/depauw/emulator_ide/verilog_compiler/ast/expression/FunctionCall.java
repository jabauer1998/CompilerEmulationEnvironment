package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;

import java.util.List;
import java.util.ArrayList;

public class FunctionCall extends Expression{

    private final Identifier functionName;
    private final List<Expression> expressionList;
    
    public FunctionCall(Identifier functionName){
	super(functionName.getPosition());
	this.functionName = functionName;
	this.expressionList = new ArrayList<>();
    }

    public FunctionCall(Identifier functionName, List<Expression> expressionList){
	super(functionName.getPosition());
	this.functionName = functionName;
	this.expressionList = expressionList;
    }

    public Identifier getFunctionName(){
	return this.functionName;
    }

    public Expression getExpression(int index){
	return expressionList.get(index);
    }

    public int getExpressionListSize(){
	return expressionList.size();
    }
}
