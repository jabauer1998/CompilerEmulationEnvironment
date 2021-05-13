package edu.depauw.emulator_ide.assembler.Address;

import edu.depauw.emulator_ide.common.Position;

public class AddressExpr extends Address{
	private int exprResult;
	public AddressExpr(int exprResult, Position position) {
		super(position);
	}
	
	public long eval() {
		return  (1 << 22) | exprResult;
	}
}
