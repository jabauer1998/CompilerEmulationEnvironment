package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.visitor.ExpressionVisitor;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;

/**The vectorcall class is used to par.E a call to an array cell
 * @author Jacob Bauer
 */

public class VectorCall extends Expression{
    
    private final Expression index1; //initial index to grap from the array
    private final Identifier ident; //name of the array

    /** The VectorCall constructor takes an identifier with up to twoindex to s Ecify the sub array that is desired
     * @param ident name of the array
     * @param index1 min index of the array
     * @param index2 max index of the array
     */
    
    public VectorCall(Identifier ident, Expression index1){
	super(ident.getPosition());
	this.index1 = index1;
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
    public Expression getExpression(){
	return index1;
    }

    /** Returns the starting index
     * @param none
     */
    public void setExpression(Expression exp){
	this.index1 = exp;
    }

   /**The accept method will make it so the visitor interface will work
     * @param astNodeVisitor the visitor object we want to use to visit another member of a class
     */
    public <ExprVisitType> ExprVisitType accept(ExpressionVisitor<ExprVisitType> exprVisitor, Object... argv){
	return exprVisitor.visit(this, argv);
    }
}
