package edu.depauw.emulator_ide.assembler.ast.instruction;

import edu.depauw.emulator_ide.common.Position;

public class MSRImmed extends Instruction{
    
    public MSRImmed(byte cond, boolean immed, boolean dest, byte Rm, Position position){
	super(cond, position);
	super.storedValue |= (immed ? 1 : 0) << 25;
	super.storedValue |= (dest ? 1 : 0) << 22;
	super.storedValue |= 0b1010011111 << 12;
	super.storedValue |= Rm & 0xf;
    }

}
