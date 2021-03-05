package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.ExpressionList;

/**
 * The Concatenation class is used to parse concatenation expressions
 * Concatenation expressions are used to group Wires together from different nets
 * @author Jacob Bauer 
 */
public class Concatenation extends Expression{
    
    private final ExpressionList expList; //list of expressions to concatenate

    /** The concatenation expression is used to Concatenate multiple instances
     * @param expList the expressions which to concatenate
     */
    public Concatenation(ExpressionList expList){
	super(expList.getPosition());
	this.expList = expList;
    }

    /** To get an expression from a specific index
     *@param index index in expresson list to fetch from
     */
    public Expression getExpression(int index){
	return expList.getExpression(index);
    }

    /** Get the size of the expression list for which to concatenate
     * @param none
     */
    public int getSize(){
	return expList.getSize();
    }

    /** The accept method is designed so the visitor can visit Concatenations
     * 
     */
    public <ModVisitType, StatVisitType, ExprVisitType> ExprVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor){
	return astNodeVisitor.visit(this);
    }
}
