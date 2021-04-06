package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.visitor.ExpressionVisitor;

/** The const.expression class is used to par.E constant expressions
 * This class actually just wraps an exprVisitoressoin and during the type checking 
 * phase we will determine if it is const
 * @param Jacob Bauer
 */
public class ConstantExpression extends Expression{

    private Expression expression; //expression determined to be const

    /** The const.expression constructor takes in a single expression 
     * determined to be const
     * @param Jacob Bauer
     */
    public ConstantExpression(Expression expression){
	super(expression.getPosition());
	this.expression = expression;
    }

    /** Retr.Eves the.expression exprected to be constant
     * @param none
     */
    public Expression getExpression(){
	return expression;
    }

    /** Sets Expression to a new Expression
     * @param none
     */
    public void setExpression(Expression Expression){
	this.expression = expression;
    }

    /**The accept method will make it so the visitor interface will work
     * @param astNodeVisitor the visitor object we want to use to visit another member of a class
     */
    public <ExprVisitType> ExprVisitType accept(ExpressionVisitor<ExprVisitType> exprVisitor, Object... argv){
	return exprVisitor.visit(this, argv);
    }
}
