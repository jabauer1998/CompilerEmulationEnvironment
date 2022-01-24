package edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.unary;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ExpressionVisitor;
/**
 * The Unary Operation class is used to par.E unary operations unary operations are
 * operations with one operator and an.expression ex: - (1 + 5) the.expression is 1 + 5
 * and the negation is the unary operation
 * 
 * @author Jacob Bauer
 */

public abstract class UnaryOperation extends AstNode implements Expression {

    private final Expression rightHandSideExpression; // the right hand side of the equation

    /**
     * The UnaryOperation constuctor is used to create a Unary operation
     * 
     * @param op               operator to use
     * @param right.expression to apply the operator to
     */

    public UnaryOperation(Position start, Expression rightHandSideExpression) {
        super(start);
        this.rightHandSideExpression = rightHandSideExpression;
    }

    /**
     * The right operator is used to get the right hand side of an.expression
     * 
     * @param none
     */

    public Expression getRighthandSide(){ return rightHandSideExpression; }

    /**
     * The accept method will make it so the visitor interface will work
     * 
     * @param astNodeVisitor the visitor object we want to use to visit another member of a
     *                       class
     */
    abstract public <ExprVisitType> ExprVisitType accept(ExpressionVisitor<ExprVisitType> exprVisitor, Object... argv);
}
