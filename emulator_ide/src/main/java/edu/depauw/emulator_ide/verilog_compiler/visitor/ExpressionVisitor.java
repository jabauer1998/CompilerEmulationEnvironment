package edu.depauw.emulator_ide.verilog_compiler.visitor;

import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.*;
    
public interface ExpressionVisitor<ExprVisitType>{

    /**
     * This is the code for visiting binary operations
     * @param op
     */
    
    public ExprVisitType visit(BinaryOperation op, Object... argv);

    /**
     * This is the code for visiting unary operations
     * @param op
     */
    
    public ExprVisitType visit(UnaryOperation op, Object... argv);


    /**
     * This is the code for visiting concatenations
     * @param concat
     */
    
    public ExprVisitType visit(Concatenation concat, Object... argv);

    /**
     * This is the code for visiting Constant Expressions
     * @param expr
     */
    
    public ExprVisitType visit(ConstantExpression expr, Object... argv);

    /**
     * This is the code for visiting Empty Expressions
     * @param expr
     */
    
    public ExprVisitType visit(EmptyExpression  expr, Object... argv);

    /**
     * This is the code for visiting Function Calls
     * @param call
     */
    
    public ExprVisitType visit(FunctionCall call, Object... argv);

    /**
     * This is the code for visiting Function Calls
     * @param call
     */
    
    public ExprVisitType visit(SystemFunctionCall call, Object... argv);

    /**
     * This is the code for visiting an Identifier
     * @param ident
     */
    
    public ExprVisitType visit(Identifier ident, Object... argv);

    /**
     * This is the code for visiting an Number in verilog
     * @param number
     */
    
    public ExprVisitType visit(NumValue number, Object... argv);

    /**
     * This is the code for visiting a port connection in verilog
     * @param connection
     */
    
    public ExprVisitType visit(PortConnection connection, Object... argv);

    /**
     * This is the code for visiting a string in verilog
     * @param string
     */
    
    public ExprVisitType visit(StrValue string, Object... argv);

    /**
     * This is the code for visiting a TernaryOperation in verilog
     * @param expr
     */
    
    public ExprVisitType visit(TernaryExpression expr, Object... argv);

    /**
     * This is the code for visiting a Vector in verilog
     * @param string
     */
    
    public ExprVisitType visit(VectorCall string, Object... argv);

    /**
     * This is the code for visiting a Vector in verilog
     * @param string
     */
    
    public ExprVisitType visit(VectorSlice string, Object... argv);
    
}
