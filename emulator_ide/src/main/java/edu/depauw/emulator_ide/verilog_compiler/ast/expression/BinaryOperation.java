package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;

/** The Binary operation class is used to parse binary operations
 * a binary operation takes the format of Expression Op Expression
 * Ex: 1 + 1 or 1 + (3 - 2) where (3 - 2) is the expression on the right hand side of the operator
 * @author Jacob Bauer 
 */
public class BinaryOperation extends Expression{

    private final Expression right; //expression to the right of the operator
    private final Expression left; //expression to the left of the operator
    private final BinOp op; //operator of the binary expression

    /** This is the consructor for creating Binary expressions it has the 
     * following arguments:
     * @param left expresson on the left side of the operator
     * @param op operator in the middle of the binary operation
     * @param right expresson on the right side of the operator
     */
    public BinaryOperation(Expression left, BinOp op, Expression right){
	super(left.getPosition());
	this.left = left;
	this.right = right;
	this.op = op;
    }

    /**The getLeft method will retrieve the expresson on the left side of the
     * operator 
     * @param none
     */

    public Expression getLeft(){
	return left;
    }

    /**The getRight method will retrieve the expresson on the right side of the
     * operator 
     * @param none
     */

    public Expression getRight(){
	return right;
    }

    /**The getRight method will retrieve the operator in the middle of the 
     * operation
     * @param none
     */

    public BinOp getOp(){
	return op;
    }

    /**The accept method will make it so the visitor interface will work
     * @param astNodeVisitor the visitor object we want to use to visit another member of a class
     */
    public <ModVisitType, StatVisitType, ExprVisitType> ExprVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor){
	return astNodeVisitor.visit(this);
    }
}
