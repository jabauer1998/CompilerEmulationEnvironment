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

public class FunctionCall extends Expression{

    private final Identifier functionName; //name of the function
    private final ExpressionList expList; //parameter expressions

    /** The FunctionCall constructor takes two arguments:
     * @param functionName name of the function
     */
    public FunctionCall(Identifier functionName){
	super(functionName.getPosition());
	this.functionName = functionName;
	this.expList = new ExpressionList(new ArrayList<>());
    }

    public FunctionCall(Identifier functionName, ExpressionList expList){
	super(functionName.getPosition());
	this.functionName = functionName;
	this.expList = expList;
    }

    /** This function returns an identifier representing the function name
     * @param none
     */
    public Identifier getFunctionName(){
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
    public int getExpressionListSize(){
	return expList.getSize();
    }

    /** This accept method is mandatory so the FunctionCall can be visited by a visitor object
     * @param astNodeVisitor visitor obect to visit the node
     */
    public <ModVisitType, StatVisitType, ExprVisitType> ExprVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor){
	return astNodeVisitor.visit(this);
    }
}
