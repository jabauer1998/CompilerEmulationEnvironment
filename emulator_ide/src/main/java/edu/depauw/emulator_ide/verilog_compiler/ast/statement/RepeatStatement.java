package edu.depauw.emulator_ide.verilog_compiler.ast.statement;

import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.visitor.StatementVisitor;
import edu.depauw.emulator_ide.common.Position;

public class RepeatStatement extends Statement{
    
    private final Statement stat; //Statement
    private Expression exp; //Expression
    
    public RepeatStatement(Expression exp, Statement stat){
	super(exp.getPosition());
	this.stat = stat;
	this.exp = exp;
    }

    public Statement getStatement(){
	return stat;
    }

    public Expression getExpression(){
	return exp;
    }

    public void setExpression(Expression lValue){
	this.exp = exp;
    }

    /** The accept method makes it possible so that nodes know which visitor object to call the visit method from. This is needed because the Visitor method is an interface not a class. All of the classes implementing ASTnode visitor will not have the required dependencies. 
     * @author Jacob Bauer
     */
    public <StatVisitType> StatVisitType accept(StatementVisitor<StatVisitType> statVisitor, Object... argv){
	return statVisitor.visit(this, argv);
    }
}
