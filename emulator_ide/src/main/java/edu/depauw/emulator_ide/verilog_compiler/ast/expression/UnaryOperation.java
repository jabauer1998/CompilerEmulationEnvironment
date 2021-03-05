package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;
/**
 * The Unary Operation class is used to parse unary operations
 * unary operations are operations with one operator and an expression
 * ex: - (1 + 5) the Expression is 1 + 5 and the negation is the unary operation
 * @author Jacob Bauer
 */

public class UnaryOperation extends Expression{

    private final Expression right; //the right hand side of the equation
    private final UnOp op; // the operator

    /** The UnaryOperation constuctor is used to create a Unary operation
     *  @param op operator to use
     *  @param right expression to apply the operator to
     */
    
    public UnaryOperation(UnOp op, Expression right){
	super(right.getPosition());
	this.right = right;
	this.op = op;
    }

    /** The right operator is used to get the right hand side of an expression
     * @param none
     */
    
    public Expression getRight(){
	return right;
    }

    /**
     * Expression used to get the operator used in the Operation
     * @param none
     */
    
    public UnOp getOp(){
	return op;
    }

    /**
     * The accept method is used to visit Unary Operations
     * @param astNodeVisitor the visitor object visiting the unary operation
     */
    
    public <ModVisitType, StatVisitType, ExprVisitType> ExprVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor){
	return astNodeVisitor.visit(this);
    }
}
