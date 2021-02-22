package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.token.Token;
import java.lang.String;

public class StrValue extends Expression{
    private final Token string;
    
    public StrValue(Token string){
	super(string.getPosition());
	this.string = string;
    }

    public String getLexeme(){
	return string.getLexeme();
    }
}
