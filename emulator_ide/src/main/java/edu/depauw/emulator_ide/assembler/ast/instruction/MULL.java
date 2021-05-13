package edu.depauw.emulator_ide.assembler.ast.instruction;

import edu.depauw.emulator_ide.common.Position;

public class MULL extends Instruction{
    
    public MULL(int cond, boolean u, boolean a, boolean s, int RdHi, int RdLow, int Rs, int Rm, Position position){
	super(cond, position);
	super.storedValue |= 0b00001 << 23;
	super.storedValue |= (u ? 1 : 0) << 22;
	super.storedValue |= (a ? 1 : 0) << 21;
	super.storedValue |= (s ? 1 : 0) << 20;
	super.storedValue |= (RdHi & 0xf) << 16;
	super.storedValue |= (RdLow & 0xf) << 12;
	super.storedValue |= (Rs & 0xf) << 8;
	super.storedValue |= 0b1001 << 4;
	super.storedValue |= Rm & 0xf;
    }

}
