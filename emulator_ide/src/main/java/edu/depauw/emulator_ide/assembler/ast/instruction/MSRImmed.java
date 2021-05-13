package edu.depauw.emulator_ide.assembler.ast.instruction;

import edu.depauw.emulator_ide.assembler.ast.op2.PoundExpression;
import edu.depauw.emulator_ide.common.Position;

public class MSRImmed extends Instruction{
    
    public MSRImmed(int cond, PoundExpression exp, Position position){
		super(cond, position);
		super.storedValue |= 1 << 25;
		super.storedValue |= 0b1010011111 << 12;
		super.storedValue |= exp.eval() & 0xf;
    }

}
