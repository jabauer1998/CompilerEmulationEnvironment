package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.visitor.ExpressionVisitor;
import edu.depauw.emulator_ide.verilog_compiler.token.Token;
import java.lang.String;

/** The StrValue Ast node class is used to Par.E Strings
 * @author Jacob Bauer
 */
public class StrValue extends Expression{
    private final Token string; //token to hold the string value

    /** The StrValue constructor when provided a token produces the exprVisitorected string value ast node
     * @param string token representing the string value to be created
     */
    public StrValue(Token string){
	super(string.getPosition());
	this.string = string;
    }

    /** Designed to get the string representation of the string value object
     * @param none
     */
    public String getLexeme(){
	return string.getLexeme().substring(1, string.getLexeme().length() - 1);
    }

    /**The accept method will make it so the visitor interface will work
     * @param astNodeVisitor the visitor object we want to use to visit another member of a class
     */
    public <ExprVisitType> ExprVisitType accept(ExpressionVisitor<ExprVisitType> exprVisitor, Object... argv){
	return exprVisitor.visit(this, argv);
    }
}
