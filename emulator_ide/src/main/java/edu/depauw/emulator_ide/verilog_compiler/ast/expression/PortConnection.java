package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

/** The port connection class is used for module instantiations
 * @author Jacob Bauer
 */
public class PortConnection extends Expression{

    private final Identifier ident;// name of the port connection
    private final Expression exp; //what the port connection equals

    /** The port connection constructor takes in an identifier 
     * @param ident name of the port connecting to
     * @param exp expression representing what is being connected
     */
    public PortConnection(Identifier ident, Expression exp){
	super(ident.getPosition());
	this.ident = ident;
	this.exp = exp;
    }

    /** Retrieves the expression that represents what is being connected
     * @param none
     */
    public Expression getExpression(){
	return exp;
    }

    /** Returns the name of the identifier that we are connected to
     * @param none
     */
    public Identifier getIdentifier(){
	return ident;
    }

    /** The accept method is used to visit a node
     *@param astNodeVisitor the visitor object to visit the Port Connection
     */
    public <ModVisitType, StatVisitType, ExprVisitType> ExprVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor){
	return astNodeVisitor.visit(this);
    }
    
}
