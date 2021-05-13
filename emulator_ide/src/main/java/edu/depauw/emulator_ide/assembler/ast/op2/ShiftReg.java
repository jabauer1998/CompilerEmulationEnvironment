package edu.depauw.emulator_ide.assembler.ast.op2;

import edu.depauw.emulator_ide.common.Position;

public class ShiftReg extends Shift{
	private final int shiftType;
	private final int register;
	
	public ShiftReg(int Rm, int shiftType, int register, Position position) {
		super(Rm, position);
		this.shiftType = shiftType;
		this.register = register;
	}
	
	public long eval() {
		return (register << 8) | (1 << 4) | (shiftType << 5) | super.eval();
	}
}
