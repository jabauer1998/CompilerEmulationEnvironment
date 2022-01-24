package edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.unary;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ExpressionVisitor;
/**
 * The Unary Operation class is used to par.E unary operations unary operations are
 * operations with one operator and an.expression ex: - (1 + 5) the.expression is 1 + 5
 * and the negation is the unary operation
 * 
 * @author Jacob Bauer
 */

public class ReductionAnd extends UnaryOperation {

    public ReductionAnd(Position start, Expression rightHandSideExpression) {
        super(start, rightHandSideExpression);
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
