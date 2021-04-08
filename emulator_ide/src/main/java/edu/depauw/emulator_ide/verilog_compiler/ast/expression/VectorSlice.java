package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.visitor.ExpressionVisitor;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;

/**The vectorslice class is used to par.E a call to an array cell
 * @author Jacob Bauer
 */

public class VectorSlice extends Expression{
    
    private final ConstantExpression index1; //initial index to grap from the array
    private final ConstantExpression index2; //final index to grab from the array
    private final Identifier ident; //name of the array

    /** The VectorCall constructor takes an identifier with up to twoindex to s Ecify the sub array that is desired
     * @param ident name of the array
     * @param index1 min index of the array
     * @param index2 max index of the array
     */
    public VectorSlice(Identifier ident, ConstantExpression index1, ConstantExpression index2){
	super(ident.getPosition());
	this.index1 = index1;
	this.index2 = index2;
	this.ident = ident;
    }

    /** The name of the array
     * @param none
     */
    public Identifier getIdentifier(){
	return ident;
    }

    /** Returns the starting index
     * @param none
     */
    public Expression getExpression1(){
	return index1;
    }

    /** Returns the ending index
     * @param none
     */
    public Expression getExpression2(){
	return index2;
    }

     /** Returns the starting index
     * @param none
     */
    public Expression setExpression1(Expression index1){
	this.index1 = index1;
    }

    /** Returns the ending index
     * @param none
     */
    public Expression setExpression2(Expression index2){
	this.index2 = index2;
    }

    /**The accept method will make it so the visitor interface will work
     * @param astNodeVisitor the visitor object we want to use to visit another member of a class
     */
    public <ExprVisitType> ExprVisitType accept(ExpressionVisitor<ExprVisitType> exprVisitor, Object... argv){
	return exprVisitor.visit(this, argv);
    }
}
