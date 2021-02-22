package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;

import java.util.List;

public class Concatenation extends Expression {
    
    private final List<Expression> expressionList;
    
    public Concatenation(List<Expression> expressionList){
        super(expressionList.get(0).getPosition());
	this.expressionList = expressionList;
    }

    public Expression getExpression(int index){
	return expressionList.get(index);
    }

    public int getSize(){
	return expressionList.size();
    }
    
}
