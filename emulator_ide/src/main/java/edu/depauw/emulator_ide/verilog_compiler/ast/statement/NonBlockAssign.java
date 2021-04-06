package edu.depauw.emulator_ide.verilog_compiler.ast.statement;

import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.visitor.StatementVisitor;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

public class NonBlockAssign extends Statement{

    private final Expression lValue;
    private final Expression exp;
    
    public NonBlockAssign(Expression lValue, Expression exp){
	super(lValue.getPosition());
	this.lValue = lValue;
	this.exp = exp;
    }

    public Expression getLValue(){
	return lValue;
    }

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
