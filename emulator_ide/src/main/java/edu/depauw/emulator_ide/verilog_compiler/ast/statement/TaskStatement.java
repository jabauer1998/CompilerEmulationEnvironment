package edu.depauw.emulator_ide.verilog_compiler.ast.statement;

import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.ExpressionList;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Identifier;


import java.util.List;
import java.util.ArrayList;

public class TaskStatement extends Statement{

    private final Identifier functionName;
    private final ExpressionList expList;
    
    public TaskStatement(Identifier functionName){
	super(functionName.getPosition());
	this.functionName = functionName;
	this.expList = new ExpressionList(new ArrayList<>());
    }

    public TaskStatement(Identifier functionName, ExpressionList expList){
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
