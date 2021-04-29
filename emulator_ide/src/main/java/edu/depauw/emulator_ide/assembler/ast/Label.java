package edu.depauw.emulator_ide.assembler.ast.label;

import edu.depauw.emulator_ide.common.Position;

public class Label extends AstNode{

    private final Identifier ident;
    
    public AstNode(Position position, Token token){
	super(position);
	this.ident = new Identifier(token);
    }

    public Identifier getName(){
	return ident;
    }
    
}
