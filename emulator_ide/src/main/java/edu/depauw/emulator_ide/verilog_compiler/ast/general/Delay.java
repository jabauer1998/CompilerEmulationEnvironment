package edu.depauw.emulator_ide.verilog_compiler.ast.general;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;

public class Delay extends AstNode{

    private final Expression delayTime;

    public Delay(Expression delayTime){
	super(delayTime.getPosition());
	this.delayTime = delayTime;
    }

    public Expression getDelayTime(){
	return delayTime;
    }
}
