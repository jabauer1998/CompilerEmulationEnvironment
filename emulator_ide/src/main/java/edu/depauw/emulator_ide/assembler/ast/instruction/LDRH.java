package edu.depauw.emulator_ide.assembler.ast.instruction;

import edu.depauw.emulator_ide.common.Position;

public class LDRH extends Instruction{
    
    public LDRH(byte cond, boolean p, boolean u,  boolean w, boolean l, byte Rn, byte Rd, boolean s, boolean h, byte Rm, Position position){
	super(cond, position);
	super.storedValue |= (p ? 1 : 0) << 24;
	super.storedValue |= (u ? 1 : 0) << 23;
	super.storedValue |= (w ? 1 : 0) << 21;
	super.storedValue |= (l ? 1 : 0) << 20;
	super.storedValue |= (Rn & 0xf) << 16;
	super.storedValue |= (Rd & 0xf) << 12;
	super.storedValue |= 0b1 << 7;
	super.storedValue |= (s ? 1 : 0) << 6;
	super.storedValue |= (h ? 1 : 0) << 5;
	super.storedValue |= 0b1 << 4;
	super.storedValue |= Rm & 0xf;
    }

    public LDRH(byte cond, boolean p, boolean u,  boolean w, boolean l, byte Rn, byte Rd, byte offset, boolean s, boolean h, byte Rm, Position position){
	super(cond, position);
	super.storedValue |= (p ? 1 : 0) << 24;
	super.storedValue |= (u ? 1 : 0) << 23;
	super.storedValue |= 0b1 << 22;
	super.storedValue |= (w ? 1 : 0) << 21;
	super.storedValue |= (l ? 1 : 0) << 20;
	super.storedValue |= (Rn & 0xf) << 16;
	super.storedValue |= (Rd & 0xf) << 12;
	super.storedValue |= (Rn & 0xf) << 16;
	super.storedValue |= (offset & 0xf) << 8;
	super.storedValue |= 0b1 << 7;
	super.storedValue |= (s ? 1 : 0) << 6;
	super.storedValue |= (h ? 1 : 0) << 5;
	super.storedValue |= 0b1 << 4;
	super.storedValue |= Rm & 0xf;
    }

}
