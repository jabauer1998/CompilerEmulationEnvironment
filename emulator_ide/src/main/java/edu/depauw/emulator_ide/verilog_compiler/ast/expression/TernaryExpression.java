package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.visitor.ExpressionVisitor;

/** The Ternary.expression class was designed to par.E Ternary expressions
 * Ex: (i == x) ?  x : y it kind of works like an if else statement
 * @author Jacob Bauer
 */
public class TernaryExpression extends Expression{

    private Expression condition; //the condition phrase of the ternary Expression
    private Expression left; //the Expression to the left of the colon
    private Expression right; //the Expression to the right of the colon

    /** Th ternary.expression takes in 3 expressions only one of which it returns.
     * It can return the one on the left hand side if the colon or the right.
     * @param condition the condition to be evaluated
     * @param left the.expression to return if the condition is tr.E
     * @param right the exprVisitoresson to evaluate if the condition is false
     */
    public TernaryExpression(Expression condition, Expression left, Expression right){
	super(condition.getPosition());
	this.left = left;
	this.right = right;
	this.condition = condition;
    }

    /** Returns the.expression that is tr.E
     * @param none
     */
    public Expression getLeft(){
	return left;
    }

    public void setLeft(Expression left){
	this.left = left;
    }

    /** Returns the.expression that is false
     * @param none
     */
    public Expression getRight(){
	return right;
    }

    public void setRight(Expression right){
	this.right = right;
    }

    /** Returns the.expression that is checked at the beginning of the ternary operation
     * @param none
     */
    public Expression getCondition(){
	return condition;
    }

    /** Returns the.expression that is checked at the beginning of the ternary operation
     * @param none
     */
    public void setCondition(Expression condition){
        this.condition = condition;
    }

    /**The accept method will make it so the visitor interface will work
     * @param astNodeVisitor the visitor object we want to use to visit another member of a class
     */
    public <ExprVisitType> ExprVisitType accept(ExpressionVisitor<ExprVisitType> exprVisitor, Object... argv){
	return exprVisitor.visit(this, argv);
    }
}
