package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;

/** The Ternary expression class was designed to parse Ternary expressions
 * Ex: (i == x) ?  x : y it kind of works like an if else statement
 * @author Jacob Bauer
 */
public class TernaryExpression extends Expression{

    private final Expression condition; //the condition phrase of the ternary expression
    private final Expression left; //the expression to the left of the colon
    private final Expression right; //the expression to the right of the colon

    /** Th ternary expression takes in 3 expressions only one of which it returns.
     * It can return the one on the left hand side if the colon or the right.
     * @param condition the condition to be evaluated
     * @param left the expression to return if the condition is true
     * @param right the expresson to evaluate if the condition is false
     */
    public TernaryExpression(Expression condition, Expression left, Expression right){
	super(condition.getPosition());
	this.left = left;
	this.right = right;
	this.condition = condition;
    }

    /** Returns the expression that is true
     * @param none
     */
    public Expression getLeft(){
	return left;
    }

    /** Returns the expression that is false
     * @param none
     */
    public Expression getRight(){
	return right;
    }

    /** Returns the expression that is checked at the beginning of the ternary operation
     * @param none
     */
    public Expression getCondition(){
	return condition;
    }

    /** The accept method is necessary so any Ternary expression can be visited
     *
     */
    public <ModVisitType, StatVisitType, ExprVisitType> ExprVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor){
	return astNodeVisitor.visit(this);
    }
}
