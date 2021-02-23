package edu.depauw.emulator_ide.verilog_compiler.ast.statement;

import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

public abstract class ForStatement extends Statement{
    
    private final Assignment init;
    private final Expression exp;
    private final Assignment change;
    
    public ForStatement(Assignment init, Expression exp, Assignment change){
	super(init.getPosition());
	this.exp = exp;
	this.init = init;
	this.change = change;
    }

    public Assignment getInit(){
	return init;
    }

    public Expression getExpression(){
	return exp;
    }

    public Assignment getChange(){
	return change;
    }
}
