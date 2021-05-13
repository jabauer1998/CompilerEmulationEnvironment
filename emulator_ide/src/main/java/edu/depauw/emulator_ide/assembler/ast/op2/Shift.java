package edu.depauw.emulator_ide.assembler.ast.op2;

import edu.depauw.emulator_ide.common.Position;

public class Shift extends Op2{
	private final int Rm;
	
	public Shift(int Rm, Position position){
		super(position);
		this.Rm = Rm;
	}
	
	public long eval() {
		return Rm;
	}
}
