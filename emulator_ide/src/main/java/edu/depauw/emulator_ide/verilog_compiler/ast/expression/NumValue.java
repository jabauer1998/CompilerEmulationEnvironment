package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;
import edu.depauw.emulator_ide.verilog_compiler.token.Token;
import java.lang.String;

/** The NumValue class is used to parse different numbers in verilog
 * @author Jacob Bauer
 */
public class NumValue extends Expression{

    private final Token number;//The token to use for the number value

    /** The NumValue constructor takes in a token representing the number and generates the corresponding num value for that token
     *@param number the token to convert into a number
     */
    public NumValue(Token number){
	super(number.getPosition());
	this.number = number;
    }

    /** The Lexeme of the number
     * @param none
     */
    public String getLexeme(){
	return number.getLexeme();
    }

    /**The accept method will make it so the visitor interface will work
     * @param astNodeVisitor the visitor object we want to use to visit another member of a class
     */
    public <ModVisitType, StatVisitType, ExprVisitType> ExprVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor, Object... argv){
	return astNodeVisitor.visit(this, argv);
    }
}
