package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;

/**The vectorslice class is used to parse a call to an array cell
 * @author Jacob Bauer
 */

public class IntegerIdent extends RegValue{
    
    private final Identifier ident; //name of the array

    /** The VectorCall constructor takes an identifier with up to twoindex to specify the sub array that is desired
     * @param ident name of the array
     * @param index1 min index of the array
     * @param index2 max index of the array
     */
    public IntegerArray(Identifier ident){
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

    /**
     * The accept method is used to visit VectorCalls
     * @param astNodeVisitor the visitor object visiting the unary operation
     */
    
    public <ModVisitType, StatVisitType, ExprVisitType> ExprVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor, Object... argv){
	return astNodeVisitor.visit(this, Object... argv);
    }
}
