package edu.depauw.emulator_ide.assembler.ast;

import edu.depauw.emulator_ide.assembler.Token;
import edu.depauw.emulator_ide.common.Position;

public class Label extends AstNode{

    private final Identifier ident;
    
    public Label(Position position, Token token){
	super(position);
	this.ident = new Identifier(token);
    }

    public Identifier getName(){
	return ident;
    }
    
}
