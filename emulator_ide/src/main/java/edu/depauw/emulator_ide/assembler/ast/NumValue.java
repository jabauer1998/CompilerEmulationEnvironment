package edu.depauw.emulator_ide.assembler.ast.label;

import edu.depauw.emulator_ide.common.Position;

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
