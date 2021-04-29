package edu.depauw.emulator_ide.assembler.ast;

import edu.depauw.emulator_ide.common.Position;

public abstract class AstNode{

    private Position position;
    
    protected AstNode(Position position){
	this.position = position;
    }
    
}
