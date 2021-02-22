package edu.depauw.emulator_ide.verilog_compiler.ast;

import edu.depauw.emulator_ide.verilog_compiler.token.Position;
    
public class AstNode{
    private final Position position; //field to store the starting position of an ast node

    protected AstNode(Position position){
	this.position = position;
    }

    public Position getPosition(){
	return position;
    }
}
