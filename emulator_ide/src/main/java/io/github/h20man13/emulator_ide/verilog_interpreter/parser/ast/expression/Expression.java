package io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.expression;
import io.github.h20man13.emulator_ide.common.debug.ErrorLog;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.Environment;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.Value;
import io.github.h20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.ExpressionVisitor;

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
