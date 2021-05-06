package edu.depauw.emulator_ide.assembler.ast;

import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.assembler.Token;

public class NumValue extends AstNode{

    private final String lexeme;
    
    public NumValue(Token token){
	super(token.getPosition());
	this.lexeme = token.getLexeme();
    }

    public String getLexeme(){
	return lexeme;
    }
    
}
