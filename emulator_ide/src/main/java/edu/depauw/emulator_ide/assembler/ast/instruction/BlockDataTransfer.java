package edu.depauw.emulator_ide.assembler.ast.instruction;

import edu.depauw.emulator_ide.common.Position;

public class BlockDataTransfer extends Instruction{
    
    public BlockDataTransfer(byte cond, boolean p, boolean u, boolean s, boolean w, boolean l, byte Rn, short registerList, Position position){
	super(cond, position);
	super.storedValue |= 0b100 << 25;
	super.storedValue |= (p ? 1 : 0) << 24;
	super.storedValue |= (u ? 1 : 0) << 23;
	super.storedValue |= (s ? 1 : 0) << 22;
	super.storedValue |= (w ? 1 : 0) << 21;
	super.storedValue |= (l ? 1 : 0) << 20;
	super.storedValue |= (Rn & 0xf) << 16;
	super.storedValue |= registerList & 0xffff;
    }
}
