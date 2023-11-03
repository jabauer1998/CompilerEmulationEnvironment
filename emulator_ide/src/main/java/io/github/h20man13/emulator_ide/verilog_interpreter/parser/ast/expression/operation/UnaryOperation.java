package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.operation;


import java.lang.management.OperatingSystemMXBean;
import io.github.H20man13.emulator_ide.common.Position;
import io.github.H20man13.emulator_ide.common.debug.ErrorLog;
import io.github.H20man13.emulator_ide.common.debug.item.ErrorItem;
import io.github.H20man13.emulator_ide.verilog_interpreter.Utils;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.Environment;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.Value;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.AstNode;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import io.github.H20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.ExpressionVisitor;
/**
 * The Unary Operation class is used to par.E unary operations unary operations are
 * operations with one operator and an.expression ex: - (1 + 5) the.expression is 1 + 5
 * and the negation is the unary operation
 * 
 * @author Jacob Bauer
 */

public class UnaryOperation extends AstNode implements Expression {

    public final Expression rightHandSideExpression; // the right hand side of the equation
    public final Operator Op;
    public enum Operator{
        PLUS,
        MINUS,
        LNEG,
        BNEG
    }
    /**
     * The UnaryOperation constuctor is used to create a Unary operation
     * 
     * @param op               operator to use
     * @param right.expression to apply the operator to
     */

    public UnaryOperation(Position start, Operator Op, Expression rightHandSideExpression) {
        super(start);
        this.Op = Op;
        this.rightHandSideExpression = rightHandSideExpression;
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
