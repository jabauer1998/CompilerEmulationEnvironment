package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;

/**The vectorcall class is used to parse a call to an array cell
 * @author Jacob Bauer
 */

public class VectorCall extends Expression{
    
    private final Expression index1; //initial index to grap from the array
    private final Expression index2; //final index to grab from the array
    private final Identifier ident; //name of the array

    /** The VectorCall constructor takes an identifier with up to twoindex to specify the sub array that is desired
     * @param ident name of the array
     * @param index1 min index of the array
     * @param index2 max index of the array
     */
    public VectorCall(Identifier ident, Expression index1, Expression index2){
	super(ident.getPosition());
	this.index1 = index1;
	this.index2 = index2;
	this.ident = ident;
    }
    
    public VectorCall(Identifier ident, Expression index1){
	super(ident.getPosition());
	this.index1 = index1;
	this.index2 = null;
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

    /**
     * The accept method is used to visit VectorCalls
     * @param astNodeVisitor the visitor object visiting the unary operation
     */
    
    public <ModVisitType, StatVisitType, ExprVisitType> ExprVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor){
	return astNodeVisitor.visit(this);
    }
}
