package edu.depauw.emulator_ide.verilog_compiler.ast.expression;


import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.token.Token;

public class UnOp {

	public enum Type{ PLUS, MINUS, BNEG, LNEG, BAND, BNAND, BOR, BXOR, BXNOR }

	private Type type;

	public UnOp(Token op) {

		switch(op.getTokenType()){
			case PLUS:
				type = Type.PLUS;
				break;
			case MINUS:
				type = Type.MINUS;
				break;
			case BNEG:
				type = Type.BNEG;
				break;
			case LNEG:
				type = Type.LNEG;
				break;
			case BAND:
				type = Type.BAND;
				break;
			case BNAND:
				type = Type.BNAND;
				break;
			case BOR:
				type = Type.BOR;
				break;
			case BXOR:
				type = Type.BXOR;
				break;
			case BXNOR:
				type = Type.BXNOR;
				break;
		}

	}

	public Type getOpType(){ return type; }

	public static boolean isUnOp(Token.Type opType){

		if (opType == Token.Type.PLUS || opType == Token.Type.MINUS || opType == Token.Type.BNEG || opType == Token.Type.LNEG
			|| opType == Token.Type.BAND || opType == Token.Type.BOR || opType == Token.Type.BXOR || opType == Token.Type.BXNOR) {
			return true;
		} else {
			return false;
		}

	}
}
