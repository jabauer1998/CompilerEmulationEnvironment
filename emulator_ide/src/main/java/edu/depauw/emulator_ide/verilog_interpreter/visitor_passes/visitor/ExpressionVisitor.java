package edu.depauw.emulator_ide.verilog_interpreter.visitor_passes.visitor;


import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.*;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.function_call.FunctionCall;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.function_call.SystemFunctionCall;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.operation.BinaryOperation;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.operation.Concatenation;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.operation.TernaryOperation;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.operation.UnaryOperation;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.value_node.BinaryNode;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.value_node.DecimalNode;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.value_node.HexadecimalNode;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.value_node.OctalNode;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.value_node.StringNode;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.label.Element;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.label.Identifier;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.label.Slice;

public interface ExpressionVisitor<ExprVisitType> {

    public ExprVisitType visit(BinaryOperation Op, Object... argv);

    public ExprVisitType visit(UnaryOperation Op, Object... argv);

    /**
     * This is the code for visiting concatenations
     * 
     * @param concat
     */

    public ExprVisitType visit(Concatenation concat, Object... argv);

    /**
     * This is the code for visiting Constant Expressions
     * 
     * @param expr
     */

    public ExprVisitType visit(ConstantExpression expr, Object... argv);

    /**
     * This is the code for visiting Empty Expressions
     * 
     * @param expr
     */

    public ExprVisitType visit(EmptyExpression expr, Object... argv);

    /**
     * This is the code for visiting Function Calls
     * 
     * @param call
     */

    public ExprVisitType visit(FunctionCall call, Object... argv);

    /**
     * This is the code for visiting Function Calls
     * 
     * @param call
     */

    public ExprVisitType visit(SystemFunctionCall call, Object... argv);

    /**
     * This is the code for visiting an Identifier
     * 
     * @param ident
     */

    public ExprVisitType visit(Identifier ident, Object... argv);

    /**
     * This is the code for visiting an Number in verilog
     * 
     * @param number
     */

    public ExprVisitType visit(BinaryNode number, Object... argv);

    /**
     * This is the code for visiting an Number in verilog
     * 
     * @param number
     */

    public ExprVisitType visit(DecimalNode number, Object... argv);

    /**
     * This is the code for visiting an Number in verilog
     * 
     * @param number
     */

    public ExprVisitType visit(HexadecimalNode number, Object... argv);

    /**
     * This is the code for visiting an Number in verilog
     * 
     * @param number
     */

    public ExprVisitType visit(OctalNode number, Object... argv);

    /**
     * This is the code for visiting a port connection in verilog
     * 
     * @param connection
     */

    public ExprVisitType visit(PortConnection connection, Object... argv);

    /**
     * This is the code for visiting a string in verilog
     * 
     * @param string
     */

    public ExprVisitType visit(StringNode string, Object... argv);

    /**
     * This is the code for visiting a TernaryOperation in verilog
     * 
     * @param expr
     */

    public ExprVisitType visit(TernaryOperation expr, Object... argv);

    /**
     * This is the code for visiting a Vector in verilog
     * 
     * @param string
     */

    public ExprVisitType visit(Element string, Object... argv);

    /**
     * This is the code for visiting a Vector in verilog
     * 
     * @param string
     */

    public ExprVisitType visit(Slice string, Object... argv);

}
