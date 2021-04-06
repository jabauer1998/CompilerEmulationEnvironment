package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.visitor.ExpressionVisitor;
import edu.depauw.emulator_ide.verilog_compiler.token.Token;

import java.lang.String;

/** The Identifier class is used to define an Identifier
 * An identifier is any set of characters that isnt considered a verilog keyword.
 * This includes variable names, function names, module names Etc...
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

    /**The accept method will make it so the visitor interface will work
     * @param astNodeVisitor the visitor object we want to use to visit another member of a class
     */
    public <ExprVisitType> ExprVisitType accept(ExpressionVisitor<ExprVisitType> exprVisitor, Object... argv){
	return exprVisitor.visit(this, argv);
    }
    
}
