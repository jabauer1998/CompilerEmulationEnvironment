package edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.function_call;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.procedure_declaration.FunctionDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.Environment;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.Value;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ExpressionVisitor;
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

    private final String     functionName; // name of the function
    private final List<Expression> argumentList;      // parameter Expressions

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
     * This function returns an identifier representing the function name
     * 
     * @param none
     */
    public String getSystemFunctionName(){ return this.functionName; }

    /**
     * This function returns an.expression from the expression list at the s Ecified index
     * 
     * @param index of the.expression
     */
    public Expression getArgument(int index){ return argumentList.get(index); }

    /**
     * This function returns the.expression List size
     * 
     * @param none
     */
    public int getNumberOfArguments(){ return argumentList.size(); }

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
