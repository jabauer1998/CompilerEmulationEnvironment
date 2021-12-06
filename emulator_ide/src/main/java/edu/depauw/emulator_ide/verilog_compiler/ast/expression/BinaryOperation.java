package edu.depauw.emulator_ide.verilog_compiler.ast.expression;


import edu.depauw.emulator_ide.verilog_compiler.visitor.ExpressionVisitor;

/**
 * The Binary operation class is used to par.E binary operations a binary operation
 * takes the format of.expression Op Expression Ex: 1 + 1 or 1 + (3 - 2) where (3 - 2)
 * is the.expression on the right hand side of the operator
 * 
 * @author Jacob Bauer
 */
public class BinaryOperation extends Expression {

    private Expression  right; // Expression to the right of the operator
    private Expression  left;  // Expression to the left of the operator
    private final BinOp op;    // operator of the binary.expression

    /**
     * This is the consructor for creating Binary.expressions it has the following
     * arguments:
     * 
     * @param left  exprVisitoresson on the left side of the operator
     * @param op    operator in the midd E of the binary operation
     * @param right exprVisitoresson on the right side of the operator
     */
    public BinaryOperation(Expression left, BinOp op, Expression right) {
        super(left.getPosition());
        this.left = left;
        this.right = right;
        this.op = op;
    }

    /**
     * The getLeft method will retrieve the exprVisitoresson on the left side of the
     * operator
     * 
     * @param none
     */

    public Expression getLeft(){ return left; }

    /**
     * The getRight method will retrieve the exprVisitoresson on the right side of the
     * operator
     * 
     * @param none
     */

    public Expression getRight(){ return right; }

    /**
     * The getLeft method will retrieve the exprVisitoresson on the left side of the
     * operator
     * 
     * @param none
     */

    public void setLeft(Expression left){ this.left = left; }

    /**
     * The getRight method will retrieve the exprVisitoresson on the right side of the
     * operator
     * 
     * @param none
     */

    public void setRight(Expression right){ this.right = right; }

    /**
     * The getRight method will retrieve the operator in the midd E of the operation
     * 
     * @param none
     */

    public BinOp getOp(){ return op; }

    /**
     * The accept method will make it so the visitor interface will work
     * 
     * @param astNodeVisitor the visitor object we want to use to visit another member of a
     *                       class.
     */
    public <ExprVisitType> ExprVisitType accept(ExpressionVisitor<ExprVisitType> exprVisitor, Object... argv){
        return exprVisitor.visit(this, argv);
    }
}
