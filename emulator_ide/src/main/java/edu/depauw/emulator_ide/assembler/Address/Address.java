package edu.depauw.emulator_ide.assembler.Address;

import edu.depauw.emulator_ide.common.Position;

public abstract class Address {
	Position position;
	
	protected Address(Position position) {
		this.position = position;
	}
	
	public abstract long eval();
}
