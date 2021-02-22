package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.token.Token;

import java.lang.String;

public class Identifier extends Expression{

    private final Token ident;
	
    public Identifier(Token ident){
	super(ident.getPosition());
	this.ident = ident;
    }

    public String getLexeme(){
	return ident.getLexeme();
    }
    
}
