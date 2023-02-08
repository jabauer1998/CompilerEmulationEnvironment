package edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression;
import edu.depauw.emulator_ide.common.debug.ErrorLog;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.Environment;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.Value;
import edu.depauw.emulator_ide.verilog_compiler.visitor_passes.visitor.ExpressionVisitor;

/**
 * The.expression class is an extention of the AstNode class This class is used as a
 * supertype for all.expression objects
 * 
 * @author Jacob Bauer
 */
public interface Expression {

    /**
     * The accept method will make it so the visitor interface will work
     * 
     * @param astNodeVisitor the visitor object we want to use to visit another member of a
     *                       class
     */
    public <ExprVisitType> ExprVisitType accept(ExpressionVisitor<ExprVisitType> exprVisitor, Object... argv);

}
