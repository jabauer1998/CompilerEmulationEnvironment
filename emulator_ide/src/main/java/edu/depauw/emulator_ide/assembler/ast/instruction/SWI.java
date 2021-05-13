package edu.depauw.emulator_ide.assembler.ast.instruction;

import edu.depauw.emulator_ide.common.Position;

public class SWI extends Instruction{
    
    public SWI(int cond, int command, Position position){
    	super(cond, position);
    	super.storedValue |= command;
    	super.storedValue |= 0b1111 << 24;
    }
}
