package edu.depauw.emulator_ide.assembler.ast.instruction;

import edu.depauw.emulator_ide.assembler.Address.Address;
import edu.depauw.emulator_ide.common.Position;

public class LDRH extends Instruction{
    
    public LDRH(int cond, boolean l, int Rd, int sh, Address addr, Position position){
    	super(cond, position);
		super.storedValue |= (l ? 1 : 0) << 20;
		super.storedValue |= (Rd & 0xf) << 12;
		super.storedValue |= 0b1 << 7;
		super.storedValue |= sh  << 5;
		super.storedValue |= 0b1 << 4;
		super.storedValue |= addr.eval();
    }

}
