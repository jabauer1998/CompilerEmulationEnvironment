package edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation;


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
 * The Binary operation class is used to par.E binary operations a binary operation
 * takes the format of.expression Op Expression Ex: 1 + 1 or 1 + (3 - 2) where (3 - 2)
 * is the.expression on the right hand side of the operator
 * 
 * @author Jacob Bauer
 */
public class BinaryOperation extends AstNode implements Expression {

    public Expression  right; // Expression to the right of the operator
    public Expression  left;  // Expression to the left of the operator
    public Operator Op; 

    public enum Operator{
        PLUS,
        MINUS,
        TIMES,
        DIV,
        MOD,
        EQ2,
        EQ3,
        NE1,
        NE2,
        LAND,
        LOR,
        LE,
        LT,
        GE,
        GT,
        BAND,
        BOR,
        BXOR,
        BXNOR,
        LSHIFT,
        RSHIFT
    }

    /**
     * This is the consructor for creating Binary.expressions it has the following
     * arguments:
     * @param start the starting positon in the token stream of the ast node
     * @param left  exprVisitoresson on the left side of the operator
     * @param op    operator in the midd E of the binary operation
     * @param right exprVisitoresson on the right side of the operator
     */
    public BinaryOperation(Position start, Expression left, Operator Op, Expression right) {
        super(start);
        this.left = left;
        this.Op = Op;
        this.right = right;
    }

    public <ExprVisitType> ExprVisitType accept(ExpressionVisitor<ExprVisitType> exprVisitor, Object... argv){
        return exprVisitor.visit(this, argv);
    }
}
