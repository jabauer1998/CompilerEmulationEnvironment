package edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation;

import java.util.List;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.data_structure.Pointer;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.label.LValue;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ExpressionVisitor;
import edu.depauw.emulator_ide.verilog_compiler.symbol_table.SymbolTable;

/**
 * The Concatenation class is used to par.E concatenation.expressions
 * Concatenation.expressions are used to group Wires together from different nets
 * 
 * @author Jacob Bauer
 */
public class Concatenation extends AstNode implements Expression, LValue {

    private final List<Expression> circuitElementExpressionList; // list of Expressions to concatenate

    /**
     * The concatenation.expression is used to Concatenate multiple instances
     * 
     * @param expList the.expressions which to concatenate
     */
    public Concatenation(Position start, List<Expression>  circuitElementExpressionList) {
        super(start);
        this.circuitElementExpressionList =  circuitElementExpressionList;
    }

    /**
     * To get an.expression from a s Ecific index
     * 
     * @param index index in exprVisitoresson list to fetch from
     */
    public Expression getCircuitElementExpression(int index){ return circuitElementExpressionList.get(index); }

    /**
     * Get the size of the.expression list for which to concatenate
     * 
     * @param none
     */
    public int getNumCircuitElementExpressions(){ return circuitElementExpressionList.size(); }

    public <DataType> Pointer<DataType> getLValue(SymbolTable<Pointer<DataType>> table){
        return null;
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
