package edu.depauw.emulator_ide.verilog_compiler.parser.ast.label;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.common.SymbolTable;
import edu.depauw.emulator_ide.verilog_compiler.data_structure.Pointer;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.ConstantExpression;
import edu.depauw.emulator_ide.verilog_compiler.visitor_passes.visitor.ExpressionVisitor;

/**
 * The vectorslice class is used to par.E a call to an array cell
 * 
 * @author Jacob Bauer
 */

public class Slice extends Label {
    public final ConstantExpression index1; // initial index to grap from the array
    public final ConstantExpression index2; // final index to grab from the array

    /**
     * The VectorElement constructor takes an identifier with up to twoindex to s Ecify the sub
     * array that is desired
     * 
     * @param ident  name of the array
     * @param index1 min index of the array
     * @param index2 max index of the array
     */
    public Slice(Position start, String vectorName, ConstantExpression index1, ConstantExpression index2) {
        super(start, vectorName);
        this.index1 = index1;
        this.index2 = index2;
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
