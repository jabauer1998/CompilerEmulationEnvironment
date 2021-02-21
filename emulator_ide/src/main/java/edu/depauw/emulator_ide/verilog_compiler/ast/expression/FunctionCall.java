package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import java.util.List;

public class FunctionCall implements Expression{

    private final Identifier functionName;
    private final List<Expression> expressionList;
    
    public FunctionCall(Identifier functionName){
	this.functionName = functionName;
	this.expressionList = new ArrayList<>();
    }

    public FucntionCall(Identifier functionName, List<Expression> expressionList){
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
