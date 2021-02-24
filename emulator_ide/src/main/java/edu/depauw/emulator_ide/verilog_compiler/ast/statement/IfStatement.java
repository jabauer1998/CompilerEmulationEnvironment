package edu.depauw.emulator_ide.verilog_compiler.ast.statement;

import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

public class IfStatement extends Statement{

    
    private final Expression exp; //expression 
    private final Statement stat; //statement
    
    public IfStatement(Expression exp, Statement stat){
	super(exp.getPosition());
	this.exp = exp;
	this.stat = stat;
    }

    public Expression getExpression(){
	return exp;
    }

    public Statement getStatement(){
	return stat;
    }
}
