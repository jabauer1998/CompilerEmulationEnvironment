package io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.expression.function_call;


import io.github.h20man13.emulator_ide.common.Position;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.Environment;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.Value;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.AstNode;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.procedure_declaration.FunctionDeclaration;
import io.github.h20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.ExpressionVisitor;
import java.util.List;
import javax.management.RuntimeErrorException;
import java.util.Collections;

/**
 * The FunctionCall is used to call functions Functions are different than tasks because
 * they have a return value
 * 
 * @author Jacob Bauer
 */

public class FunctionCall extends AstNode implements Expression {

    public final String     functionName; // name of the function
    public final List<Expression> argumentList;      // parameter Expressions

    /**
     * The FunctionCall constructor takes two arguments:
     * 
     * @param functionName name of the function
     */
    public FunctionCall(Position start, String functionName, List<Expression> argumentList) {
        super(start);
        this.functionName = functionName;
        this.argumentList = Collections.unmodifiableList(argumentList);
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
