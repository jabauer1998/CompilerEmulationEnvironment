package edu.depauw.emulator_ide.verilog_compiler.ast.reg_value;

import edu.depauw.emulator_ide.verilog_compiler.visitor.RegValueVisitor;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.ConstantExpression;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Identifier;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;

/**The vectorslice class is used to parse a call to an array cell
 * @author Jacob Bauer
 */

public class OutputRegScalarArray extends RegValue{
    
    private ConstantExpression index1; //initial index to grap from the array
    private ConstantExpression index2; //final index to grab from the array
    private final Identifier ident; //name of the array

    /** The VectorCall constructor takes an identifier with up to twoindex to specify the sub array that is desired
     * @param ident name of the array
     * @param index1 min index of the array
     * @param index2 max index of the array
     */
    public OutputRegScalarArray(Identifier ident, ConstantExpression index1, ConstantExpression index2){
	super(ident.getPosition());
	this.index1 = index1;
	this.index2 = index2;
	this.ident = ident;
    }

    /** The name of the array
     * @param none
     */
    public Identifier getIdentifier(){
	return ident;
    }

    /** Returns the starting index
     * @param none
     */
    public Expression getExpression1(){
	return index1;
    }

    /** Returns the ending index
     * @param none
     */
    public Expression getExpression2(){
	return index2;
    }

     /** Returns the starting index
     * @param none
     */
    public void setExpression1(Expression exp){
	this.index1 = new ConstantExpression(exp);
    }

    /** Returns the ending index
     * @param none
     */
    public void setExpression2(Expression exp){
	this.index2 = new ConstantExpression(exp);
    }

    /**
     * The accept method is used to visit VectorCalls
     * @param RegValueVisitor the visitor object visiting the unary operation
     */
    
    public <RegValVisitType> RegValVisitType accept(RegValueVisitor<RegValVisitType> RegValueVisitor, Object... argv){
	return RegValueVisitor.visit(this, argv);
    }
}
