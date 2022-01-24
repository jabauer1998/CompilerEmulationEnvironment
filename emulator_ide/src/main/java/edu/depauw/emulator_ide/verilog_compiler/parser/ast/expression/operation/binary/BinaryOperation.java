package edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ExpressionVisitor;

/**
 * The Binary operation class is used to par.E binary operations a binary operation
 * takes the format of.expression Op Expression Ex: 1 + 1 or 1 + (3 - 2) where (3 - 2)
 * is the.expression on the right hand side of the operator
 * 
 * @author Jacob Bauer
 */
abstract class BinaryOperation extends AstNode implements Expression {

    public Expression  right; // Expression to the right of the operator
    public Expression  left;  // Expression to the left of the operator

    /**
     * This is the consructor for creating Binary.expressions it has the following
     * arguments:
     * @param start the starting positon in the token stream of the ast node
     * @param left  exprVisitoresson on the left side of the operator
     * @param op    operator in the midd E of the binary operation
     * @param right exprVisitoresson on the right side of the operator
     */
    protected BinaryOperation(Position start, Expression left, Expression right) {
        super(start);
        this.left = left;
        this.right = right;
    }

    public abstract <ExprVisitType> ExprVisitType accept(ExpressionVisitor<ExprVisitType> exprVisitor, Object... argv);
}
