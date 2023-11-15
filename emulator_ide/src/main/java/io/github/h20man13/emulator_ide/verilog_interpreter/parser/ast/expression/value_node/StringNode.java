package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.value_node;


import io.github.H20man13.emulator_ide.common.Position;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.AstNode;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import io.github.H20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.ExpressionVisitor;
import java.lang.String;

/**
 * The StrValue Ast node class is used to Par.E Strings
 * 
 * @author Jacob Bauer
 */
public class StringNode extends AstNode implements Expression {
    public final String lexeme; // token to hold the string value

    /**
     * The StrValue constructor when provided a token produces the exprVisitorected string
     * value ast node
     * 
     * @param string token representing the string value to be created
     */
    public StringNode(Position start, String lexeme) {
        super(start);
        this.lexeme = lexeme.substring(1, lexeme.length() - 1); //take away the \" marks around the string lexeme
    }

    /**
     * The accept method will make it so the visitor interface will work
     * 
     * @param astNodeVisitor the visitor object we want to use to visit another member of a
     *                       class
     */
    public <ExprVisitType> ExprVisitType accept(ExpressionVisitor<ExprVisitType> exprVisitor, Object... argv){
        return exprVisitor.visit(this, argv);
    }

    @Override
    public String toString(){ // TODO Auto-generated method stub
        return lexeme;
    }
}
