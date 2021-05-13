package edu.depauw.emulator_ide.assembler.ast.instruction;

import edu.depauw.emulator_ide.assembler.Address.Address;
import edu.depauw.emulator_ide.common.Position;

public class LDR extends Instruction{
    
    public LDR(int cond, boolean p, boolean b, boolean l, Address addr, Position position){
	super(cond, position);
	super.storedValue |= 0b01 << 26;
	super.storedValue |= 0b0 << 25;
	super.storedValue |= (p ? 1 : 0) << 24;
	super.storedValue |= (b ? 1 : 0) << 22;
	super.storedValue |= (l ? 1 : 0) << 20;
	super.storedValue |= addr.eval();
    }

}
