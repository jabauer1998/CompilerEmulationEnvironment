package edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.function_call;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_interpreter.visitor_passes.visitor.ExpressionVisitor;
import java.util.List;

/**
 * The FunctionCall is used to call functions Functions are different than tasks because
 * they have a return value
 * 
 * @author Jacob Bauer
 */

public class SystemFunctionCall extends FunctionCall {
    
    public SystemFunctionCall(Position start, String functionName, List<Expression> argumentList) {
        super(start, functionName, argumentList);
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
