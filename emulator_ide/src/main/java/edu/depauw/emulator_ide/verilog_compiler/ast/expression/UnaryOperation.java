package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

public class UnaryOperation extends Expression{

    private final Expression right;
    private final UnOp op;

    public UnaryOperation(UnOp op, Expression right){
	super(right.getPosition());
	this.right = right;
	this.op = op;
    }

    public Expression getRight(){
	return right;
    }

    public UnOp getOp(){
	return op;
    }
}
