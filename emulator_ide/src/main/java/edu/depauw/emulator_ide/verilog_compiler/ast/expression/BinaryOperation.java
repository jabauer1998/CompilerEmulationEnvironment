package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

public class BinaryOperation extends Expression{

    private final Expression right;
    private final Expression left;
    private final BinOp op;

    public BinaryOperation(Expression left, BinOp op, Expression right){
	super(left.getPosition());
	this.left = left;
	this.right = right;
	this.op = op;
    }

    public Expression getLeft(){
	return left;
    }

    public Expression getRight(){
	return right;
    }

    public BinOp getOp(){
	return op;
    }
}
