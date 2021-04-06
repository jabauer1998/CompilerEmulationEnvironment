package edu.depauw.emulator_ide.verilog_compiler.ast.statement;

import edu.depauw.emulator_ide.verilog_compiler.visitor.StatementVisitor;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

/**The Block Assignment class is used to parse blocking assignments
 * These assignments act like traditional assignments in a programming list
 *@author Jacob Bauer
 */

public class BlockAssign extends Statement{

    private final Expression lValue; //value on the left hand side of the assignment
    private final Expression exp; //value on the right hand side of the assignment

    /** The BlockAssign constuctor takes in two expressions:
     * @param lValue the value to be assigned to
     * @param exp the expression on the right hand side of the equals
     */
    public BlockAssign(Expression lValue, Expression exp){
	super(lValue.getPosition());
	this.lValue = lValue;
	this.exp = exp;
    }

    /** The getLvalue method returns an expression representing an Identifer or a Vector or a Concatenation
     * @param none
     */
    public Expression getLValue(){
	return lValue;
    }

    /** The get Expression method returns the expression on the right hand side od the =
     * @param none
     */
    public Expression getExpression(){
	return exp;
    }

    /** The accept method makes it possible so that nodes know which visitor object to call the visit method from. This is needed because the Visitor method is an interface not a class. All of the classes implementing ASTnode visitor will not have the required dependencies. 
     * @author Jacob Bauer
     */
    public <StatVisitType> StatVisitType accept(StatementVisitor<StatVisitType> statVisitor, Object... argv){
	return statVisitor.visit(this, argv);
    }
}
