package edu.depauw.emulator_ide.assembler.ast.op2;

import edu.depauw.emulator_ide.common.Position;

public class ShiftExpr extends Shift{
	
	
	private final int shiftType;
	private final PoundExpression expr;
	
	public ShiftExpr(int Rm, int shiftType, PoundExpression expr, Position position) {
		super(Rm, position);
		this.shiftType = shiftType;
		this.expr = expr;
	}
	
	public long eval() {
		return (expr.eval() << 7) | (shiftType << 5) | super.eval(); 
	}
}
