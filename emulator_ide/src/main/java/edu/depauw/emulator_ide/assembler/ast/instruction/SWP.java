package edu.depauw.emulator_ide.assembler.ast.instruction;

import edu.depauw.emulator_ide.common.Position;

public class SWP extends Instruction{
    
    public SWP(byte cond, boolean b, byte Rn, byte Rd, byte Rm, Position position){
	super(cond, position);
	super.storedValue |= 0b00010 << 23;
	super.storedValue |= (b ? 1 : 0) << 22;
	super.storedValue |= (Rn & 0xf) << 16;
	super.storedValue |= (Rd & 0xf) << 12;
	super.storedValue |= 0b1001 << 4;
	super.storedValue |= Rm & 0xf;
    }
}
