package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

public class TernaryExpression extends Expression{

    private final Expression condition;
    private final Expression left;
    private final Expression right;

    public TernaryExpression(Expression condition, Expression left, Expression right){
	super(condition.getPosition());
	this.left = left;
	this.right = right;
	this.condition = condition;
    }

    public Expression getLeft(){
	return left;
    }

    public Expression getRight(){
	return right;
    }

    public Expression getCondition(){
	return condition;
    }
}
