package edu.depauw.emulator_ide.assembler.ast.op2;

import edu.depauw.emulator_ide.common.Position;

public class PoundExpression extends Op2{
	
	private final int immed;
	
	public PoundExpression(int immed, Position position) {
		super(position);
		this.immed = immed;
	}
	
	public long eval() {
		return immed;
	}
}
