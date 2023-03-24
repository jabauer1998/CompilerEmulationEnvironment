package edu.depauw.emulator_ide.verilog_interpreter.parser.ast.label;


import edu.depauw.emulator_ide.common.Pointer;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.common.SymbolTable;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_interpreter.visitor_passes.visitor.ExpressionVisitor;

/**
 * The VectorElement class is used to par.E a call to an array cell
 * 
 * @author Jacob Bauer
 */

public abstract class Label extends AstNode implements Expression, LValue {

    public final String     labelIdentifier;  // name of the array

    /**
     * The VectorElement constructor takes an identifier with up to twoindex to s Ecify the sub
     * array that is desired
     * 
     * @param ident  name of the array
     * @param index1 min index of the array
     * @param index2 max index of the array
     */

    protected Label(Position start, String labelIdentifier) {
        super(start);
        this.labelIdentifier = labelIdentifier;
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
    abstract public <ExprVisitType> ExprVisitType accept(ExpressionVisitor<ExprVisitType> exprVisitor, Object... argv);
}
