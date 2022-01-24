package edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary;

import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ExpressionVisitor;

public class Add extends BinaryOperation{
    public Add(Position start, Expression left, Expression right){
        super(start, left, right);
    }

    public <ExprVisitType> ExprVisitType accept(ExpressionVisitor<ExprVisitType> exprVisitor, Object... argv){
        return exprVisitor.visit(this, argv);
    }
}
