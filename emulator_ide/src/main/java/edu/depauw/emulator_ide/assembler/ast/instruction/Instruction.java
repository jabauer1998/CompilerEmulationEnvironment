package edu.depauw.emulator_ide.assembler.ast.instruction;

import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.assembler.ast.AstNode;

public abstract class Instruction extends AstNode{

    protected int storedValue;
    
    protected Instruction(byte cond, Position position){
	super(position);
	this.storedValue = (cond & 0xf) << 28;
    }

    public String genCode(){
	StringBuilder str = new StringBuilder();
	int value = storedValue;
	while(value > 0){
	    char mod = (char)((value % 2) | 0x30);
	    str.append(mod);
	    value /= 2;
	}
	while(str.length() < 32){
	    str.append('0');
	}
	str.reverse();
	str.append('\n');
	return str.toString();
    }
    
}
