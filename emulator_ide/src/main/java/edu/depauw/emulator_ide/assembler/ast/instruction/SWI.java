package edu.depauw.emulator_ide.assembler.ast.instruction;

import edu.depauw.emulator_ide.common.Position;

public class SWI extends Instruction{
    
    public SWI(byte cond, boolean b, byte Rn, byte Rd, byte Rm, Position position){
	super(cond, position);
	super.storedValue |= 0b1111 << 24;
    }
}
