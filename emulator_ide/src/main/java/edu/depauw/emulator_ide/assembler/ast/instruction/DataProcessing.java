package edu.depauw.emulator_ide.assembler.ast.instruction;

import edu.depauw.emulator_ide.common.Position;

public abstract class DataProcessing extends Instruction{
    
    protected DataProcessing(byte cond, boolean immed, byte opCode, boolean condCode, byte Rn, byte Rd, byte B1, byte B2, Position position){
	super(cond, position);
	super.storedValue |= (immed ? 1 : 0) << 25;
	super.storedValue |= (opCode & 0xf) << 21;
	super.storedValue |= (condCode ? 1 : 0) << 20;
	super.storedValue |= (Rn & 0xf) << 16;
	super.storedValue |= (Rd & 0xf) << 12;
	if(immed){
	    super.storedValue |= (B1 & 0xf) << 8;
	    super.storedValue |= B2;
	} else {
	    super.storedValue |= B1 << 4;
	    super.storedValue |= B2 & 0xf;
	}
    }

}
