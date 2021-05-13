package edu.depauw.emulator_ide.assembler.Address;

import edu.depauw.emulator_ide.assembler.ast.op2.Op2;
import edu.depauw.emulator_ide.common.Position;

public class AddressPost extends Address{
	private final int Rn;
	private final Op2 op2;
	private final boolean pm;
	
	public AddressPost(int Rn, Position position) {
		super(position);
		this.Rn = Rn;
		this.pm = true;
		this.op2 = null;
	}
	
	public AddressPost(int Rn, boolean pm, Op2 op2, Position position) {
		super(position);
		this.Rn = Rn;
		this.op2 = op2;
		this.pm = pm;
	}
	
	public long eval() {
		long res = (Rn << 16) | ((pm ? 1 : 0) << 23);
		if(op2 != null) {
			res |= op2.eval();
		}
		return res;
	}
}
