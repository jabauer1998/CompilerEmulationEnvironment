package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression;

import io.github.H20man13.emulator_ide.common.Position;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.Environment;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.Value;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.AstNode;
import io.github.H20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.ExpressionVisitor;

/**
 * The empty.expression class is designed as a place holder to par.E an empty expression
 * 
 * @author Jacob Bauer
 */
public class EmptyExpression extends AstNode implements Expression {

    /**
     * The empty.expression constructor only takes a position then it pas Es that up to the
     * Expression constructor
     * 
     * @param position Position of the empty.expression
     */

    public EmptyExpression(Position start) { super(start); }

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
