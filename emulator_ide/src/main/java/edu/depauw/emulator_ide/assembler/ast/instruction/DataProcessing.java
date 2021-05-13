package edu.depauw.emulator_ide.assembler.ast.instruction;

import edu.depauw.emulator_ide.assembler.ast.op2.Op2;
import edu.depauw.emulator_ide.assembler.ast.op2.PoundExpression;
import edu.depauw.emulator_ide.common.Position;

public class DataProcessing extends Instruction{
    
    public DataProcessing(int cond, int opCode, boolean condCode, int Rn, int Rd, Op2 op2, Position position){
		super(cond, position);
		super.storedValue |= (op2 instanceof PoundExpression ? 1 : 0) << 25;
		super.storedValue |= (opCode & 0xf) << 21;
		super.storedValue |= (condCode ? 1 : 0) << 20;
		super.storedValue |= (Rn & 0xf) << 16;
		super.storedValue |= (Rd & 0xf) << 12;
		super.storedValue |= op2.eval();
    }

}
