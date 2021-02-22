package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.token.Token;
import java.lang.String;

public class NumValue extends Expression{
    private final Token number;
    
    public NumValue(Token number){
	super(number.getPosition());
	this.number = number;
    }

    public String getLexeme(){
	return number.getLexeme();
    }
}
