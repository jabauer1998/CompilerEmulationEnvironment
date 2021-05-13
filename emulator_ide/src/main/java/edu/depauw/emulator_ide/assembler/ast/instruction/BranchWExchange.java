package edu.depauw.emulator_ide.assembler.ast.instruction;

import edu.depauw.emulator_ide.common.Position;

public class BranchWExchange extends Instruction{
    
    public BranchWExchange(int cond, int Rn, Position position){
		super(cond, position);
		super.storedValue |= 0b000100101111111111110001 << 4;
		super.storedValue |= Rn & 0Xf;
    }

}
