package edu.depauw.emulator_ide.assembler.ast.instruction;

import edu.depauw.emulator_ide.common.Position;

public class LDR extends Instruction{
    
    public LDR(byte cond, boolean p, boolean u, boolean b, boolean w, boolean l, byte Rn, byte Rd, short immed, Position position){
	super(cond, position);
	super.storedValue |= 0b01 << 26;
	super.storedValue |= 0b0 << 25;
	super.storedValue |= (p ? 1 : 0) << 24;
	super.storedValue |= (u ? 1 : 0) << 23;
	super.storedValue |= (b ? 1 : 0) << 22;
	super.storedValue |= (w ? 1 : 0) << 21;
	super.storedValue |= (l ? 1 : 0) << 20;
	super.storedValue |= (Rn & 0xf) << 16;
	super.storedValue |= (Rd & 0xf) << 12;
	super.storedValue |= immed & 0xfff;
    }

    public LDR(byte cond, boolean p, boolean u, boolean b, boolean w, boolean l, byte Rn, byte Rd, byte shift, byte Rm, Position position){
	super(cond, position);
	super.storedValue |= 0b01 << 26;
	super.storedValue |= 0b1 << 25;
	super.storedValue |= (p ? 1 : 0) << 24;
	super.storedValue |= (u ? 1 : 0) << 23;
	super.storedValue |= (b ? 1 : 0) << 22;
	super.storedValue |= (w ? 1 : 0) << 21;
	super.storedValue |= (l ? 1 : 0) << 20;
	super.storedValue |= (Rn & 0xf) << 16;
	super.storedValue |= (Rd & 0xf) << 12;
	super.storedValue |= shift << 4;
	super.storedValue |= (Rm & 0xf);
    }

}
