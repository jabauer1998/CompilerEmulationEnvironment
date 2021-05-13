package edu.depauw.emulator_ide.assembler.ast.instruction;

import edu.depauw.emulator_ide.common.Position;

public class Stop extends Instruction{

	protected Stop(byte cond, Position position) {
		super(cond, position);
		// TODO Auto-generated constructor stub
	}
	
	public Stop(Position pos) {
		super((byte) 0, pos);
	}
	
	
	
}
