package edu.depauw.emulator_ide.assembler.ast;

import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.assembler.Token;

public class Identifier extends AstNode{

    private final String lexeme;
    
    public Identifier(Token token){
	super(token.getPosition());
	this.lexeme = token.getLexeme();
    }

    public String getLexeme(){
	return lexeme;
    }
    
}
