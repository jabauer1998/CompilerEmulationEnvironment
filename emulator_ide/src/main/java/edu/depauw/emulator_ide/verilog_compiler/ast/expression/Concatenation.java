package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.visitor.ExpressionVisitor;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.ExpressionList;

/**
 * The Concatenation class is used to par.E concatenation.expressions
 * Concatenation.expressions are used to group Wires together from different nets
 * @author Jacob Bauer 
 */
public class Concatenation extends Expression{
    
    private final ExpressionList expList; //list of Expressions to concatenate

    /** The concatenation.expression is used to Concatenate multiple instances
     * @param expList the.expressions which to concatenate
     */
    public Concatenation(ExpressionList expList){
	super(expList.getPosition());
	this.expList = expList;
    }

    /** To get an.expression from a s Ecific index
     *@param index index in exprVisitoresson list to fetch from
     */
    public Expression getExpression(int index){
	return expList.getExpression(index);
    }

    /** To get an.expression from a s Ecific index
     *@param index index in exprVisitoresson list to fetch from
     */
    public void setExpression(int index, Expression exp){
	this.expList.setExpression(index, exp);
    }

    /** Get the size of the.expression list for which to concatenate
     * @param none
     */
    public int numExpressions(){
	return expList.getSize();
    }

   /**The accept method will make it so the visitor interface will work
     * @param astNodeVisitor the visitor object we want to use to visit another member of a class
     */
    public <ExprVisitType> ExprVisitType accept(ExpressionVisitor<ExprVisitType> exprVisitor, Object... argv){
	return exprVisitor.visit(this, argv);
    }
}
