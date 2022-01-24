package edu.depauw.emulator_ide.verilog_compiler.passes.visitor;


import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.function_call.FunctionCall;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.function_call.SystemFunctionCall;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.Concatenation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.TernaryOperation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.unary.BitwiseNegation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.unary.LogicalNegation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.unary.Negation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.unary.ReductionAnd;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.unary.ReductionNand;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.unary.ReductionNor;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.unary.ReductionOr;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.unary.ReductionXnor;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.unary.ReductionXor;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.value_node.BinaryNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.value_node.DecimalNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.value_node.HexadecimalNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.value_node.OctalNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.value_node.StringNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.label.Element;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.label.Identifier;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.label.Slice;

public interface ExpressionVisitor<ExprVisitType> {

    /**
     * This is the code for visiting binary addition operations
     * 
     * @param op
     */

    public ExprVisitType visit(Add op, Object... argv);

    /**
     * This is the code for visiting binary addition operations
     * 
     * @param op
     */

    public ExprVisitType visit(Subtract op, Object... argv);

    /**
     * This is the code for visiting binary addition operations
     * 
     * @param op
     */

    public ExprVisitType visit(Multiply op, Object... argv);

    /**
     * This is the code for visiting binary addition operations
     * 
     * @param op
     */

    public ExprVisitType visit(Divide op, Object... argv);

    /**
     * This is the code for visiting binary addition operations
     * 
     * @param op
     */

    public ExprVisitType visit(Modulo op, Object... argv);

    /**
     * This is the code for visiting bitwise and operations
     * 
     * @param op
     */

    public ExprVisitType visit(BitwiseAnd op, Object... argv);

     /**
     * This is the code for visiting bitwise and operations
     * 
     * @param op
     */

    public ExprVisitType visit(BitwiseNand op, Object... argv);

    /**
     * This is the code for visiting logical and operations
     * 
     * @param op
     */

    public ExprVisitType visit(LogicalAnd op, Object... argv);

    /**
     * This is the code for visiting unary operations
     * 
     * @param op
     */

    public ExprVisitType visit(BitwiseOr op, Object... argv);

     /**
     * This is the code for visiting unary operations
     * 
     * @param op
     */

    public ExprVisitType visit(BitwiseNor op, Object... argv);

    /**
     * This is the code for visiting logical and operations
     * 
     * @param op
     */

    public ExprVisitType visit(LogicalOr op, Object... argv);

     /**
     * This is the code for bitwise or operations
     * 
     * @param op
     */

    public ExprVisitType visit(BitwiseXor op, Object... argv);

    /**
     * This is the code for bitwise or operations
     * 
     * @param op
     */

    public ExprVisitType visit(BitwiseXnor op, Object... argv);

    /**
     * This is the code for bitwise or operations
     * 
     * @param op
     */

    public ExprVisitType visit(LessThan op, Object... argv);

     /**
     * This is the code for bitwise or operations
     * 
     * @param op
     */

    public ExprVisitType visit(GreaterThan op, Object... argv);

    /**
     * This is the code for bitwise or operations
     * 
     * @param op
     */

    public ExprVisitType visit(GreaterThanOrEqualTo op, Object... argv);

    /**
     * This is the code for bitwise or operations
     * 
     * @param op
     */

    public ExprVisitType visit(LessThanOrEqualTo op, Object... argv);

     /**
     * This is the code for bitwise or operations
     * 
     * @param op
     */

    public ExprVisitType visit(BasicEquality op, Object... argv);

    /**
     * This is the code for bitwise or operations
     * 
     * @param op
     */

    public ExprVisitType visit(BasicInequality op, Object... argv);

     /**
     * This is the code for bitwise or operations
     * 
     * @param op
     */

    public ExprVisitType visit(StrictEquality op, Object... argv);

    /**
     * This is the code for bitwise or operations
     * 
     * @param op
     */

    public ExprVisitType visit(StrictInequality op, Object... argv);

    /**
     * This is the code for bitwise or operations
     * 
     * @param op
     */

    public ExprVisitType visit(BitshiftLeft op, Object... argv);

    /**
     * This is the code for bitwise or operations
     * 
     * @param op
     */

    public ExprVisitType visit(BitshiftRight op, Object... argv);

    

    /**
     * This is the code for visiting unary operations
     * 
     * @param op
     */

    public ExprVisitType visit(LogicalNegation op, Object... argv);

    /**
     * This is the code for visiting unary operations
     * 
     * @param op
     */

    public ExprVisitType visit(BitwiseNegation op, Object... argv);

    /**
     * This is the code for visiting unary operations
     * 
     * @param op
     */

    public ExprVisitType visit(ReductionAnd op, Object... argv);

     /**
     * This is the code for visiting unary operations
     * 
     * @param op
     */

    public ExprVisitType visit(ReductionOr op, Object... argv);

     /**
     * This is the code for visiting unary operations
     * 
     * @param op
     */

    public ExprVisitType visit(ReductionNand op, Object... argv);

     /**
     * This is the code for visiting unary operations
     * 
     * @param op
     */

    public ExprVisitType visit(ReductionNor op, Object... argv);

     /**
     * This is the code for visiting unary operations
     * 
     * @param op
     */

    public ExprVisitType visit(ReductionXor op, Object... argv);

    /**
     * This is the code for visiting unary operations
     * 
     * @param op
     */

    public ExprVisitType visit(ReductionXnor op, Object... argv);


    /**
     * This is the code for visiting unary operations
     * 
     * @param op
     */

    public ExprVisitType visit(Negation op, Object... argv);

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
