package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.operation;


import io.github.H20man13.emulator_ide.common.Position;
import io.github.H20man13.emulator_ide.common.debug.ErrorLog;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.Environment;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.Value;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.AstNode;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import io.github.H20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.ExpressionVisitor;

/**
 * The Ternary.expression class was designed to par.E Ternary expressions Ex: (i == x) ?
 * x : y it kind of works like an if else statement
 * 
 * @author Jacob Bauer
 */
public class TernaryOperation extends AstNode implements Expression {
    public final Expression condition; // the condition phrase of the ternary Expression
    public final Expression ifTrue;      // the Expression to the left of the colon
    public final Expression ifFalse;     // the Expression to the right of the colon

    /**
     * Th ternary.expression takes in 3 expressions only one of which it returns. It can
     * return the one on the left hand side if the colon or the right.
     * 
     * @param condition the condition to be evaluated
     * @param left      the.expression to return if the condition is tr.E
     * @param right     the exprVisitoresson to evaluate if the condition is false
     */
    public TernaryOperation(Position start, Expression condition, Expression ifTrue, Expression ifFalse) {
        super(start);
        this.condition = condition;
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
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
