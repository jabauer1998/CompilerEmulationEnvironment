package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;
import edu.depauw.emulator_ide.verilog_compiler.token.Token;

import java.lang.String;

/** The Identifier class is used to define an Identifier
 * An identifier is any set of characters that isnt considered a verilog keyword.
 * This includes variable names, function names, module names etc...
 */

public class Identifier extends Expression{

    private final Token ident; //identifier Token to refererence identifier

    /**The identifier constructor produces an identifier given an Identifier Token
     * @param ident the ident token to base the token off of
     */
    public Identifier(Token ident){
	super(ident.getPosition());
	this.ident = ident;
    }

    /**
     * Returns the lexeme of the identifier
     * The lexeme is just the name of the variable
     * @param none
     */
    public String getLexeme(){
	return ident.getLexeme();
    }

    /**
     * The accept method was design so a visitor object can visit this type of node
     * @param the visitor object to visit the Identifier
     */
    public <ModVisitType, StatVisitType, ExprVisitType> ExprVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor){
	return astNodeVisitor.visit(this);
    }
    
}
