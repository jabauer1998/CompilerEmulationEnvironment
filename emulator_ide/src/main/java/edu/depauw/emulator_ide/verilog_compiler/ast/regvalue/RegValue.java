package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

/** The RegValue class is an extention of the AstNode class
 * This class is used as a supertype for all expression objects
 * @author Jacob Bauer
 */
public abstract class RegValue extends AstNode{

    /** The RegValue constructor takes in a positionand passes it to the 
     * AstNode super consturctor
     * @param position the position obect to pass to the Astnode constuctor
     */
    
    protected RegValue(Position position){
	super(position);
    }

    /** Every Expression type must contain a accept method so it can be visited 
     * by a visitor
     * @param astNodeVisitor visitor object to visit expression
     */
    
    public abstract <ModVisitType, StatVisitType, ExprVisitType> ExprVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor);
    
}
