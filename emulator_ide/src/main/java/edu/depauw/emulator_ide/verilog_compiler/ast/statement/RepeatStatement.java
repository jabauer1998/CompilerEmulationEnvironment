package edu.depauw.emulator_ide.verilog_compiler.ast.statement;

import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

public class RepeatStatement extends Statement{
    
    private final Statement stat; //Statement
    private final Expression exp; //Expression
    
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
}
