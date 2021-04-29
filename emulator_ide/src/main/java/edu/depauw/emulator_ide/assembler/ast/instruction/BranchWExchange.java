package edu.depauw.emulator_ide.assembler.ast.instruction;

import edu.depauw.emulator_ide.common.Position;

public abstract class BranchWExchange extends Instruction{
    
    protected BranchWExchange(byte cond, byte Rn, Position position){
	super(cond, position);
	super.storedValue |= 0b000100101111111111110001 << 4;
	super.storedValue |= Rn & 0Xf;
    }

}
