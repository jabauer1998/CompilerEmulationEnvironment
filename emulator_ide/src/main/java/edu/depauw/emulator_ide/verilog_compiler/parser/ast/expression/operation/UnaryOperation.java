package edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation;


import java.lang.management.OperatingSystemMXBean;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.common.debug.ErrorLog;
import edu.depauw.emulator_ide.common.debug.item.ErrorItem;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.Environment;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.Value;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.util.OpUtil;
import edu.depauw.emulator_ide.verilog_compiler.visitor_passes.visitor.ExpressionVisitor;
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
