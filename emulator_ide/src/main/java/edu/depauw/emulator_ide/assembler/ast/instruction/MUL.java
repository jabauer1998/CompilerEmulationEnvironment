package edu.depauw.emulator_ide.assembler.ast.instruction;

import edu.depauw.emulator_ide.common.Position;

public class MUL extends Instruction{
    
    public MUL(byte cond, boolean a, boolean s, byte Rd, byte Rn, byte Rs, byte Rm, Position position){
	super(cond, position);
	super.storedValue |= (a ? 1 : 0) << 21;
	super.storedValue |= (s ? 1 : 0) << 20;
	super.storedValue |= (Rd & 0xf) << 16;
	super.storedValue |= (Rn & 0xf) << 12;
	super.storedValue |= (Rs & 0xf) << 8;
	super.storedValue |= 0b1001 << 4;
	super.storedValue |= Rm & 0xf;
    }

}
