package edu.depauw.emulator_ide.assembler.ast.instruction;

import edu.depauw.emulator_ide.common.Position;

public class MRS extends Instruction{
    
    public MRS(byte cond, boolean source, byte Rd, Position position){
	super(cond, position);
	super.storedValue |= 0b00010 << 23;
	super.storedValue |= (source ? 1 : 0) << 22;
	super.storedValue |= 0b001111 << 16;
	super.storedValue |= (Rd & 0xf) << 12;
    }

}
