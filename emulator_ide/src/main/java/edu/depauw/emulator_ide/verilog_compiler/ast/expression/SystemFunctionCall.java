package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.ExpressionList;

import java.util.List;
import java.util.ArrayList;

/** The FunctionCall is used to call functions
 * Functions are different than tasks because they have a return value
 * @author Jacob Bauer
 */

public class SystemFunctionCall extends Expression{

    private final Identifier functionName; //name of the function
    private final ExpressionList expList; //parameter expressions

    /** The FunctionCall constructor takes two arguments:
     * @param functionName name of the function
     */
    public SystemFunctionCall(Identifier functionName){
	super(functionName.getPosition());
	this.functionName = functionName;
	this.expList = new ExpressionList(new ArrayList<>());
    }

    public SystemFunctionCall(Identifier functionName, ExpressionList expList){
	super(functionName.getPosition());
	this.functionName = functionName;
	this.expList = expList;
    }

    /** This function returns an identifier representing the function name
     * @param none
     */
    public Identifier getSystemFunctionName(){
	return this.functionName;
    }

    /** This function returns an expression from the expression list at the specified index 
     * @param index of the expression
     */
    public Expression getExpression(int index){
	return expList.getExpression(index);
    }

    /** This function returns the expression List size
     * @param none
     */
    public int numExpressions(){
	return expList.getSize();
    }

    /**The accept method will make it so the visitor interface will work
     * @param astNodeVisitor the visitor object we want to use to visit another member of a class
     */
    public <ModVisitType, StatVisitType, ExprVisitType> ExprVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor, Object... argv){
	return astNodeVisitor.visit(this, argv);
    }
}
