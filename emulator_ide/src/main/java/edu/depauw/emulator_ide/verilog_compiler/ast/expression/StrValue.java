package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;
import edu.depauw.emulator_ide.verilog_compiler.token.Token;
import java.lang.String;

/** The StrValue Ast node class is used to Parse Strings
 * @author Jacob Bauer
 */
public class StrValue extends Expression{
    private final Token string; //token to hold the string value

    /** The StrValue constructor when provided a token produces the exprected string value ast node
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
	return string.getLexeme();
    }

    /** The accept method is a requirement to be able to visit StrValue nodes
     * @param none
     */
    public <ModVisitType, StatVisitType, ExprVisitType> ExprVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor){
	return astNodeVisitor.visit(this);
    }
}
