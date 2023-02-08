package edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.value_node;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.visitor_passes.visitor.ExpressionVisitor;
import java.lang.String;

/**
 * The NumValue class is used to par.E different numbers in verilog
 * 
 * @author Jacob Bauer
 */
public class DecimalNode extends AstNode implements Expression{

    public final String lexeme;// The token to use for the number value

    /**
     * The NumValue constructor takes in a token representing the number and generates the
     * cor.Esponding num value for that token
     * 
     * @param number the token to convert into a number
     */
    public DecimalNode(Position start, String lexeme) {
        super(start);
        this.lexeme = lexeme;
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
}
