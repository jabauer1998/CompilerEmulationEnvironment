package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.label;


import io.github.H20man13.emulator_ide.common.Pointer;
import io.github.H20man13.emulator_ide.common.Position;
import io.github.H20man13.emulator_ide.common.SymbolTable;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import io.github.H20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.ExpressionVisitor;

/**
 * The VectorElement class is used to par.E a call to an array cell
 * 
 * @author Jacob Bauer
 */

public class Element extends Label {

    public final Expression       index1; // initial index to grap from the array

    /**
     * The VectorElement constructor takes an identifier with up to twoindex to s Ecify the sub
     * array that is desired
     * 
     * @param ident  name of the array
     * @param index1 min index of the array
     * @param index2 max index of the array
     */

    public Element(Position start, String ident, Expression index1) {
        super(start, ident);
        this.index1 = index1;
    }

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
