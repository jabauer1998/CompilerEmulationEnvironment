package edu.depauw.emulator_ide.assembler.Address;

import edu.depauw.emulator_ide.assembler.ast.op2.Op2;
import edu.depauw.emulator_ide.common.Position;

public class AddressPre extends Address{
	private final int Rn;
	private final boolean pm;
	private final boolean wb;
	private final Op2 op2;
	
	public AddressPre(int Rn, Position position) {
		super(position);
		this.Rn = Rn;
		this.pm = true;
		this.op2 = null;
		this.wb = false;
	}
	
	public AddressPre(int Rn, boolean pm, Op2 op2, boolean wb, Position position) {
		super(position);
		this.Rn = Rn;
		this.pm = pm;
		this.op2 = op2;
		this.wb = wb;
	}
	
	public long eval() {
		long res = (Rn << 16) | ((pm ? 1 : 0 ) << 23) | ((wb ? 1: 0) << 21);
		if(op2 != null) {
			res |= op2.eval();
		}
		return res;
	}
}
