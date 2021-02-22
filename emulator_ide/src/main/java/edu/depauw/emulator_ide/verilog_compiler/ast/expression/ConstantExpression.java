package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

public class ConstantExpression extends Expression{

    private final Expression expression;

    public ConstantExpression(Expression expression){
	this.expression = expression;
    }

    public Expression getExpression(){
	return expression;
    }
}
