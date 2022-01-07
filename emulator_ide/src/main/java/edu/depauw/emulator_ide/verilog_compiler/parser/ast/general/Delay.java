package edu.depauw.emulator_ide.verilog_compiler.parser.ast.general;


import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;

public class Delay extends AstNode {

    private final Expression delayTime; // The delay time which is usually represented by and identiifer or an expression

    public Delay(Expression delayTime) {
        super(delayTime.getPosition());
        this.delayTime = delayTime;
    }

    public Expression getDelayTime(){ return delayTime; }
}
