package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;

/** The const expression class is used to parse constant expressions
 * This class actually just wraps an expressoin and during the type checking 
 * phase we will determine if it is const
 * @param Jacob Bauer
 */
public class ConstantExpression extends Expression{

    private final Expression expression; //expression determined to be const

    /** The const expression constructor takes in a single expression 
     * determined to be const
     * @param Jacob Bauer
     */
    public ConstantExpression(Expression expression){
	super(expression.getPosition());
	this.expression = expression;
    }

    /** Retrieves the expression exprected to be constant
     * @param none
     */
    public Expression getExpression(){
	return expression;
    }

    /** This accept method is needed to visit const expressions
     * @param astNodeVisitor the visitor that is visiting the object
     */
    public <ModVisitType, StatVisitType, ExprVisitType> ExprVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor){
	return astNodeVisitor.visit(this);
    }
}
