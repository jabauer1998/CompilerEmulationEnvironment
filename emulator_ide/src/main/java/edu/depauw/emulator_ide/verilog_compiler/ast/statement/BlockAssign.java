package edu.depauw.emulator_ide.verilog_compiler.ast.statement;

import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

public class BlockAssign extends Statement{

    private final Expression lValue;
    private final Expression exp;
    
    public BlockAssign(Expression lValue, Expression exp){
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
}
