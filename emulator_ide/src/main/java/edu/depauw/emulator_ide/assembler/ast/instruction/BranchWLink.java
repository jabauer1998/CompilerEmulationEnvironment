package edu.depauw.emulator_ide.assembler.ast.instruction;

import edu.depauw.emulator_ide.common.Position;

public abstract class BranchWLink extends Instruction{
    
    protected BranchWLink(byte cond, boolean link, int offset, Position position){
	super(cond, position);
	super.storedValue |= 0b101 << 25;
	super.storedValue |= (link ? 1 : 0) << 24;
	super.storedValue |= (offset & 0xffffff);
    }

}
