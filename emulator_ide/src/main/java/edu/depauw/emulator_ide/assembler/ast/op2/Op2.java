package edu.depauw.emulator_ide.assembler.ast.op2;

import edu.depauw.emulator_ide.common.Position;

public abstract class Op2 {
	
	private final Position position;
	
	protected Op2(Position position) {
		this.position = position;
	}
	
	public abstract long eval();
	
}
