package edu.depauw.emulator_ide.assembler.ast.instruction;

import edu.depauw.emulator_ide.common.Position;

public class MSRreg extends Instruction{
    
    public MSRreg(int cond, int Rm, Position position){
		super(cond, position);
		super.storedValue |= 0b00010 << 23;
		super.storedValue |= 0b1010011111 << 12;
		super.storedValue |= Rm & 0xf;
    }

}
